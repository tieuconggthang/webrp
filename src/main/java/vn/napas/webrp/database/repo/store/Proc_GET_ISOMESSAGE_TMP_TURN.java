package vn.napas.webrp.database.repo.store;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.napas.webrp.database.repo.sql.DbLoggerRepository;
import vn.napas.webrp.report.util.SqlLogUtils;

import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class Proc_GET_ISOMESSAGE_TMP_TURN {
    private final JdbcTemplate jdbc;
    private final DbLoggerRepository dblog;
//    private final DbLoggerRepository dblog;

    /** Lấy watermark iSTT = max(STT) giữa SHCLOG và SHCLOG_SETT_IST */
    private long getWatermarkStt() {
        final String sql =
            "SELECT GREATEST(" +
            "  IFNULL((SELECT MAX(STT) FROM SHCLOG), 0)," +
            "  IFNULL((SELECT MAX(STT) FROM SHCLOG_SETT_IST), 0)" +
            ")";
        Long val = jdbc.queryForObject(sql, Long.class);
        return val == null ? 0L : val;
    }

//    /** Đảm bảo index cơ bản (an toàn nếu đã tồn tại) */
//    private void ensureIndexes() {
//        try { jdbc.execute("CREATE INDEX IF NOT EXISTS IX_SHCLOG_STT ON SHCLOG(STT)"); } catch (Exception ignored) {}
//        try { jdbc.execute("CREATE INDEX IF NOT EXISTS IX_SHCLOG_SETT_IST_STT ON SHCLOG_SETT_IST(STT)"); } catch (Exception ignored) {}
//        try { jdbc.execute("CREATE INDEX IF NOT EXISTS IX_TURN_MTI ON ISOMESSAGE_TMP_TURN(MTI)"); } catch (Exception ignored) {}
//        try { jdbc.execute("CREATE INDEX IF NOT EXISTS IX_TURN_TRACE ON ISOMESSAGE_TMP_TURN(TRACE_NO)"); } catch (Exception ignored) {}
//        try { jdbc.execute("CREATE INDEX IF NOT EXISTS IX_TURN_BENID ON ISOMESSAGE_TMP_TURN(BEN_ID)"); } catch (Exception ignored) {}
//    }

    /**
     * Thực thi nghiệp vụ GET_ISOMESSAGE_TMP_TURN: đổ 0210 từ ISOMESSAGE_TMP_TURN sang SHCLOG_SETT_IBFT
     * kèm sinh STT = iSTT + rownum và chuyển đổi cột theo các quy tắc trong store gốc.
     */
    @Transactional
    public int execute() {
        final String module = "GET_ISOMESSAGE_TMP_TURN";

//        ensureIndexes();
        dblog.begin(module, "Begin Insert From ISOMESSAGE_TMP_TURN to SHCLOG_SETT_IBFT");

        long iSTT = getWatermarkStt();
        dblog.info("STT Start in SHCLOG_SETT_IBFT: " + iSTT, module);

        // reset biến user variable cho rownum
        jdbc.execute("SET @row := 0");

        // Lưu ý:
        // - Thay thế các hàm Oracle tuỳ biến bằng phương án tương đương/placeholder:
        //   * NP_CONVERT_LOCAL_DATE(x, TRUNC(SYSDATE)) -> convertLocalDate(x) : STR_TO_DATE cho định dạng số; NULL -> CURDATE()
        //   * MAP_IBFT_ACQ_ID(ACQ_ID) -> m.map_id từ bảng map (nếu có), fallback về ACQ_ID
        //   * GET_IBT_BIN(BEN_ID hoặc SUBSTR(DEST_ACCOUNT,1,6)) -> tra bảng BIN (nếu có), fallback NULL
        //   * to_number_bnv(BEN_ID) -> CAST(BEN_ID AS DECIMAL) an toàn; nếu không numeric -> NULL
        //
        // - Kiểm tra số: IS_NUMBER(col) -> col REGEXP '^[0-9]+$'
        // - TRUNC(date) -> DATE(date) ; TO_CHAR(ts,'HH24MISS') -> DATE_FORMAT(ts,'%H%i%s')
        //
        // Nếu bạn đã có UDF tương đương, thay vào phần TODO_...

        final String sql =
            "INSERT INTO SHCLOG_SETT_IBFT (" +
            "  DATA_ID,PPCODE,STT,MSGTYPE,PAN,PCODE,AMOUNT,ACQ_CURRENCY_CODE,TRACE,LOCAL_TIME,LOCAL_DATE,SETTLEMENT_DATE," +
            "  ACQUIRER,ISSUER,RESPCODE,MERCHANT_TYPE,MERCHANT_TYPE_ORIG,AUTHNUM,SETT_CURRENCY_CODE,TERMID,ADD_INFO,ACCTNUM," +
            "  ISS_CURRENCY_CODE,ORIGTRACE,ORIGISS,ORIGRESPCODE,CH_CURRENCY_CODE,ACQUIRER_FE,ACQUIRER_RP,ISSUER_FE," +
            "  ISSUER_RP,PCODE2,FROM_SYS,BB_BIN,BB_BIN_ORIG,CONTENT_FUND,TXNSRC,ACQ_COUNTRY,POS_ENTRY_CODE,POS_CONDITION_CODE," +
            "  ADDRESPONSE,MVV,F4,F5,F6,F49,SETTLEMENT_CODE,SETTLEMENT_RATE,ISS_CONV_RATE,TCC," +
            "  REFNUM,TRANDATE,TRANTIME,ACCEPTORNAME,TERMLOC,F15,PCODE_ORIG,ACCOUNT_NO,DEST_ACCOUNT,INS_PCODE" +
            ") " +
            "SELECT " +
            "  1 AS DATA_ID, " +
            "  CAST(PROC_CODE AS DECIMAL(18,6)) AS PPCODE, " +
            "  (@row := @row + 1) + ? AS STT, " +                // rownum + iSTT
            "  210 AS MSGTYPE, " +
            "  CARD_NO AS PAN, " +
            "  CAST(PROC_CODE AS DECIMAL(18,6)) AS PCODE, " +
            "  /* AMOUNT: bỏ 2 số lẻ cuối, rỗng -> 0 */ " +
            "  COALESCE(CAST(SUBSTRING(AMOUNT, 1, CHAR_LENGTH(AMOUNT)-2) AS DECIMAL(26,8)), 0) AS AMOUNT, " +
            "  704 AS ACQ_CURRENCY_CODE, " +
            "  CAST(CONCAT('2', TRACE_NO) AS DECIMAL(18,6)) AS TRACE, " +
            "  CAST(LOCAL_TIME AS DECIMAL(18,6)) AS LOCAL_TIME, " +
            "  /* NP_CONVERT_LOCAL_DATE(LOCAL_DATE, TRUNC(SYSDATE)) */ " +
            "  CASE " +
            "    WHEN LOCAL_DATE IS NULL THEN CURDATE() " +
            "    /* TODO_LOCAL_DATE_FORMAT: nếu LOCAL_DATE là chuỗi 'YYYYMMDD' -> STR_TO_DATE */ " +
            "    WHEN LOCAL_DATE REGEXP '^[0-9]{8}$' THEN STR_TO_DATE(LOCAL_DATE,'%Y%m%d') " +
            "    ELSE LOCAL_DATE " + // nếu đã là DATETIME
            "  END AS LOCAL_DATE, " +
            "  /* NP_CONVERT_LOCAL_DATE(SETTLE_DATE, TRUNC(SYSDATE)) */ " +
            "  CASE " +
            "    WHEN SETTLE_DATE IS NULL THEN CURDATE() " +
            "    WHEN SETTLE_DATE REGEXP '^[0-9]{8}$' THEN STR_TO_DATE(SETTLE_DATE,'%Y%m%d') " +
            "    ELSE SETTLE_DATE " +
            "  END AS SETTLEMENT_DATE, " +
            "  CAST(ACQ_ID AS DECIMAL(18,6)) AS ACQUIRER, " +
            "  CAST(ACQ_ID AS DECIMAL(18,6)) AS ISSUER, " +
            "  /* RESPCODE rules */ " +
            "  CAST((CASE " +
            "    WHEN BEN_ID = '971133' AND (DEST_ACCOUNT LIKE 'NPDC%' OR DEST_ACCOUNT LIKE 'NQ%') THEN RESPONSE_CODE " +
            "    WHEN SERVICE_CODE = 'QR_PUSH' THEN RESPONSE_CODE " +
            "    WHEN BEN_ID = '971100' AND TCC = '99' THEN RESPONSE_CODE " +
            "    WHEN TRIM(ISS_ID) IN ('980471','980472') THEN RESPONSE_CODE " +
            "    WHEN RESPONSE_CODE = '68' THEN '0' " +
            "    ELSE RESPONSE_CODE " +
            "  END) AS DECIMAL(18,6)) AS RESPCODE, " +
            "  6011 AS MERCHANT_TYPE, " +
            "  CAST(MCC AS DECIMAL(18,6)) AS MERCHANT_TYPE_ORIG, " +
            "  APPROVAL_CODE AS AUTHNUM, " +
            "  704 AS SETT_CURRENCY_CODE, " +
            "  TERM_ID AS TERMID, " +
            "  ADD_INFO, " +
            "  CONCAT(COALESCE(ACCOUNT_NO,' '), '|', COALESCE(DEST_ACCOUNT,'')) AS ACCTNUM, " +
            "  704 AS ISS_CURRENCY_CODE, " +
            "  CAST(TRACE_NO AS DECIMAL(18,6)) AS ORIGTRACE, " +
            "  CAST(ACQ_ID AS DECIMAL(18,6)) AS ORIGISS, " +
            "  CAST(COALESCE(NULLIF(ORIGRESPCODE, ''), '97') AS DECIMAL(18,6)) AS ORIGRESPCODE, " +
            "  704 AS CH_CURRENCY_CODE, " +
            "  /* ACQUIRER_FE */ " +
            "  CAST((CASE " +
            "    WHEN CAST(ACQ_ID AS UNSIGNED) = 191919 THEN 970459 " +
            "    WHEN CAST(ACQ_ID AS UNSIGNED) = 970415 THEN 970489 " +
            "    /* TODO_MAP_IBFT_ACQ_ID: JOIN bảng map nếu có */ " +
            "    ELSE CAST(ACQ_ID AS UNSIGNED) " +
            "  END) AS DECIMAL(18,6)) AS ACQUIRER_FE, " +
            "  /* ACQUIRER_RP */ " +
            "  CAST((CASE " +
            "    WHEN TRIM(ISS_ID) = '980471' THEN 980471 " +
            "    WHEN TRIM(ISS_ID) = '980475' THEN 980478 " +
            "    WHEN CAST(ACQ_ID AS UNSIGNED) = 191919 THEN 970459 " +
            "    WHEN CAST(ACQ_ID AS UNSIGNED) = 970415 THEN 970489 " +
            "    /* TODO_MAP_IBFT_ACQ_ID */ " +
            "    ELSE CAST(ACQ_ID AS UNSIGNED) " +
            "  END) AS DECIMAL(18,6)) AS ACQUIRER_RP, " +
            "  /* ISSUER_FE */ " +
            "  CAST((CASE " +
            "    WHEN CAST(ACQ_ID AS UNSIGNED) = 191919 THEN 970459 " +
            "    WHEN CAST(ACQ_ID AS UNSIGNED) = 970415 THEN 970489 " +
            "    /* TODO_MAP_IBFT_ACQ_ID */ " +
            "    ELSE CAST(ACQ_ID AS UNSIGNED) " +
            "  END) AS DECIMAL(18,6)) AS ISSUER_FE, " +
            "  /* ISSUER_RP */ " +
            "  CAST((CASE " +
            "    WHEN TRIM(ISS_ID) = '980471' THEN 980471 " +
            "    WHEN TRIM(ISS_ID) = '980475' THEN 980478 " +
            "    WHEN CAST(ACQ_ID AS UNSIGNED) = 191919 THEN 970459 " +
            "    WHEN CAST(ACQ_ID AS UNSIGNED) = 970415 THEN 970489 " +
            "    /* TODO_MAP_IBFT_ACQ_ID */ " +
            "    ELSE CAST(ACQ_ID AS UNSIGNED) " +
            "  END) AS DECIMAL(18,6)) AS ISSUER_RP, " +
            "  /* PCODE2 theo TCC/SERVICE_CODE */ " +
            "  CAST((CASE " +
            "    WHEN TCC = '99' THEN 930000 " +
            "    WHEN TCC = '95' THEN 950000 " +
            "    WHEN SERVICE_CODE = 'QR_PUSH' THEN 890000 " +
            "    WHEN TCC = '97' THEN 720000 " +
            "    WHEN TCC = '98' THEN 730000 " +
            "    ELSE 910000 " +
            "  END) AS DECIMAL(18,6)) AS PCODE2, " +
            "  'IBT' AS FROM_SYS, " +
            "  /* BB_BIN */ " +
            "  CAST((CASE " +
            "    WHEN TRIM(ISS_ID) = '980472' THEN 980471 " +
            "    WHEN TRIM(ISS_ID) = '980474' THEN 980478 " + // IBFT2.0
            "    WHEN BEN_ID IS NOT NULL AND PROC_CODE IN ('912020','910020') THEN /* TODO_GET_IBT_BIN(BEN_ID) */ NULL " +
            "    ELSE /* TODO_GET_IBT_BIN(SUBSTR(DEST_ACCOUNT,1,6)) */ NULL " +
            "  END) AS DECIMAL(18,6)) AS BB_BIN, " +
            "  /* BB_BIN_ORIG <- 'BEN_ID' hoặc map */ " +
            "  CAST((CASE " +
            "    WHEN TRIM(ISS_ID) IN ('980472','980474') THEN " +
            "       (CASE WHEN BEN_ID IS NOT NULL AND PROC_CODE IN ('912020','910020') THEN NULL /* TODO_GET_IBT_BIN(BEN_ID) */ " +
            "             ELSE NULL /* TODO_GET_IBT_BIN(SUBSTR(DEST_ACCOUNT,1,6)) */ END) " +
            "    ELSE (CASE WHEN BEN_ID REGEXP '^[0-9]+$' THEN CAST(BEN_ID AS DECIMAL(18,6)) ELSE NULL END) " +
            "  END) AS DECIMAL(18,6)) AS BB_BIN_ORIG, " +
            "  /* CONTENT_FUND <- IBFT_INFO ; TXNSRC <- VAS_INFO */ " +
            "  IBFT_INFO AS CONTENT_FUND, " +
            "  VAS_INFO AS TXNSRC, " +
            "  ACQ_COUNTRY, POS_ENTRY_CODE, POS_CONDITION_CODE, ADDRESPONSE, MVV, " +
            "  /* F4/F5/F6: bỏ 2 số lẻ cuối */ " +
            "  COALESCE(CAST(SUBSTRING(F4,1,CHAR_LENGTH(F4)-2) AS DECIMAL(26,8)),0) AS F4, " +
            "  COALESCE(CAST(SUBSTRING(F5,1,CHAR_LENGTH(F5)-2) AS DECIMAL(26,8)),0) AS F5, " +
            "  COALESCE(CAST(SUBSTRING(F6,1,CHAR_LENGTH(F6)-2) AS DECIMAL(26,8)),0) AS F6, " +
            "  F49, SETTLEMENT_CODE, SETTLEMENT_RATE, ISS_CONV_RATE, TCC, " +
            "  REF_NO AS REFNUM, DATE(TNX_STAMP) AS TRANDATE, DATE_FORMAT(TNX_STAMP,'%H%i%s') AS TRANTIME, " +
            "  CARD_ACCEPT_NAME_LOCATION AS ACCEPTORNAME, CARD_ACCEPT_ID_CODE AS TERMLOC, " +
            "  /* F15 <- SETTLEMENT_DATE (ISO field 15) */ " +
            "  CASE WHEN SETTLE_DATE IS NULL THEN CURDATE() " +
            "       WHEN SETTLE_DATE REGEXP '^[0-9]{8}$' THEN STR_TO_DATE(SETTLE_DATE,'%Y%m%d') ELSE SETTLE_DATE END AS F15, " +
            "  CAST(PROC_CODE AS DECIMAL(18,6)) AS PCODE_ORIG, " +
            "  ACCOUNT_NO, DEST_ACCOUNT, SUBSTRING(OF_YEAR,1,2) AS INS_PCODE " +
            "FROM ISOMESSAGE_TMP_TURN " +
            "WHERE MTI = '0210' " +
            "  AND (COALESCE(BEN_ID,'0') REGEXP '^[0-9]+$') " +
            "  AND (TRACE_NO REGEXP '^[0-9]+$')";

        try {
        	
        	SqlLogUtils.renderSql(sql, Map.of("STT", iSTT));
            int rows = jdbc.update(sql, iSTT);
            dblog.end(module, "End Insert From ISOMESSAGE_TMP_TURN to SHCLOG_SETT_IBFT. rows=" + rows);
            return rows;
        } catch (Exception ex) {
            String msg = ex.getClass().getSimpleName() + ": " + ex.getMessage();
            dblog.error(module, msg);
            // TODO: hook gửi SMS nếu bạn cần (thay SEND_SMS trong Oracle)
            throw ex;
        }
    }
}
