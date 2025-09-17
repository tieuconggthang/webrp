package vn.napas.webrp.database.repo;



import lombok.RequiredArgsConstructor;
import vn.napas.webrp.database.dto.ISOMESSAGETMPTURN;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class IsoMessageTmpTurnReaderJdbc {
    private final JdbcTemplate jdbc;

    public List<ISOMESSAGETMPTURN> fetchAfter(Instant lastTs, long lastTrace, int limit) {
        Timestamp ts = lastTs == null ? Timestamp.from(Instant.EPOCH) : Timestamp.from(lastTs);

        String sql = """
            SELECT *
            FROM ISOMESSAGE_TMP_TURN b
            WHERE b.MTI='0200'
              AND b.CARD_NO IS NOT NULL
              AND b.BEN_ID  REGEXP '^[0-9]+$'
              AND b.TRACE_NO REGEXP '^[0-9]+$'
              AND (b.TNX_STAMP > ? OR (b.TNX_STAMP = ? AND CAST(b.TRACE_NO AS UNSIGNED) > ?))
            ORDER BY b.TNX_STAMP, CAST(b.TRACE_NO AS UNSIGNED)
            LIMIT ?
        """;

        return jdbc.query(sql, (ResultSet rs, int rowNum) -> ISOMESSAGETMPTURN.builder()
                .mti(rs.getString("MTI"))
                .cardNo(rs.getString("CARD_NO"))
                .procCode(rs.getString("PROC_CODE"))
                .traceNo(rs.getString("TRACE_NO"))
                .refNo(rs.getString("REF_NO"))
                .acqId(rs.getString("ACQ_ID"))
                .issId(rs.getString("ISS_ID"))
                .approvalCode(rs.getString("APPROVAL_CODE"))
                .termId(rs.getString("TERM_ID"))
                .tnxStamp(rs.getTimestamp("TNX_STAMP") == null ? null : rs.getTimestamp("TNX_STAMP").toInstant())
                .amount((Long) rs.getObject("AMOUNT"))  // bigint(20)
                .accountNo(rs.getString("ACCOUNT_NO"))
                .localTime(rs.getString("LOCAL_TIME"))
                .localDate(rs.getString("LOCAL_DATE"))
                .settleDate(rs.getString("SETTLE_DATE"))
                .mcc(rs.getString("MCC"))
                .destAccount(rs.getString("DEST_ACCOUNT"))
                .addInfo(rs.getString("ADD_INFO"))
                .serviceCode(rs.getString("SERVICE_CODE"))
                .benId(rs.getString("BEN_ID"))
                .acqCountry((BigDecimal) rs.getObject("ACQ_COUNTRY"))
                .posEntryCode((BigDecimal) rs.getObject("POS_ENTRY_CODE"))
                .posConditionCode((BigDecimal) rs.getObject("POS_CONDITION_CODE"))
                .addResponse(rs.getString("ADDRESPONSE"))
                .mvv(rs.getString("MVV"))
                .f4((BigDecimal) rs.getObject("F4"))
                .f5((BigDecimal) rs.getObject("F5"))
                .f6((BigDecimal) rs.getObject("F6"))
                .f49((BigDecimal) rs.getObject("F49"))
                .settlementCode((BigDecimal) rs.getObject("SETTLEMENT_CODE"))
                .settlementRate((BigDecimal) rs.getObject("SETTLEMENT_RATE"))
                .issConvRate((BigDecimal) rs.getObject("ISS_CONV_RATE"))
                .tcc(rs.getString("TCC"))
                .cardAcceptNameLocation(rs.getString("CARD_ACCEPT_NAME_LOCATION"))
                .cardAcceptIdCode(rs.getString("CARD_ACCEPT_ID_CODE"))
                .ibftInfo(rs.getString("IBFT_INFO"))
                .build(),
                ts, ts, lastTrace, limit
        );
    }
}
