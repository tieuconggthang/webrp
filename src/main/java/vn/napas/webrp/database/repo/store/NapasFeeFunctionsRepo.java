package vn.napas.webrp.database.repo.store;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;

@Repository
public class NapasFeeFunctionsRepo {

    private final NamedParameterJdbcTemplate jdbc;

    public NapasFeeFunctionsRepo(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /* =========================================================
     * 1) CREATE_PCODE -> createPcode(String bankId, String pan)
     *    - PCODE72(BANKID, PRANGE)
     *    - Trả "00" nếu tồn tại đúng 1 cấu hình khớp 6 số đầu PAN
     * ========================================================= */
    public String createPcode(String bankId, String pan) {
        String sql = """
            SELECT COUNT(*) 
            FROM PCODE72
            WHERE TRIM(BANKID) = TRIM(:bankId)
              AND PRANGE LIKE CONCAT('%', LEFT(TRIM(:pan), 6), '%')
            """;
        try {
            Integer cnt = jdbc.queryForObject(sql, Map.of("bankId", bankId, "pan", pan), Integer.class);
            return (cnt != null && cnt == 1) ? "00" : "20";
        } catch (Exception ex) {
            // Theo function gốc: mọi lỗi đều return "00"
            return "00";
        }
    }

    /* =========================================================
     * 2) NAPAS_GET_FEE_KEY -> getFeeKey(...)
     *    Bảng: GR_FEE_CONFIG_NEW
     *    Điều kiện:
     *     - I_SETT_DATE giữa VALID_FROM..VALID_TO (so theo DATE)
     *     - (ISSUER=0 hoặc = I_ISSUER) AND (ACQUIRER=0 hoặc = I_ACQUIRER)
     *     - PRO_CODE = I_PRO_CODE
     *     - MERCHANT_TYPE = I_MERCHANT
     *     - CURRENCY_CODE theo Decode(I_CURRENCY,null,704,840,840,418,418,704)
     *    Sắp xếp ưu tiên (ORDER_CONFIG):
     *      When ISSUER=I_ISSUER AND ACQUIRER=I_ACQUIRER -> 1
     *      When ISSUER=I_ISSUER AND ACQUIRER=0        -> 3
     *      When ISSUER=0        AND ACQUIRER=I_ACQUIRER -> 3
     *      When (ISSUER xor ACQUIRER) match khác phía -> 5 + ORDER_CONFIG
     *      Else                                        -> 10
     *    - Nếu không có bản ghi: "ERR - NOT FOUND -3"
     *    - Nếu có >1 bản ghi ở "rank=1": "ERR - Many Fee Config"
     *    - Nếu có 0 ở rank=1: "ERR - Not Found Config"
     *    - Ngược lại trả FEE_KEY
     * ========================================================= */
    public String getFeeKey(int acquirer, int issuer, String proCode, int merchantType,
                            Integer currencyNullable, LocalDate settDate, long otrace) {
        int currency = decodeCurrency(currencyNullable);

        // Kiểm tra tồn tại bất kỳ cấu hình hợp lệ
        String existSql = """
            SELECT COUNT(*) 
            FROM GR_FEE_CONFIG_NEW G
            WHERE DATE(:sett) BETWEEN DATE(G.VALID_FROM) AND DATE(G.VALID_TO)
              AND (G.ISSUER = 0 OR G.ISSUER = :issuer)
              AND (G.ACQUIRER = 0 OR G.ACQUIRER = :acquirer)
              AND G.PRO_CODE = :pro
              AND G.MERCHANT_TYPE = :merchant
              AND G.CURRENCY_CODE = :currency
              AND G.ACTIVE = 'Y'
            """;
        Map<String, Object> p = new HashMap<>();
        p.put("sett", settDate);
        p.put("issuer", issuer);
        p.put("acquirer", acquirer);
        p.put("pro", proCode);
        p.put("merchant", merchantType);
        p.put("currency", currency);

        Integer exist = jdbc.queryForObject(existSql, p, Integer.class);
        if (exist == null || exist == 0) {
            return "ERR - NOT FOUND -3";
        }

        // Lấy danh sách ứng viên rank=1 (tính order_weight như CASE trong Oracle)
        String candidatesSql = """
            SELECT 
              G.FEE_KEY,
              CASE 
                WHEN G.ISSUER = :issuer AND G.ACQUIRER = :acquirer THEN 1
                WHEN G.ISSUER = :issuer AND G.ACQUIRER = 0         THEN 3
                WHEN G.ISSUER = 0        AND G.ACQUIRER = :acquirer THEN 3
                WHEN G.ISSUER = :issuer  AND G.ACQUIRER <> :acquirer THEN 5 + IFNULL(G.ORDER_CONFIG,0)
                WHEN G.ISSUER <> :issuer AND G.ACQUIRER = :acquirer THEN 5 + IFNULL(G.ORDER_CONFIG,0)
                ELSE 10
              END AS order_weight
            FROM GR_FEE_CONFIG_NEW G
            WHERE DATE(:sett) BETWEEN DATE(G.VALID_FROM) AND DATE(G.VALID_TO)
              AND (G.ISSUER = 0 OR G.ISSUER = :issuer)
              AND (G.ACQUIRER = 0 OR G.ACQUIRER = :acquirer)
              AND G.PRO_CODE = :pro
              AND G.MERCHANT_TYPE = :merchant
              AND G.CURRENCY_CODE = :currency
              AND G.ACTIVE = 'Y'
            ORDER BY order_weight ASC, IFNULL(G.ORDER_CONFIG,0) ASC, G.FEE_KEY
            """;

        var candidateKeys = jdbc.query(candidatesSql, p, (rs, rowNum) -> new Candidate(
            rs.getString("FEE_KEY"),
            rs.getInt("order_weight")
        ));

        if (candidateKeys.isEmpty()) return "ERR - Not Found Config";

        // Lấy mức weight thấp nhất
        int bestWeight = candidateKeys.get(0).orderWeight;
        long numBest = candidateKeys.stream().filter(c -> c.orderWeight == bestWeight).count();

        if (numBest > 1) return "ERR - Many Fee Config";
        if (numBest == 0) return "ERR - Not Found Config";
        return candidateKeys.get(0).feeKey;
    }

    private static class Candidate {
        final String feeKey;
        final int orderWeight;
        Candidate(String feeKey, int orderWeight) {
            this.feeKey = feeKey;
            this.orderWeight = orderWeight;
        }
    }

    /* =========================================================
     * 3) OLD_FINAL_FEE_CAL_VER -> oldFinalFeeCalVer(...)
     *     Lấy 1 dòng theo FEE_KEY và tính toán theo nhánh TYPE
     *     - Nếu (FEE_FOR in ('PAY','REC') && không phải 2 ngân hàng ngoại):
     *         trả null (theo function gốc), nhưng cuối cùng ép 0 nếu null.
     *     - Các nhánh 'PHAN_TRAM', 'PHANG_PHAN_TRAM', 'PHANG_TG_PHAN_TRAM',
     *       'CBFT', 'PHANG_TYGIA'... áp dụng đúng như Oracle
     * ========================================================= */
    public BigDecimal oldFinalFeeCalVer(
            String feeFor,
            String feeKey,
            int acquirer,
            int issuer,
            String proCode,
            Integer currencyNullable,
            BigDecimal amount,
            BigDecimal conrate,
            long otrace
    ) {
        // early skip theo rule PAY/REC + ko phải 2 bank ngoại
        if ((equalsAny(feeFor, "PAY", "REC")) && !equalsAny(acquirer, 602907, 605609) && !equalsAny(issuer, 602907, 605609)) {
            return BigDecimal.ZERO; // function gốc trả null, sau cùng ép 0
        }

        var row = findConfigByFeeKey(feeKey);
        if (row == null) {
            // như function gốc: log và return null -> mình trả 0
            logErr( "TRACE:" + otrace + "-NO_DATA_FOUND", "FINAL_FEE_CAL_VER");
            return BigDecimal.ZERO;
        }

        // Map cột ra biến
        String feeIssType      = nv(row.feeIssType);
        String feeAcqType      = nv(row.feeAcqType);
        BigDecimal feeIss      = n0(row.feeIss);
        BigDecimal feeAcq      = n0(row.feeAcq);
        BigDecimal lowBound    = n0(row.lowBound);
        BigDecimal upBound     = n0(row.upBound);
        BigDecimal payAt       = n0(row.feePayAt);
        BigDecimal recAt       = n0(row.feeRecAt);
        String feePayType      = nv(row.feePayType);
        String feeRecType      = nv(row.feeRecType);
        BigDecimal feeIssExt   = n0(row.feeIssExt);
        BigDecimal feeAcqExt   = n0(row.feeAcqExt);
        BigDecimal payAtExt    = n0(row.feePayAtExt);
        BigDecimal recAtExt    = n0(row.feeRecAtExt);

        BigDecimal result;
        switch (feeFor) {
            case "ISS" -> result = feeIss;
            case "ACQ" -> result = feeAcq;
            case "PAY" -> result = payAt;
            case "REC" -> result = recAt;
            default    -> result = BigDecimal.ZERO;
        }

        // Các nhánh tính toán
        if ("ISS".equals(feeFor) && "PHAN_TRAM".equalsIgnoreCase(feeIssType)) {
            result = pct(amount, result);
            result = clamp(result, lowBound, upBound);
        } else if ("ACQ".equals(feeFor) && "PHAN_TRAM".equalsIgnoreCase(feeAcqType)) {
            result = pct(amount, result);
            result = clamp(result, lowBound, upBound);
        } else if ("PAY".equals(feeFor) && "PHAN_TRAM".equalsIgnoreCase(feePayType)) {
            result = pct(amount, result);
        } else if ("REC".equals(feeFor) && "PHAN_TRAM".equalsIgnoreCase(feeRecType)) {
            result = pct(amount, result);
        } else if ("ISS".equals(feeFor) && "PHANG_PHAN_TRAM".equalsIgnoreCase(feeIssType)) {
            result = pct(amount, result).add(feeIssExt);
            result = clamp(result, lowBound, upBound);
        } else if ("ACQ".equals(feeFor) && "PHANG_PHAN_TRAM".equalsIgnoreCase(feeAcqType)) {
            result = pct(amount, result).add(feeAcqExt);
            result = clamp(result, lowBound, upBound);
        } else if ("ISS".equals(feeFor) && "PHANG_TG_PHAN_TRAM".equalsIgnoreCase(feeIssType)) {
            // (Ngoại tệ * Tỷ giá) + phí phẳng
            result = feeIss.multiply(n0(conrate)).add(feeIssExt);
            result = clamp(result, lowBound, upBound);
        } else if ("ACQ".equals(feeFor) && "PHANG_TG_PHAN_TRAM".equalsIgnoreCase(feeAcqType)) {
            result = feeAcq.multiply(n0(conrate)).add(feeAcqExt);
            result = clamp(result, lowBound, upBound);
        } else if ("ISS".equals(feeFor) && "CBFT".equalsIgnoreCase(feeIssType)) {
            // (feeIss * conrate + ((feeIssExt * amount)/100))/1.1
            BigDecimal term1 = feeIss.multiply(n0(conrate));
            BigDecimal term2 = feeIssExt.multiply(amount).divide(BigDecimal.valueOf(100));
            result = term1.add(term2).divide(BigDecimal.valueOf(1.1), 8, BigDecimal.ROUND_HALF_UP);
            result = clamp(result, lowBound, upBound);
        } else if ("ACQ".equals(feeFor) && !"840".equals(String.valueOf(currencyNullable)) 
                   && conrate != null && "PHANG_TYGIA".equalsIgnoreCase(feeAcqType)) {
            result = result.multiply(conrate);
        } else if ("ISS".equals(feeFor) && !"840".equals(String.valueOf(currencyNullable)) 
                   && conrate != null && "PHANG_TYGIA".equalsIgnoreCase(feeIssType)) {
            result = result.multiply(conrate);
        }

        // Chuẩn hóa theo function: null/0 -> 0
        if (result == null) result = BigDecimal.ZERO;
        if (result.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        return result;
    }

    /* =========================================================
     * 4) NAPAS_FEE_OLD_TRAN -> napasFeeOldTran(...)
     *     Lấy dòng theo FEE_KEY và tính các biến thể:
     *     - Hỗ trợ: ISS/ACQ/PAY/REC, IRF_ISS/SVF_ISS, IRF_ACQ/SVF_ACQ, IRF_BEN/SVF_BEN
     *     - MSGTYPE=430: chỉ hoàn phần % của IRF_* theo TYPE=PHAN_TRAM, còn lại = 0
     * ========================================================= */
    public BigDecimal napasFeeOldTran(
            int msgType,
            String feeFor,
            String feeKey,
            int acquirer,
            int issuer,
            Integer currencyNullable,
            BigDecimal amount,
            BigDecimal conrate,
            long otrace
    ) {
        // early skip giống function gốc
        if ((equalsAny(feeFor, "PAY", "REC")) && !equalsAny(acquirer, 602907, 605609) && !equalsAny(issuer, 602907, 605609)) {
            return BigDecimal.ZERO;
        }

        var row = findConfigByFeeKeyOldTran(feeKey);
        if (row == null) {
            logErr( "TRACE:" + otrace + "-NO_DATA_FOUND", "NAPAS_FEE_OLD_TRAN");
            return BigDecimal.ZERO;
        }

        // Đưa về biến
        String feeIssType      = nv(row.feeIssTypeNew);
        String feeAcqType      = nv(row.feeAcqType);
        BigDecimal feeIss      = n0(row.feeIss);
        BigDecimal feeAcq      = n0(row.feeAcq);
        BigDecimal lowBound    = n0(row.lowBound);
        BigDecimal upBound     = n0(row.upBound);
        BigDecimal payAt       = n0(row.feePayAt);
        BigDecimal recAt       = n0(row.feeRecAt);
        String feePayType      = nv(row.feePayType);
        String feeRecType      = nv(row.feeRecType);
        BigDecimal feeIssExt   = n0(row.feeIssExt);
        BigDecimal feeAcqExt   = n0(row.feeAcqExt);
        BigDecimal payAtExt    = n0(row.feePayAtExt);
        BigDecimal recAtExt    = n0(row.feeRecAtExt);

        BigDecimal irfIss      = n0(row.feeIrfIss);
        BigDecimal svfIss      = n0(row.feeSvfIss);
        BigDecimal irfAcq      = n0(row.feeIrfAcq);
        BigDecimal svfAcq      = n0(row.feeSvfAcq);
        BigDecimal irfBen      = n0(row.feeIrfBen);
        BigDecimal svfBen      = n0(row.feeSvfBen);
        BigDecimal benExt      = n0(row.feeBenExt);
        String feeBenType      = nv(row.feeBenType);

        BigDecimal result = switch (feeFor) {
            case "ISS"     -> feeIss;
            case "IRF_ISS" -> irfIss;
            case "SVF_ISS" -> svfIss;
            case "IRF_ACQ" -> irfAcq;
            case "SVF_ACQ" -> svfAcq;
            case "IRF_BEN" -> irfBen;
            case "SVF_BEN" -> svfBen;
            case "ACQ"     -> feeAcq;
            case "PAY"     -> payAt;
            case "REC"     -> recAt;
            default        -> BigDecimal.ZERO;
        };

        // MSGTYPE = 430 (reversal/hoàn?)
        if (msgType == 430) {
            if (equalsAny(feeFor, "SVF_ISS", "SVF_ACQ", "SVF_BEN")) {
                result = BigDecimal.ZERO;
            } else if ("IRF_ISS".equals(feeFor) && "PHAN_TRAM".equalsIgnoreCase(feeIssType)) {
                result = pct(amount, result);
            } else if ("IRF_ACQ".equals(feeFor) && "PHAN_TRAM".equalsIgnoreCase(feeAcqType)) {
                result = pct(amount, result);
            } else {
                result = BigDecimal.ZERO; // fee phẳng không hoàn
            }
            return nz(result);
        }

        // MSGTYPE != 430: áp dụng full rule
        if (equalsAny(feeFor, "ISS", "IRF_ISS", "SVF_ISS") && "PHAN_TRAM".equalsIgnoreCase(feeIssType)) {
            result = pct(amount, result);
            // riêng SVF_ISS một số FEE_KEY ép min âm => domain đặc thù, bạn thêm nếu cần
        } else if (equalsAny(feeFor, "ACQ", "IRF_ACQ", "SVF_ACQ") && "PHAN_TRAM".equalsIgnoreCase(feeAcqType)) {
            result = pct(amount, result);
        } else if (equalsAny(feeFor, "PAY", "REC") && "PHAN_TRAM".equalsIgnoreCase(feePayType)) {
            result = pct(amount, result);
        } else if (equalsAny(feeFor, "ISS") && "PHANG_PHAN_TRAM".equalsIgnoreCase(feeIssType)) {
            result = pct(amount, result).add(feeIssExt);
        } else if (equalsAny(feeFor, "SVF_ACQ") && "PHANG_PHAN_TRAM".equalsIgnoreCase(feeAcqType)) {
            result = pct(amount, result).add(feeAcqExt);
        } else if (equalsAny(feeFor, "SVF_ACQ") && "PHANG_PHAN_TRAM_USD".equalsIgnoreCase(feeAcqType)) {
            // + O_FEE_ACQ_EXT / CONRATE
            result = pct(amount, result).add(divSafe(feeAcqExt, n0(conrate)));
        } else if (equalsAny(feeFor, "ISS") && "PHANG_TG_PHAN_TRAM".equalsIgnoreCase(feeIssType)) {
            result = svfOrIssSpecial(feeFor, feeKey, svfIss, conrate, feeIssExt);
        } else if (equalsAny(feeFor, "SVF_ISS") && "PHANG_TG_PHAN_TRAM".equalsIgnoreCase(feeIssType)) {
            // nếu feeKey thuộc list đặc biệt -> dùng SVF_ISS thuần, else SVF_ISS*conrate + feeIssExt
            if (isSpecialSvfIssKey(feeKey)) {
                result = svfIss;
            } else {
                result = svfIss.multiply(n0(conrate)).add(feeIssExt);
            }
        } else if (equalsAny(feeFor, "ACQ") && "PHANG_TG_PHAN_TRAM".equalsIgnoreCase(feeAcqType)) {
            result = feeAcq.multiply(n0(conrate)).add(feeAcqExt);
        } else if (equalsAny(feeFor, "ISS") && "CBFT".equalsIgnoreCase(feeIssType)) {
            result = feeIss.multiply(n0(conrate)).add(feeIssExt.multiply(amount));
        } else if (equalsAny(feeFor, "IRF_ISS") && "CBFT".equalsIgnoreCase(feeIssType)) {
            result = irfIss.multiply(n0(conrate)).add(feeIssExt.multiply(amount));
        } else if (equalsAny(feeFor, "IRF_BEN") && "CBFT".equalsIgnoreCase(feeBenType)) {
            result = irfBen.multiply(n0(conrate)).add(benExt.multiply(amount));
        } else if (equalsAny(feeFor, "IRF_ISS") && "CBFT_V2".equalsIgnoreCase(feeIssType)) {
            result = irfIss.multiply(amount).add(feeIssExt);
        } else if (equalsAny(feeFor, "IRF_BEN") && "CBFT_V2".equalsIgnoreCase(feeBenType)) {
            result = irfBen.multiply(amount).add(benExt);
        } else if ("IRF_BEN".equals(feeFor) && "PHAN_TRAM".equalsIgnoreCase(feeBenType)) {
            result = pct(amount, result);
        } else if ("SVF_BEN".equals(feeFor) && "PHAN_TRAM".equalsIgnoreCase(feeBenType)) {
            result = pct(amount, result);
        } else if (equalsAny(feeFor, "ACQ", "IRF_ACQ", "SVF_ACQ")
                && !Objects.equals(currencyNullable, 840)
                && conrate != null
                && "PHANG_TYGIA".equalsIgnoreCase(feeAcqType)) {
            result = result.multiply(conrate);
        } else if (equalsAny(feeFor, "ISS", "IRF_ISS", "SVF_ISS")
                && !Objects.equals(currencyNullable, 840)
                && conrate != null
                && "PHANG_TYGIA".equalsIgnoreCase(feeIssType)) {
            if (isSpecialSvfIssKey(feeKey) && "SVF_ISS".equals(feeFor)) {
                result = svfIss;
            } else {
                result = result.multiply(conrate);
            }
        }

        return nz(result);
    }

    /* =========================================================
     * 5) NAPAS_GET_FEE_NAME -> getFeeName(feeKey)
     * ========================================================= */
    public String getFeeName(String feeKey) {
        String sql = """
            SELECT FEE_NOTE 
            FROM GR_FEE_CONFIG_NEW 
            WHERE TRIM(FEE_KEY) = TRIM(:key)
            """;
        try {
            return jdbc.queryForObject(sql, Map.of("key", feeKey), String.class);
        } catch (EmptyResultDataAccessException e) {
            return "ERR - NO_DATA_FOUND Fee_Note";
        } catch (Exception e) {
            return "ERR - OTHERS Fee_Note";
        }
    }

    /* ================== Helpers & Mappers ================== */

    private static boolean equalsAny(String s, String... arr) {
        for (String a : arr) if (a.equalsIgnoreCase(s)) return true;
        return false;
    }
    private static boolean equalsAny(int v, int... arr) {
        for (int a : arr) if (a == v) return true;
        return false;
    }

    private static BigDecimal pct(BigDecimal amount, BigDecimal rate) {
        if (amount == null || rate == null) return BigDecimal.ZERO;
        return amount.multiply(rate);
    }

    private static BigDecimal clamp(BigDecimal val, BigDecimal low, BigDecimal up) {
        if (val == null) return BigDecimal.ZERO;
        BigDecimal r = val;
        if (up != null && up.signum() != 0 && r.compareTo(up) > 0) r = up;
        if (low != null && low.signum() != 0 && r.compareTo(low) < 0) r = low;
        return r;
    }

    private static BigDecimal nz(BigDecimal v) {
        return (v == null) ? BigDecimal.ZERO : v;
    }
    private static BigDecimal n0(BigDecimal v) {
        return (v == null) ? BigDecimal.ZERO : v;
    }
    private static String nv(String s) {
        return (s == null) ? "" : s;
    }
    private static BigDecimal divSafe(BigDecimal a, BigDecimal b) {
        if (a == null || b == null || BigDecimal.ZERO.compareTo(b) == 0) return BigDecimal.ZERO;
        return a.divide(b, 8, BigDecimal.ROUND_HALF_UP);
        // NOTE: dùng RoundingMode nếu Java 9+: RoundingMode.HALF_UP
    }

    private boolean isSpecialSvfIssKey(String feeKey) {
        // Danh sách đặc thù theo code gốc (có thể mở rộng cấu hình DB):
        return equalsAny(feeKey,
                "GDAC1C26-TDV9-4EBC-0ED9-IJ3C6EC0A4RF",
                "71U283CB-AN41-4K15-9D15-C57802D5CEBC",
                "46206160-7462-46C0-81D0-6DA81C367958",
                "8B6746E8-4A7A-4744-BBD5-FDE50E48E27A",
                "C79F33FB-259C-491A-9699-1A5B5D6CFAC2",
                "4DC905F2-C4C2-4906-AEB3-E88C1033C762",
                "A300C4EB-E648-4F98-99FC-9C11BFACE6D6",
                "A775AB95-CF99-411E-83CE-F42E5AB4F61B"
        );
    }

    private int decodeCurrency(Integer iCurrency) {
        // Decode(I_CURRENCY,null,704,840,840,418,418,704)
        if (iCurrency == null) return 704;
        if (iCurrency == 840) return 840;
        if (iCurrency == 418) return 418;
        return 704;
    }

    private void logErr(String detail, String module) {
        // Bản gốc ghi ERR_EX(ERR_TIME, ERR_CODE, ERR_DETAIL, ERR_MODULE, CRITICAL)
        String sql = """
            INSERT INTO ERR_EX(ERR_TIME, ERR_CODE, ERR_DETAIL, ERR_MODULE, CRITICAL)
            VALUES (NOW(), 'E', :detail, :module, 2)
            """;
        try {
            jdbc.update(sql, Map.of("detail", detail, "module", module));
        } catch (Exception ignore) {
            // không throw để không chặn luồng tính phí
        }
    }

    /* ----- GR_FEE_CONFIG_NEW (subset) mappers ----- */

    private static class FeeConfigBasic {
        String  feeIssType;
        String  feeAcqType;
        BigDecimal feeIss;
        BigDecimal feeAcq;
        BigDecimal lowBound;
        BigDecimal upBound;
        BigDecimal feePayAt;
        BigDecimal feeRecAt;
        String  feePayType;
        String  feeRecType;
        BigDecimal feeIssExt;
        BigDecimal feeAcqExt;
        BigDecimal feePayAtExt;
        BigDecimal feeRecAtExt;
    }

    private static class FeeConfigOldTran {
        String  feeIssTypeNew;
        String  feeAcqType;
        BigDecimal feeIss;
        BigDecimal feeAcq;
        BigDecimal lowBound;
        BigDecimal upBound;
        BigDecimal feePayAt;
        BigDecimal feeRecAt;
        String  feePayType;
        String  feeRecType;
        BigDecimal feeIssExt;
        BigDecimal feeAcqExt;
        BigDecimal feePayAtExt;
        BigDecimal feeRecAtExt;

        BigDecimal feeIrfIss;
        BigDecimal feeSvfIss;
        BigDecimal feeIrfAcq;
        BigDecimal feeSvfAcq;
        BigDecimal feeIrfBen;
        BigDecimal feeSvfBen;
        BigDecimal feeBenExt;
        String  feeBenType;
    }

    private FeeConfigBasic findConfigByFeeKey(String feeKey) {
        String sql = """
            SELECT 
              FEE_ISS_TYPE,
              FEE_ACQ_TYPE,
              IFNULL(FEE_ISS,0)           AS FEE_ISS,
              IFNULL(FEE_ACQ,0)           AS FEE_ACQ,
              IFNULL(LOW_BOUND,0)         AS LOW_BOUND,
              IFNULL(UP_BOUND,0)          AS UP_BOUND,
              IFNULL(FEE_PAY_AT,0)        AS FEE_PAY_AT,
              IFNULL(FEE_REC_AT,0)        AS FEE_REC_AT,
              IFNULL(FEE_PAY_TYPE,'')     AS FEE_PAY_TYPE,
              IFNULL(FEE_REC_TYPE,'')     AS FEE_REC_TYPE,
              IFNULL(FEE_ISS_EXT,0)       AS FEE_ISS_EXT,
              IFNULL(FEE_ACQ_EXT,0)       AS FEE_ACQ_EXT,
              IFNULL(FEE_PAY_AT_EXT,0)    AS FEE_PAY_AT_EXT,
              IFNULL(FEE_REC_AT_EXT,0)    AS FEE_REC_AT_EXT
            FROM GR_FEE_CONFIG_NEW
            WHERE TRIM(FEE_KEY) = TRIM(:key)
            """;
        try {
            return jdbc.queryForObject(sql, Map.of("key", feeKey), (rs, rn) -> {
                FeeConfigBasic f = new FeeConfigBasic();
                f.feeIssType    = rs.getString("FEE_ISS_TYPE");
                f.feeAcqType    = rs.getString("FEE_ACQ_TYPE");
                f.feeIss        = rs.getBigDecimal("FEE_ISS");
                f.feeAcq        = rs.getBigDecimal("FEE_ACQ");
                f.lowBound      = rs.getBigDecimal("LOW_BOUND");
                f.upBound       = rs.getBigDecimal("UP_BOUND");
                f.feePayAt      = rs.getBigDecimal("FEE_PAY_AT");
                f.feeRecAt      = rs.getBigDecimal("FEE_REC_AT");
                f.feePayType    = rs.getString("FEE_PAY_TYPE");
                f.feeRecType    = rs.getString("FEE_REC_TYPE");
                f.feeIssExt     = rs.getBigDecimal("FEE_ISS_EXT");
                f.feeAcqExt     = rs.getBigDecimal("FEE_ACQ_EXT");
                f.feePayAtExt   = rs.getBigDecimal("FEE_PAY_AT_EXT");
                f.feeRecAtExt   = rs.getBigDecimal("FEE_REC_AT_EXT");
                return f;
            });
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    private FeeConfigOldTran findConfigByFeeKeyOldTran(String feeKey) {
        String sql = """
            SELECT 
              IFNULL(FEE_ISS_TYPE_NEW,'')  AS FEE_ISS_TYPE_NEW,
              IFNULL(FEE_ACQ_TYPE,'')      AS FEE_ACQ_TYPE,
              IFNULL(FEE_ISS,0)            AS FEE_ISS,
              IFNULL(FEE_ACQ,0)            AS FEE_ACQ,
              IFNULL(LOW_BOUND,0)          AS LOW_BOUND,
              IFNULL(UP_BOUND,0)           AS UP_BOUND,
              IFNULL(FEE_PAY_AT,0)         AS FEE_PAY_AT,
              IFNULL(FEE_REC_AT,0)         AS FEE_REC_AT,
              IFNULL(FEE_PAY_TYPE,'')      AS FEE_PAY_TYPE,
              IFNULL(FEE_REC_TYPE,'')      AS FEE_REC_TYPE,
              IFNULL(FEE_ISS_EXT,0)        AS FEE_ISS_EXT,
              IFNULL(FEE_ACQ_EXT,0)        AS FEE_ACQ_EXT,
              IFNULL(FEE_PAY_AT_EXT,0)     AS FEE_PAY_AT_EXT,
              IFNULL(FEE_REC_AT_EXT,0)     AS FEE_REC_AT_EXT,
              IFNULL(FEE_IRF_ISS,0)        AS FEE_IRF_ISS,
              IFNULL(FEE_SVF_ISS,0)        AS FEE_SVF_ISS,
              IFNULL(FEE_IRF_ACQ,0)        AS FEE_IRF_ACQ,
              IFNULL(FEE_SVF_ACQ,0)        AS FEE_SVF_ACQ,
              IFNULL(FEE_IRF_BEN,0)        AS FEE_IRF_BEN,
              IFNULL(FEE_SVF_BEN,0)        AS FEE_SVF_BEN,
              IFNULL(FEE_BEN_EXT,0)        AS FEE_BEN_EXT,
              IFNULL(FEE_BEN_TYPE,'')      AS FEE_BEN_TYPE
            FROM GR_FEE_CONFIG_NEW
            WHERE TRIM(FEE_KEY) = TRIM(:key)
            """;
        try {
            return jdbc.queryForObject(sql, Map.of("key", feeKey), (rs, rn) -> {
                FeeConfigOldTran f = new FeeConfigOldTran();
                f.feeIssTypeNew = rs.getString("FEE_ISS_TYPE_NEW");
                f.feeAcqType    = rs.getString("FEE_ACQ_TYPE");
                f.feeIss        = rs.getBigDecimal("FEE_ISS");
                f.feeAcq        = rs.getBigDecimal("FEE_ACQ");
                f.lowBound      = rs.getBigDecimal("LOW_BOUND");
                f.upBound       = rs.getBigDecimal("UP_BOUND");
                f.feePayAt      = rs.getBigDecimal("FEE_PAY_AT");
                f.feeRecAt      = rs.getBigDecimal("FEE_REC_AT");
                f.feePayType    = rs.getString("FEE_PAY_TYPE");
                f.feeRecType    = rs.getString("FEE_REC_TYPE");
                f.feeIssExt     = rs.getBigDecimal("FEE_ISS_EXT");
                f.feeAcqExt     = rs.getBigDecimal("FEE_ACQ_EXT");
                f.feePayAtExt   = rs.getBigDecimal("FEE_PAY_AT_EXT");
                f.feeRecAtExt   = rs.getBigDecimal("FEE_REC_AT_EXT");

                f.feeIrfIss     = rs.getBigDecimal("FEE_IRF_ISS");
                f.feeSvfIss     = rs.getBigDecimal("FEE_SVF_ISS");
                f.feeIrfAcq     = rs.getBigDecimal("FEE_IRF_ACQ");
                f.feeSvfAcq     = rs.getBigDecimal("FEE_SVF_ACQ");
                f.feeIrfBen     = rs.getBigDecimal("FEE_IRF_BEN");
                f.feeSvfBen     = rs.getBigDecimal("FEE_SVF_BEN");
                f.feeBenExt     = rs.getBigDecimal("FEE_BEN_EXT");
                f.feeBenType    = rs.getString("FEE_BEN_TYPE");
                return f;
            });
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    // Một nhánh helper cho "PHANG_TG_PHAN_TRAM" (ISS / SVF_ISS đặc thù)
    private BigDecimal svfOrIssSpecial(String feeFor, String feeKey, BigDecimal svfIss, BigDecimal conrate, BigDecimal feeIssExt) {
        if ("SVF_ISS".equals(feeFor)) {
            if (isSpecialSvfIssKey(feeKey)) {
                return svfIss; // đặc biệt
            } else {
                return svfIss.multiply(n0(conrate)).add(feeIssExt);
            }
        } else {
            // ISS
            return svfIss.multiply(n0(conrate)).add(feeIssExt); // dùng svfIss như feeIss cho nhánh này
        }
    }
}
