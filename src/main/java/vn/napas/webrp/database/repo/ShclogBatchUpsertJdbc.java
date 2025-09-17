package vn.napas.webrp.database.repo;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import vn.napas.webrp.database.dto.ISOMESSAGETMPTURN;
import vn.napas.webrp.database.dto.ShclogSettIbftDto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ShclogBatchUpsertJdbc {

    private final JdbcTemplate jdbc;

    /* ========= Helpers ========= */
    private static BigDecimal nz(BigDecimal x){ return x==null? BigDecimal.ZERO : x; }
    private static BigDecimal div100(BigDecimal x){ return nz(x).divide(BigDecimal.valueOf(100), 0, RoundingMode.DOWN); }
    private static BigDecimal fromLong(Long v){ return v==null? BigDecimal.ZERO : BigDecimal.valueOf(v); }
    private static LocalDate mmddToLocalDate(String mmdd){
        if (mmdd==null || mmdd.length()<4) return null;
        int y = Year.now().getValue();
        int m = Integer.parseInt(mmdd.substring(0,2));
        int d = Integer.parseInt(mmdd.substring(2,4));
        return LocalDate.of(y, m, d);
    }
    private static LocalDateTime mmddToLdt(String mmdd){ LocalDate d = mmddToLocalDate(mmdd); return d==null? null : d.atStartOfDay(); }
    private static LocalDateTime toDateLdt(Instant ts){ return ts==null? null : ts.atZone(ZoneId.systemDefault()).toLocalDate().atStartOfDay(); }
    private static String hhmmss(Instant ts){
        if (ts==null) return null;
        var z = ts.atZone(ZoneId.systemDefault());
        return String.format("%02d%02d%02d", z.getHour(), z.getMinute(), z.getSecond());
    }
    private static String acct(String a,String d){ return (a==null?" ":a) + "|" + (d==null?"":d); }
    private static String trunc(String s,int len){ if(s==null) return null; return s.length()<=len? s : s.substring(0,len); }
    private static BigDecimal num(String s){ return (s==null||s.isBlank())? null : new BigDecimal(s.trim()); }
    private static int toNumberBnv(String s){ if(s==null)return 0; var t=s.trim(); return t.matches("\\d+")?Integer.parseInt(t):0; }

    // === Rules từ store ===
    private static int respSeed(ISOMESSAGETMPTURN b){
        if ("971133".equals(b.getBenId()) && b.getDestAccount()!=null && b.getDestAccount().startsWith("NPDC")) return 68;
        if ("QR_PUSH".equals(b.getServiceCode())) return 68;
        if ("971100".equals(b.getBenId()) && "99".equals(b.getTcc())) return 68;
        String iss = b.getIssId()==null?null:b.getIssId().trim();
        if ("980471".equals(iss) || "980472".equals(iss)) return 68;
        return 0;
    }
    private static int pcode2(ISOMESSAGETMPTURN b){
        String t = String.valueOf(b.getTcc());
        return switch (t) {
            case "99" -> 930000;
            case "95" -> 950000;
            case "97" -> 720000;
            case "98" -> 730000;
            default -> "QR_PUSH".equals(b.getServiceCode()) ? 890000 : 910000;
        };
    }
    private static int mapAcq(String acqId){ try{ int a=Integer.parseInt(acqId==null?"0":acqId.trim()); if(a==191919) return 970459; if(a==970415) return 970489; return a; }catch(Exception e){return 0;} }
    private static int mapIss(String issId,String acqId){ String s= issId==null?null:issId.trim(); if("980471".equals(s)) return 980471; if("980475".equals(s)) return 980478; return mapAcq(acqId); }
    private static int getIbtBin(String key){ try { return Integer.parseInt(key); } catch (Exception e){ return 0; } }
    private static boolean inTgtt20(String benId){ return false; } // TODO: SELECT TGTT_20
    private static int bbBin(ISOMESSAGETMPTURN b){
        String iss = b.getIssId()==null?null:b.getIssId().trim();
        if ("980472".equals(iss)) return 980471;
        if ("980474".equals(iss)) return 980478;
        if (b.getBenId()!=null && ("912020".equals(b.getProcCode()) || "910020".equals(b.getProcCode()))) return getIbtBin(b.getBenId());
        String key = b.getDestAccount()==null?null:(b.getDestAccount().length()>=6? b.getDestAccount().substring(0,6): b.getDestAccount());
        return getIbtBin(key);
    }
    private static int bbBinOrig(ISOMESSAGETMPTURN b){
        if (inTgtt20(b.getBenId())) return toNumberBnv(b.getBenId());
        String iss = b.getIssId()==null?null:b.getIssId().trim();
        if ("980472".equals(iss) || "980474".equals(iss) || "980475".equals(iss)) {
            if (b.getBenId()!=null && ("912020".equals(b.getProcCode()) || "910020".equals(b.getProcCode()))) return getIbtBin(b.getBenId());
            String key = b.getDestAccount()==null?null:(b.getDestAccount().length()>=6? b.getDestAccount().substring(0,6): b.getDestAccount());
            return getIbtBin(key);
        }
        return toNumberBnv(b.getBenId());
    }

    /* ====== STT sequence per (SETTLEMENT_DATE, MSGTYPE=210, ACQUIRER) ====== */
    private static final class SttKey {
        private final LocalDate settleDate; private final int acq;
        SttKey(LocalDate d, int a){ this.settleDate=d; this.acq=a; }
        LocalDate settleDate(){ return settleDate; }
        int acq(){ return acq; }
        @Override public int hashCode(){ return settleDate.hashCode()*31 + acq; }
        @Override public boolean equals(Object o){ if(!(o instanceof SttKey k)) return false; return acq==k.acq && settleDate.equals(k.settleDate); }
    }

    /** Lấy seed MAX(STT) cho từng nhóm xuất hiện trong page. Chunk theo 200 key để tránh câu SQL quá dài. */
    private Map<SttKey, BigDecimal> fetchSttSeeds(Set<SttKey> keys) {
        Map<SttKey, BigDecimal> seeds = new HashMap<>();
        if (keys.isEmpty()) return seeds;

        List<SttKey> list = new ArrayList<>(keys);
        for (int i = 0; i < list.size(); i += 200) {
            List<SttKey> chunk = list.subList(i, Math.min(i+200, list.size()));

            // MySQL/TiDB hỗ trợ tuple IN ((?,?),(?,?))
            String placeholders = chunk.stream().map(k -> "(?,?)").collect(Collectors.joining(","));
            String sql = """
                SELECT DATE(SETTLEMENT_DATE) AS sd, ACQUIRER, IFNULL(MAX(STT),0) AS base_stt
                FROM SHCLOG_SETT_IBFT
                WHERE (DATE(SETTLEMENT_DATE), ACQUIRER) IN (%s)
                  AND MSGTYPE = 210
                GROUP BY sd, ACQUIRER
            """.formatted(placeholders);

            Object[] params = new Object[chunk.size()*2];
            int p=0;
            for (SttKey k : chunk) {
                params[p++] = java.sql.Date.valueOf(k.settleDate());
                params[p++] = BigDecimal.valueOf(k.acq());
            }

            jdbc.query(sql, rs -> {
                LocalDate sd = rs.getDate("sd").toLocalDate();
                int acq = rs.getBigDecimal("ACQUIRER").intValue();
                BigDecimal base = rs.getBigDecimal("base_stt");
                seeds.put(new SttKey(sd, acq), base);
            }, params);
        }
        return seeds;
    }

    /* ========= SQL Upsert ========= */
    private static final String UPSERT_SQL = """
        INSERT INTO SHCLOG_SETT_IBFT (
          DATA_ID, PPCODE, MSGTYPE, PAN, PCODE, AMOUNT, ACQ_CURRENCY_CODE, TRACE, LOCAL_TIME, LOCAL_DATE, SETTLEMENT_DATE,
          ACQUIRER, ISSUER, RESPCODE, MERCHANT_TYPE, MERCHANT_TYPE_ORIG, AUTHNUM, SETT_CURRENCY_CODE, TERMID, ADD_INFO, ACCTNUM,
          ISS_CURRENCY_CODE, ORIGTRACE, ORIGISS, ORIGRESPCODE, CH_CURRENCY_CODE, ACQUIRER_FE, ACQUIRER_RP, ISSUER_FE, ISSUER_RP, PCODE2,
          FROM_SYS, BB_BIN, BB_BIN_ORIG, CONTENT_FUND, TXNSRC, ACQ_COUNTRY, POS_ENTRY_CODE, POS_CONDITION_CODE, ADDRESPONSE, MVV,
          F4, F5, F6, F49, SETTLEMENT_CODE, SETTLEMENT_RATE, ISS_CONV_RATE, TCC, REFNUM, TRANDATE, TRANTIME, ACCEPTORNAME, TERMLOC, F15, PCODE_ORIG,
          ACCOUNT_NO, DEST_ACCOUNT, STT
        ) VALUES (
          ?,?,?,?,?,?,?,?,?,?, ?,?,?,?,?,?,?,?,?,?,
          ?,?,?,?,?,?,?,?,?,?, ?,?,?,?,?,?,?,?,?,?,
          ?,?,?,?,?,?,?,?,?,?, ?,?,?,?, ?,?
        )
        ON DUPLICATE KEY UPDATE
          RESPCODE = CASE WHEN (AMOUNT <> VALUES(AMOUNT)) AND RESPCODE = 0 THEN 116 ELSE RESPCODE END,
          TXNSRC   = CASE WHEN (AMOUNT <> VALUES(AMOUNT)) THEN 'RC=99' ELSE TXNSRC END,
          CONTENT_FUND = VALUES(CONTENT_FUND)
    """;

    /** Map ISOMESSAGETMPTURN → DTO đích (chỉ các cột cần ghi). */
    private ShclogSettIbftDto mapToDto(ISOMESSAGETMPTURN b, BigDecimal stt) {
        int acqInt = mapAcq(b.getAcqId());
        int resp = respSeed(b);
        int acqFe = mapAcq(b.getAcqId());
        int acqRp = mapIss(b.getIssId(), b.getAcqId());
        int issFe = acqFe, issRp = acqRp;

        BigDecimal amountOut = div100(fromLong(b.getAmount()));
        BigDecimal f4 = div100(b.getF4());
        BigDecimal f5 = div100(b.getF5());
        BigDecimal f6 = div100(b.getF6());

        LocalDateTime localDate = mmddToLdt(b.getLocalDate());
        LocalDateTime settleDate = mmddToLdt(b.getSettleDate());
        LocalDateTime tranDate = toDateLdt(b.getTnxStamp());
        String tranTime = hhmmss(b.getTnxStamp());

        return ShclogSettIbftDto.builder()
                .msgtype(BigDecimal.valueOf(210))
                .pan(trunc(b.getCardNo(), 19))
                .pcode(num(b.getProcCode()))
                .amount(amountOut)
                .acqCurrencyCode(BigDecimal.valueOf(704))
                .trace(num("2" + b.getTraceNo()))
                .localTime(num(b.getLocalTime()))
                .localDate(localDate)
                .settlementDate(settleDate)
                .acquirer(BigDecimal.valueOf(acqInt))
                .issuer(BigDecimal.valueOf(acqInt))
                .respcode(BigDecimal.valueOf(resp))
                .merchantType(BigDecimal.valueOf(6011))
                .merchantTypeOrig(num(b.getMcc()))
                .authnum(trunc(b.getApprovalCode(), 6))
                .settCurrencyCode(BigDecimal.valueOf(704))
                .termid(trunc(b.getTermId(), 8))
                .addInfo(b.getAddInfo())
                .acctnum(trunc(acct(b.getAccountNo(), b.getDestAccount()), 70))
                .issCurrencyCode(BigDecimal.valueOf(704))
                .origtrace(num(b.getTraceNo()))
                .origiss(String.valueOf(acqInt))
                .origrespcode(BigDecimal.valueOf(97))
                .chCurrencyCode(BigDecimal.valueOf(704))
                .acquirerFe(BigDecimal.valueOf(acqFe))
                .acquirerRp(BigDecimal.valueOf(acqRp))
                .issuerFe(BigDecimal.valueOf(issFe))
                .issuerRp(BigDecimal.valueOf(issRp))
                .pcode2(BigDecimal.valueOf(pcode2(b)))
                .fromSys("IBT")
                .bbBin(BigDecimal.valueOf(bbBin(b)))
                .bbBinOrig(BigDecimal.valueOf(bbBinOrig(b)))
                .contentFund(b.getIbftInfo())
                .txnsrc("MTI=200")
                .acqCountry(b.getAcqCountry())
                .posEntryCode(b.getPosEntryCode())
                .posConditionCode(b.getPosConditionCode())
                .addresponse(b.getAddResponse())
                .mvv(b.getMvv())
                .f4(f4)
                .f5(f5)
                .f6(f6)
                .f49(nz(b.getF49()))
                .settlementCode(nz(b.getSettlementCode()))
                .settlementRate(nz(b.getSettlementRate()))
                .issConvRate(nz(b.getIssConvRate()))
                .tcc(b.getTcc())
                .refnum(trunc(b.getRefNo(), 12))
                .trandate(tranDate)
                .trantime(num(tranTime))
                .acceptorname(trunc(b.getCardAcceptNameLocation(), 40))
                .termloc(trunc(b.getCardAcceptIdCode(), 25))
                .f15(settleDate) // theo store
                .pcodeOrig(num(b.getProcCode()))
                .accountNo(b.getAccountNo())
                .destAccount(b.getDestAccount())
                .stt(stt)
                .build();
    }

    /** Batch upsert: nhận trang, map sang DTO, tự cấp STT — dùng JdbcTemplate.batchUpdate */
    public int batchUpsert(List<ISOMESSAGETMPTURN> page, int batchSize) {
        if (page == null || page.isEmpty()) return 0;

        // 1) Gom key nhóm để seed STT (DATE(settle), acq)
        Set<SttKey> keys = page.stream()
                .map(b -> new SttKey(mmddToLocalDate(b.getSettleDate()), mapAcq(b.getAcqId())))
                .collect(Collectors.toCollection(LinkedHashSet::new));

        // 2) Lấy MAX(STT) hiện có cho từng nhóm
        Map<SttKey, BigDecimal> base = fetchSttSeeds(keys);
        // 3) Đếm tăng dần trong batch
        Map<SttKey, BigDecimal> counter = new HashMap<>();

        int affectedTotal = 0;
        final int bz = Math.max(1, batchSize);

        for (int i = 0; i < page.size(); i += bz) {
            final List<ISOMESSAGETMPTURN> sub = page.subList(i, Math.min(i + bz, page.size()));

            int[] ret = jdbc.batchUpdate(UPSERT_SQL, new org.springframework.jdbc.core.BatchPreparedStatementSetter() {
                @Override public void setValues(PreparedStatement ps, int row) throws java.sql.SQLException {
                    ISOMESSAGETMPTURN b = sub.get(row);
                    int acqInt = mapAcq(b.getAcqId());
                    LocalDate settleD = mmddToLocalDate(b.getSettleDate());
                    SttKey key = new SttKey(settleD, acqInt);

                    // Cấp STT = base + (++counter)
                    BigDecimal seed = base.getOrDefault(key, BigDecimal.ZERO);
                    BigDecimal cur  = counter.merge(key, BigDecimal.ONE, BigDecimal::add);
                    BigDecimal stt  = seed.add(cur);

                    ShclogSettIbftDto d = mapToDto(b, stt);

                    int idx=1;
                    ps.setBigDecimal(idx++, BigDecimal.ONE);                              // DATA_ID
                    ps.setBigDecimal(idx++, d.getPcode());                                // PPCODE
                    ps.setBigDecimal(idx++, d.getMsgtype());                              // MSGTYPE
                    ps.setString(idx++, d.getPan());                                      // PAN
                    ps.setBigDecimal(idx++, d.getPcode());                                // PCODE
                    ps.setBigDecimal(idx++, d.getAmount());                               // AMOUNT
                    ps.setBigDecimal(idx++, d.getAcqCurrencyCode());                      // ACQ_CURRENCY_CODE
                    ps.setBigDecimal(idx++, d.getTrace());                                // TRACE
                    ps.setBigDecimal(idx++, d.getLocalTime());                            // LOCAL_TIME
                    ps.setTimestamp(idx++, d.getLocalDate()==null? null : Timestamp.valueOf(d.getLocalDate())); // LOCAL_DATE
                    ps.setTimestamp(idx++, d.getSettlementDate()==null? null : Timestamp.valueOf(d.getSettlementDate())); // SETTLEMENT_DATE
                    ps.setBigDecimal(idx++, d.getAcquirer());                             // ACQUIRER
                    ps.setBigDecimal(idx++, d.getIssuer());                               // ISSUER
                    ps.setBigDecimal(idx++, d.getRespcode());                             // RESPCODE
                    ps.setBigDecimal(idx++, d.getMerchantType());                         // MERCHANT_TYPE
                    ps.setBigDecimal(idx++, d.getMerchantTypeOrig());                     // MERCHANT_TYPE_ORIG
                    ps.setString(idx++, d.getAuthnum());                                  // AUTHNUM
                    ps.setBigDecimal(idx++, d.getSettCurrencyCode());                     // SETT_CURRENCY_CODE
                    ps.setString(idx++, d.getTermid());                                   // TERMID
                    ps.setString(idx++, d.getAddInfo());                                  // ADD_INFO
                    ps.setString(idx++, d.getAcctnum());                                  // ACCTNUM
                    ps.setBigDecimal(idx++, d.getIssCurrencyCode());                      // ISS_CURRENCY_CODE
                    ps.setBigDecimal(idx++, d.getOrigtrace());                            // ORIGTRACE
                    ps.setString(idx++, d.getOrigiss());                                  // ORIGISS
                    ps.setBigDecimal(idx++, d.getOrigrespcode());                         // ORIGRESPCODE
                    ps.setBigDecimal(idx++, d.getChCurrencyCode());                       // CH_CURRENCY_CODE
                    ps.setBigDecimal(idx++, d.getAcquirerFe());                           // ACQUIRER_FE
                    ps.setBigDecimal(idx++, d.getAcquirerRp());                           // ACQUIRER_RP
                    ps.setBigDecimal(idx++, d.getIssuerFe());                             // ISSUER_FE
                    ps.setBigDecimal(idx++, d.getIssuerRp());                             // ISSUER_RP
                    ps.setBigDecimal(idx++, d.getPcode2());                               // PCODE2
                    ps.setString(idx++, d.getFromSys());                                  // FROM_SYS
                    ps.setBigDecimal(idx++, d.getBbBin());                                // BB_BIN
                    ps.setBigDecimal(idx++, d.getBbBinOrig());                            // BB_BIN_ORIG
                    ps.setString(idx++, d.getContentFund());                              // CONTENT_FUND
                    ps.setString(idx++, d.getTxnsrc());                                   // TXNSRC
                    ps.setBigDecimal(idx++, d.getAcqCountry());                           // ACQ_COUNTRY
                    ps.setBigDecimal(idx++, d.getPosEntryCode());                         // POS_ENTRY_CODE
                    ps.setBigDecimal(idx++, d.getPosConditionCode());                     // POS_CONDITION_CODE
                    ps.setString(idx++, d.getAddresponse());                              // ADDRESPONSE
                    ps.setString(idx++, d.getMvv());                                      // MVV
                    ps.setBigDecimal(idx++, d.getF4());                                   // F4
                    ps.setBigDecimal(idx++, d.getF5());                                   // F5
                    ps.setBigDecimal(idx++, d.getF6());                                   // F6
                    ps.setBigDecimal(idx++, d.getF49());                                  // F49
                    ps.setBigDecimal(idx++, d.getSettlementCode());                       // SETTLEMENT_CODE
                    ps.setBigDecimal(idx++, d.getSettlementRate());                       // SETTLEMENT_RATE
                    ps.setBigDecimal(idx++, d.getIssConvRate());                          // ISS_CONV_RATE
                    ps.setString(idx++, d.getTcc());                                      // TCC
                    ps.setString(idx++, d.getRefnum());                                   // REFNUM
                    ps.setTimestamp(idx++, d.getTrandate()==null? null : Timestamp.valueOf(d.getTrandate())); // TRANDATE
                    ps.setBigDecimal(idx++, d.getTrantime());                             // TRANTIME
                    ps.setString(idx++, d.getAcceptorname());                             // ACCEPTORNAME
                    ps.setString(idx++, d.getTermloc());                                  // TERMLOC
                    ps.setTimestamp(idx++, d.getF15()==null? null : Timestamp.valueOf(d.getF15())); // F15
                    ps.setBigDecimal(idx++, d.getPcodeOrig());                            // PCODE_ORIG
                    ps.setString(idx++, d.getAccountNo());                                // ACCOUNT_NO
                    ps.setString(idx++, d.getDestAccount());                              // DEST_ACCOUNT
                    ps.setBigDecimal(idx++, d.getStt());                                  // STT
                }
                @Override public int getBatchSize() { return sub.size(); }
            });

            for (int v : ret) if (v > 0) affectedTotal += v;
        }

        return affectedTotal;
    }
}
