package vn.napas.webrp.report.service.ibft;

import org.springframework.stereotype.Service;

import vn.napas.webrp.database.repo.FeeCacheLoader;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Các "FUNCTION" Oracle chuyển sang Java, đọc dữ liệu từ cache (FeeCacheLoader).
 * - createPcode            ~ CREATE_PCODE
 * - getFeeKey              ~ NAPAS_GET_FEE_KEY
 * - oldFinalFeeCalVer      ~ OLD_FINAL_FEE_CAL_VER
 * - napasFeeOldTran        ~ NAPAS_FEE_OLD_TRAN
 * - getFeeName             ~ NAPAS_GET_FEE_NAME
 */
@Service
public class CachedFeeFunctionsService {

    private final FeeCacheLoader loader;

    public CachedFeeFunctionsService(FeeCacheLoader loader) {
        this.loader = loader;
    }

    /* =========================================================
     * CREATE_PCODE
     * ========================================================= */
    public String createPcode(String bankId, String pan) {
        try {
            if (bankId == null || pan == null) return "20";
            String b = bankId.trim();
            String pan6 = pan.trim();
            pan6 = pan6.length() >= 6 ? pan6.substring(0, 6) : pan6;

            var index = loader.getPcode72Index();
            var set = index.getOrDefault(b, Set.of());
            int cnt = set.contains(pan6) ? 1 : 0;
            return (cnt == 1) ? "00" : "20";
        } catch (Exception e) {
            // Theo function gốc: mọi lỗi đều trả "00"
            return "00";
        }
    }

    /* =========================================================
     * NAPAS_GET_FEE_KEY
     * ========================================================= */
    public String getFeeKey(
            int acquirer,
            int issuer,
            String proCode,
            int merchantType,
            Integer currencyNullable,
            LocalDate settDate,
            long otrace
    ) {
        int currency = decodeCurrency(currencyNullable);
        var all = loader.getFeeConfigs();

        // Tập ứng viên thỏa điều kiện
        List<FeeConfigRow> candidates = all.stream()
                .filter(r -> "Y".equalsIgnoreCase(nv(r.active)))
                .filter(r -> r.validFrom != null && r.validTo != null)
                .filter(r -> !settDate.isBefore(r.validFrom) && !settDate.isAfter(r.validTo))
                .filter(r -> eq(r.proCode, proCode))
                .filter(r -> r.merchantType == merchantType)
                .filter(r -> r.currencyCode == currency)
                .filter(r -> (r.issuer == 0 || r.issuer == issuer))
                .filter(r -> (r.acquirer == 0 || r.acquirer == acquirer))
                .collect(Collectors.toList());

        if (candidates.isEmpty()) return "ERR - NOT FOUND -3";

        // xếp theo weight giống CASE trong Oracle
        List<Weighted> sorted = candidates.stream()
                .map(r -> new Weighted(r.feeKey, weight(r, issuer, acquirer), nz(r.orderConfig)))
                .sorted(Comparator
                        .comparingInt((Weighted w) -> w.weight)
                        .thenComparingInt(w -> w.orderConfig)
                        .thenComparing(w -> w.feeKey, Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();

        int best = sorted.get(0).weight;
        long sameBest = sorted.stream().filter(w -> w.weight == best).count();
        if (sameBest > 1) return "ERR - Many Fee Config";
        if (sameBest == 0) return "ERR - Not Found Config";
        return sorted.get(0).feeKey;
    }

    private static class Weighted {
        final String feeKey;
        final int weight;
        final int orderConfig;
        Weighted(String feeKey, int weight, int orderConfig) {
            this.feeKey = feeKey;
            this.weight = weight;
            this.orderConfig = orderConfig;
        }
    }

    private int weight(FeeConfigRow g, int issuer, int acquirer) {
        if (g.issuer == issuer && g.acquirer == acquirer) return 1;
        if (g.issuer == issuer && g.acquirer == 0)       return 3;
        if (g.issuer == 0      && g.acquirer == acquirer) return 3;
        if (g.issuer == issuer && g.acquirer != acquirer) return 5 + nz(g.orderConfig);
        if (g.issuer != issuer && g.acquirer == acquirer) return 5 + nz(g.orderConfig);
        return 10;
    }

    /* =========================================================
     * OLD_FINAL_FEE_CAL_VER
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
        // skip PAY/REC nếu ko phải 2 bank ngoại
        if (isAny(feeFor, "PAY", "REC") && !isAny(acquirer, 602907, 605609) && !isAny(issuer, 602907, 605609)) {
            return BigDecimal.ZERO;
        }

        var row = findByFeeKey(feeKey);
        if (row == null) return BigDecimal.ZERO;

        String feeIssType   = nv(row.feeIssType);
        String feeAcqType   = nv(row.feeAcqType);
        BigDecimal feeIss   = n0(row.feeIss);
        BigDecimal feeAcq   = n0(row.feeAcq);
        BigDecimal lowBound = n0(row.lowBound);
        BigDecimal upBound  = n0(row.upBound);
        BigDecimal payAt    = n0(row.feePayAt);
        BigDecimal recAt    = n0(row.feeRecAt);
        String feePayType   = nv(row.feePayType);
        String feeRecType   = nv(row.feeRecType);
        BigDecimal feeIssExt= n0(row.feeIssExt);
        BigDecimal feeAcqExt= n0(row.feeAcqExt);

        BigDecimal result;
        switch (sv(feeFor)) {
            case "ISS" -> result = feeIss;
            case "ACQ" -> result = feeAcq;
            case "PAY" -> result = payAt;
            case "REC" -> result = recAt;
            default    -> result = BigDecimal.ZERO;
        }

        if (eq(feeIssType, "PHAN_TRAM") && eq(feeFor, "ISS")) {
            result = pct(amount, result);
            result = clamp(result, lowBound, upBound);
        } else if (eq(feeAcqType, "PHAN_TRAM") && eq(feeFor, "ACQ")) {
            result = pct(amount, result);
            result = clamp(result, lowBound, upBound);
        } else if (eq(feePayType, "PHAN_TRAM") && eq(feeFor, "PAY")) {
            result = pct(amount, result);
        } else if (eq(feeRecType, "PHAN_TRAM") && eq(feeFor, "REC")) {
            result = pct(amount, result);
        } else if (eq(feeIssType, "PHANG_PHAN_TRAM") && eq(feeFor, "ISS")) {
            result = pct(amount, result).add(feeIssExt);
            result = clamp(result, lowBound, upBound);
        } else if (eq(feeAcqType, "PHANG_PHAN_TRAM") && eq(feeFor, "ACQ")) {
            result = pct(amount, result).add(feeAcqExt);
            result = clamp(result, lowBound, upBound);
        } else if (eq(feeIssType, "PHANG_TG_PHAN_TRAM") && eq(feeFor, "ISS")) {
            result = feeIss.multiply(n0(conrate)).add(feeIssExt);
            result = clamp(result, lowBound, upBound);
        } else if (eq(feeAcqType, "PHANG_TG_PHAN_TRAM") && eq(feeFor, "ACQ")) {
            result = feeAcq.multiply(n0(conrate)).add(feeAcqExt);
            result = clamp(result, lowBound, upBound);
        } else if (eq(feeIssType, "CBFT") && eq(feeFor, "ISS")) {
            BigDecimal term1 = feeIss.multiply(n0(conrate));
            BigDecimal term2 = feeIssExt.multiply(amount).divide(bd(100), 8, RoundingMode.HALF_UP);
            result = term1.add(term2).divide(bd(1.1), 8, RoundingMode.HALF_UP);
            result = clamp(result, lowBound, upBound);
        } else if (eq(feeFor, "ACQ") && !Objects.equals(currencyNullable, 840)
                && conrate != null && eq(feeAcqType, "PHANG_TYGIA")) {
            result = result.multiply(conrate);
        } else if (eq(feeFor, "ISS") && !Objects.equals(currencyNullable, 840)
                && conrate != null && eq(feeIssType, "PHANG_TYGIA")) {
            result = result.multiply(conrate);
        }

        return nz(result);
    }

    /* =========================================================
     * NAPAS_FEE_OLD_TRAN
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
        if (isAny(feeFor, "PAY", "REC") && !isAny(acquirer, 602907, 605609) && !isAny(issuer, 602907, 605609)) {
            return BigDecimal.ZERO;
        }

        var row = findByFeeKey(feeKey);
        if (row == null) return BigDecimal.ZERO;

        String feeIssTypeNew = nv(row.feeIssTypeNew);
        String feeAcqType    = nv(row.feeAcqType);
        BigDecimal feeIss    = n0(row.feeIss);
        BigDecimal feeAcq    = n0(row.feeAcq);
        BigDecimal lowBound  = n0(row.lowBound);
        BigDecimal upBound   = n0(row.upBound);
        BigDecimal payAt     = n0(row.feePayAt);
        BigDecimal recAt     = n0(row.feeRecAt);
        String feePayType    = nv(row.feePayType);
        String feeRecType    = nv(row.feeRecType);
        BigDecimal feeIssExt = n0(row.feeIssExt);
        BigDecimal feeAcqExt = n0(row.feeAcqExt);
        BigDecimal irfIss = n0(row.feeIrfIss);
        BigDecimal svfIss = n0(row.feeSvfIss);
        BigDecimal irfAcq = n0(row.feeIrfAcq);
        BigDecimal svfAcq = n0(row.feeSvfAcq);
        BigDecimal irfBen = n0(row.feeIrfBen);
        BigDecimal svfBen = n0(row.feeSvfBen);
        BigDecimal benExt = n0(row.feeBenExt);
        String feeBenType  = nv(row.feeBenType);

        BigDecimal result = switch (sv(feeFor)) {
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

        if (msgType == 430) {
            if (isAny(feeFor, "SVF_ISS", "SVF_ACQ", "SVF_BEN")) {
                result = BigDecimal.ZERO;
            } else if (eq(feeFor, "IRF_ISS") && eq(feeIssTypeNew, "PHAN_TRAM")) {
                result = pct(amount, result);
            } else if (eq(feeFor, "IRF_ACQ") && eq(feeAcqType, "PHAN_TRAM")) {
                result = pct(amount, result);
            } else {
                result = BigDecimal.ZERO; // phí phẳng không hoàn
            }
            return nz(result);
        }

        // MSGTYPE != 430
        if (isAny(feeFor, "ISS", "IRF_ISS", "SVF_ISS") && eq(feeIssTypeNew, "PHAN_TRAM")) {
            result = pct(amount, result);
            if (eq(feeFor, "SVF_ISS") && isSpecialSvfIssKey(feeKey) && result.abs().compareTo(lowBound.abs()) < 0) {
                result = lowBound.negate(); // rule đặc thù theo PL/SQL
            }
        } else if (isAny(feeFor, "ACQ", "IRF_ACQ", "SVF_ACQ") && eq(feeAcqType, "PHAN_TRAM")) {
            result = pct(amount, result);
        } else if (isAny(feeFor, "PAY", "REC") && eq(feePayType, "PHAN_TRAM")) {
            result = pct(amount, result);
        } else if (eq(feeFor, "ISS") && eq(feeIssTypeNew, "PHANG_PHAN_TRAM")) {
            result = pct(amount, result).add(feeIssExt);
        } else if (eq(feeFor, "SVF_ACQ") && eq(feeAcqType, "PHANG_PHAN_TRAM")) {
            result = pct(amount, result).add(feeAcqExt);
        } else if (eq(feeFor, "SVF_ACQ") && eq(feeAcqType, "PHANG_PHAN_TRAM_USD")) {
            result = pct(amount, result).add(divSafe(feeAcqExt, n0(conrate)));
        } else if (isAny(feeFor, "ISS") && eq(feeIssTypeNew, "PHANG_TG_PHAN_TRAM")) {
            result = feeIss.multiply(n0(conrate)).add(feeIssExt);
        } else if (isAny(feeFor, "SVF_ISS") && eq(feeIssTypeNew, "PHANG_TG_PHAN_TRAM")) {
            if (isSpecialSvfIssKey(feeKey)) {
                result = svfIss;
            } else {
                result = svfIss.multiply(n0(conrate)).add(feeIssExt);
            }
        } else if (eq(feeFor, "ACQ") && eq(feeAcqType, "PHANG_TG_PHAN_TRAM")) {
            result = feeAcq.multiply(n0(conrate)).add(feeAcqExt);
        } else if (eq(feeFor, "ISS") && eq(feeIssTypeNew, "CBFT")) {
            result = feeIss.multiply(n0(conrate)).add(feeIssExt.multiply(amount));
        } else if (eq(feeFor, "IRF_ISS") && eq(feeIssTypeNew, "CBFT")) {
            result = irfIss.multiply(n0(conrate)).add(feeIssExt.multiply(amount));
        } else if (eq(feeFor, "IRF_BEN") && eq(feeBenType, "CBFT")) {
            result = irfBen.multiply(n0(conrate)).add(benExt.multiply(amount));
        } else if (eq(feeFor, "IRF_ISS") && eq(feeIssTypeNew, "CBFT_V2")) {
            result = irfIss.multiply(amount).add(feeIssExt);
        } else if (eq(feeFor, "IRF_BEN") && eq(feeBenType, "CBFT_V2")) {
            result = irfBen.multiply(amount).add(benExt);
        } else if (eq(feeFor, "IRF_BEN") && eq(feeBenType, "PHAN_TRAM")) {
            result = pct(amount, result);
        } else if (eq(feeFor, "SVF_BEN") && eq(feeBenType, "PHAN_TRAM")) {
            result = pct(amount, result);
        } else if (isAny(feeFor, "ACQ", "IRF_ACQ", "SVF_ACQ")
                && !Objects.equals(currencyNullable, 840)
                && conrate != null
                && eq(feeAcqType, "PHANG_TYGIA")) {
            result = result.multiply(conrate);
        } else if (isAny(feeFor, "ISS", "IRF_ISS", "SVF_ISS")
                && !Objects.equals(currencyNullable, 840)
                && conrate != null
                && eq(feeIssTypeNew, "PHANG_TYGIA")) {
            if (eq(feeFor, "SVF_ISS") && isSpecialSvfIssKey(feeKey)) {
                result = svfIss;
            } else {
                result = result.multiply(conrate);
            }
        }

        return nz(result);
    }

    /* =========================================================
     * NAPAS_GET_FEE_NAME
     * ========================================================= */
    public String getFeeName(String feeKey) {
        var row = findByFeeKey(feeKey);
        if (row == null) return "ERR - NO_DATA_FOUND Fee_Note";
        return nv(row.feeNote);
    }

    /* ====================== Helpers ====================== */

    private FeeConfigRow findByFeeKey(String feeKey) {
        if (feeKey == null) return null;
        for (var r : loader.getFeeConfigs()) {
            if (eq(r.feeKey, feeKey)) return r;
        }
        return null;
    }

    private static boolean isAny(String s, String... arr) {
        if (s == null) return false;
        for (String a : arr) if (a.equalsIgnoreCase(s)) return true;
        return false;
    }
    private static boolean isAny(int v, int... arr) {
        for (int a : arr) if (a == v) return true;
        return false;
    }

    private static String nv(String s) { return s == null ? "" : s; }
    private static String sv(String s) { return s == null ? "" : s.toUpperCase(Locale.ROOT); }
    private static boolean eq(String a, String b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        return a.trim().equalsIgnoreCase(b.trim());
    }
    private static int nz(Integer i) { return i == null ? 0 : i; }
    private static BigDecimal n0(BigDecimal v) { return v == null ? BigDecimal.ZERO : v; }
    private static BigDecimal nz(BigDecimal v) { return v == null ? BigDecimal.ZERO : v; }
    private static BigDecimal bd(double d) { return BigDecimal.valueOf(d); }

    private static BigDecimal pct(BigDecimal amount, BigDecimal rate) {
        if (amount == null || rate == null) return BigDecimal.ZERO;
        return amount.multiply(rate);
    }
    private static BigDecimal clamp(BigDecimal val, BigDecimal low, BigDecimal up) {
        if (val == null) return BigDecimal.ZERO;
        BigDecimal r = val;
        if (up  != null && up.signum() != 0 && r.compareTo(up)  > 0) r = up;
        if (low != null && low.signum() != 0 && r.compareTo(low) < 0) r = low;
        return r;
    }
    private static BigDecimal divSafe(BigDecimal a, BigDecimal b) {
        if (a == null || b == null || b.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        return a.divide(b, 8, RoundingMode.HALF_UP);
    }

    private boolean isSpecialSvfIssKey(String feeKey) {
        // Danh sách đặc thù theo code PL/SQL (có thể chuyển sang bảng cấu hình nếu cần)
        return isAny(feeKey,
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
}
