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

//        final String sql =
//            "INSERT INTO SHCLOG_SETT_IBFT (" +
//            "  DATA_ID,PPCODE,STT,MSGTYPE,PAN,PCODE,AMOUNT,ACQ_CURRENCY_CODE,TRACE,LOCAL_TIME,LOCAL_DATE,SETTLEMENT_DATE," +
//            "  ACQUIRER,ISSUER,RESPCODE,MERCHANT_TYPE,MERCHANT_TYPE_ORIG,AUTHNUM,SETT_CURRENCY_CODE,TERMID,ADD_INFO,ACCTNUM," +
//            "  ISS_CURRENCY_CODE,ORIGTRACE,ORIGISS,ORIGRESPCODE,CH_CURRENCY_CODE,ACQUIRER_FE,ACQUIRER_RP,ISSUER_FE," +
//            "  ISSUER_RP,PCODE2,FROM_SYS,BB_BIN,BB_BIN_ORIG,CONTENT_FUND,TXNSRC,ACQ_COUNTRY,POS_ENTRY_CODE,POS_CONDITION_CODE," +
//            "  ADDRESPONSE,MVV,F4,F5,F6,F49,SETTLEMENT_CODE,SETTLEMENT_RATE,ISS_CONV_RATE,TCC," +
//            "  REFNUM,TRANDATE,TRANTIME,ACCEPTORNAME,TERMLOC,F15,PCODE_ORIG,ACCOUNT_NO,DEST_ACCOUNT,INS_PCODE" +
//            ") " +
//            "SELECT " +
//            "  1 AS DATA_ID, " +
//            "  CAST(PROC_CODE AS DECIMAL(18,6)) AS PPCODE, " +
//            "  (@row := @row + 1) + ? AS STT, " +                // rownum + iSTT
//            "  210 AS MSGTYPE, " +
//            "  CARD_NO AS PAN, " +
//            "  CAST(PROC_CODE AS DECIMAL(18,6)) AS PCODE, " +
//            "  /* AMOUNT: bỏ 2 số lẻ cuối, rỗng -> 0 */ " +
//            "  COALESCE(CAST(SUBSTRING(AMOUNT, 1, CHAR_LENGTH(AMOUNT)-2) AS DECIMAL(26,8)), 0) AS AMOUNT, " +
//            "  704 AS ACQ_CURRENCY_CODE, " +
//            "  CAST(CONCAT('2', TRACE_NO) AS DECIMAL(18,6)) AS TRACE, " +
//            "  CAST(LOCAL_TIME AS DECIMAL(18,6)) AS LOCAL_TIME, " +
//            "  /* NP_CONVERT_LOCAL_DATE(LOCAL_DATE, TRUNC(SYSDATE)) */ " +
//            "  CASE " +
//            "    WHEN LOCAL_DATE IS NULL THEN CURDATE() " +
//            "    /* TODO_LOCAL_DATE_FORMAT: nếu LOCAL_DATE là chuỗi 'YYYYMMDD' -> STR_TO_DATE */ " +
//            "    WHEN LOCAL_DATE REGEXP '^[0-9]{8}$' THEN STR_TO_DATE(LOCAL_DATE,'%Y%m%d') " +
//            "    ELSE LOCAL_DATE " + // nếu đã là DATETIME
//            "  END AS LOCAL_DATE, " +
//            "  /* NP_CONVERT_LOCAL_DATE(SETTLE_DATE, TRUNC(SYSDATE)) */ " +
//            "  CASE " +
//            "    WHEN SETTLE_DATE IS NULL THEN CURDATE() " +
//            "    WHEN SETTLE_DATE REGEXP '^[0-9]{8}$' THEN STR_TO_DATE(SETTLE_DATE,'%Y%m%d') " +
//            "    ELSE SETTLE_DATE " +
//            "  END AS SETTLEMENT_DATE, " +
//            "  CAST(ACQ_ID AS DECIMAL(18,6)) AS ACQUIRER, " +
//            "  CAST(ACQ_ID AS DECIMAL(18,6)) AS ISSUER, " +
//            "  /* RESPCODE rules */ " +
//            "  CAST((CASE " +
//            "    WHEN BEN_ID = '971133' AND (DEST_ACCOUNT LIKE 'NPDC%' OR DEST_ACCOUNT LIKE 'NQ%') THEN RESPONSE_CODE " +
//            "    WHEN SERVICE_CODE = 'QR_PUSH' THEN RESPONSE_CODE " +
//            "    WHEN BEN_ID = '971100' AND TCC = '99' THEN RESPONSE_CODE " +
//            "    WHEN TRIM(ISS_ID) IN ('980471','980472') THEN RESPONSE_CODE " +
//            "    WHEN RESPONSE_CODE = '68' THEN '0' " +
//            "    ELSE RESPONSE_CODE " +
//            "  END) AS DECIMAL(18,6)) AS RESPCODE, " +
//            "  6011 AS MERCHANT_TYPE, " +
//            "  CAST(MCC AS DECIMAL(18,6)) AS MERCHANT_TYPE_ORIG, " +
//            "  APPROVAL_CODE AS AUTHNUM, " +
//            "  704 AS SETT_CURRENCY_CODE, " +
//            "  TERM_ID AS TERMID, " +
//            "  ADD_INFO, " +
//            "  CONCAT(COALESCE(ACCOUNT_NO,' '), '|', COALESCE(DEST_ACCOUNT,'')) AS ACCTNUM, " +
//            "  704 AS ISS_CURRENCY_CODE, " +
//            "  CAST(TRACE_NO AS DECIMAL(18,6)) AS ORIGTRACE, " +
//            "  CAST(ACQ_ID AS DECIMAL(18,6)) AS ORIGISS, " +
//            "  CAST(COALESCE(NULLIF(ORIGRESPCODE, ''), '97') AS DECIMAL(18,6)) AS ORIGRESPCODE, " +
//            "  704 AS CH_CURRENCY_CODE, " +
//            "  /* ACQUIRER_FE */ " +
//            "  CAST((CASE " +
//            "    WHEN CAST(ACQ_ID AS UNSIGNED) = 191919 THEN 970459 " +
//            "    WHEN CAST(ACQ_ID AS UNSIGNED) = 970415 THEN 970489 " +
//            "    /* TODO_MAP_IBFT_ACQ_ID: JOIN bảng map nếu có */ " +
//            "    ELSE CAST(ACQ_ID AS UNSIGNED) " +
//            "  END) AS DECIMAL(18,6)) AS ACQUIRER_FE, " +
//            "  /* ACQUIRER_RP */ " +
//            "  CAST((CASE " +
//            "    WHEN TRIM(ISS_ID) = '980471' THEN 980471 " +
//            "    WHEN TRIM(ISS_ID) = '980475' THEN 980478 " +
//            "    WHEN CAST(ACQ_ID AS UNSIGNED) = 191919 THEN 970459 " +
//            "    WHEN CAST(ACQ_ID AS UNSIGNED) = 970415 THEN 970489 " +
//            "    /* TODO_MAP_IBFT_ACQ_ID */ " +
//            "    ELSE CAST(ACQ_ID AS UNSIGNED) " +
//            "  END) AS DECIMAL(18,6)) AS ACQUIRER_RP, " +
//            "  /* ISSUER_FE */ " +
//            "  CAST((CASE " +
//            "    WHEN CAST(ACQ_ID AS UNSIGNED) = 191919 THEN 970459 " +
//            "    WHEN CAST(ACQ_ID AS UNSIGNED) = 970415 THEN 970489 " +
//            "    /* TODO_MAP_IBFT_ACQ_ID */ " +
//            "    ELSE CAST(ACQ_ID AS UNSIGNED) " +
//            "  END) AS DECIMAL(18,6)) AS ISSUER_FE, " +
//            "  /* ISSUER_RP */ " +
//            "  CAST((CASE " +
//            "    WHEN TRIM(ISS_ID) = '980471' THEN 980471 " +
//            "    WHEN TRIM(ISS_ID) = '980475' THEN 980478 " +
//            "    WHEN CAST(ACQ_ID AS UNSIGNED) = 191919 THEN 970459 " +
//            "    WHEN CAST(ACQ_ID AS UNSIGNED) = 970415 THEN 970489 " +
//            "    /* TODO_MAP_IBFT_ACQ_ID */ " +
//            "    ELSE CAST(ACQ_ID AS UNSIGNED) " +
//            "  END) AS DECIMAL(18,6)) AS ISSUER_RP, " +
//            "  /* PCODE2 theo TCC/SERVICE_CODE */ " +
//            "  CAST((CASE " +
//            "    WHEN TCC = '99' THEN 930000 " +
//            "    WHEN TCC = '95' THEN 950000 " +
//            "    WHEN SERVICE_CODE = 'QR_PUSH' THEN 890000 " +
//            "    WHEN TCC = '97' THEN 720000 " +
//            "    WHEN TCC = '98' THEN 730000 " +
//            "    ELSE 910000 " +
//            "  END) AS DECIMAL(18,6)) AS PCODE2, " +
//            "  'IBT' AS FROM_SYS, " +
//            "  /* BB_BIN */ " +
//            "  CAST((CASE " +
//            "    WHEN TRIM(ISS_ID) = '980472' THEN 980471 " +
//            "    WHEN TRIM(ISS_ID) = '980474' THEN 980478 " + // IBFT2.0
//            "    WHEN BEN_ID IS NOT NULL AND PROC_CODE IN ('912020','910020') THEN /* TODO_GET_IBT_BIN(BEN_ID) */ NULL " +
//            "    ELSE /* TODO_GET_IBT_BIN(SUBSTR(DEST_ACCOUNT,1,6)) */ NULL " +
//            "  END) AS DECIMAL(18,6)) AS BB_BIN, " +
//            "  /* BB_BIN_ORIG <- 'BEN_ID' hoặc map */ " +
//            "  CAST((CASE " +
//            "    WHEN TRIM(ISS_ID) IN ('980472','980474') THEN " +
//            "       (CASE WHEN BEN_ID IS NOT NULL AND PROC_CODE IN ('912020','910020') THEN NULL /* TODO_GET_IBT_BIN(BEN_ID) */ " +
//            "             ELSE NULL /* TODO_GET_IBT_BIN(SUBSTR(DEST_ACCOUNT,1,6)) */ END) " +
//            "    ELSE (CASE WHEN BEN_ID REGEXP '^[0-9]+$' THEN CAST(BEN_ID AS DECIMAL(18,6)) ELSE NULL END) " +
//            "  END) AS DECIMAL(18,6)) AS BB_BIN_ORIG, " +
//            "  /* CONTENT_FUND <- IBFT_INFO ; TXNSRC <- VAS_INFO */ " +
//            "  IBFT_INFO AS CONTENT_FUND, " +
//            "  VAS_INFO AS TXNSRC, " +
//            "  ACQ_COUNTRY, POS_ENTRY_CODE, POS_CONDITION_CODE, ADDRESPONSE, MVV, " +
//            "  /* F4/F5/F6: bỏ 2 số lẻ cuối */ " +
//            "  COALESCE(CAST(SUBSTRING(F4,1,CHAR_LENGTH(F4)-2) AS DECIMAL(26,8)),0) AS F4, " +
//            "  COALESCE(CAST(SUBSTRING(F5,1,CHAR_LENGTH(F5)-2) AS DECIMAL(26,8)),0) AS F5, " +
//            "  COALESCE(CAST(SUBSTRING(F6,1,CHAR_LENGTH(F6)-2) AS DECIMAL(26,8)),0) AS F6, " +
//            "  F49, SETTLEMENT_CODE, SETTLEMENT_RATE, ISS_CONV_RATE, TCC, " +
//            "  REF_NO AS REFNUM, DATE(TNX_STAMP) AS TRANDATE, DATE_FORMAT(TNX_STAMP,'%H%i%s') AS TRANTIME, " +
//            "  CARD_ACCEPT_NAME_LOCATION AS ACCEPTORNAME, CARD_ACCEPT_ID_CODE AS TERMLOC, " +
//            "  /* F15 <- SETTLEMENT_DATE (ISO field 15) */ " +
//            "  CASE WHEN SETTLE_DATE IS NULL THEN CURDATE() " +
//            "       WHEN SETTLE_DATE REGEXP '^[0-9]{8}$' THEN STR_TO_DATE(SETTLE_DATE,'%Y%m%d') ELSE SETTLE_DATE END AS F15, " +
//            "  CAST(PROC_CODE AS DECIMAL(18,6)) AS PCODE_ORIG, " +
//            "  ACCOUNT_NO, DEST_ACCOUNT, SUBSTRING(OF_YEAR,1,2) AS INS_PCODE " +
//            "FROM ISOMESSAGE_TMP_TURN " +
//            "WHERE MTI = '0210' " +
//            "  AND (COALESCE(BEN_ID,'0') REGEXP '^[0-9]+$') " +
//            "  AND (TRACE_NO REGEXP '^[0-9]+$')";

        final String sql = """
        		INSERT INTO SHCLOG_SETT_IBFT (
        		  DATA_ID, PPCODE, STT, MSGTYPE, PAN, PCODE, AMOUNT, ACQ_CURRENCY_CODE, TRACE,
        		  LOCAL_TIME, LOCAL_DATE, SETTLEMENT_DATE, ACQUIRER, ISSUER, RESPCODE, MERCHANT_TYPE,
        		  MERCHANT_TYPE_ORIG, AUTHNUM, SETT_CURRENCY_CODE, TERMID, ADD_INFO, ACCTNUM,
        		  ISS_CURRENCY_CODE, ORIGTRACE, ORIGISS, ORIGRESPCODE, CH_CURRENCY_CODE,
        		  ACQUIRER_FE, ACQUIRER_RP, ISSUER_FE, ISSUER_RP, PCODE2, FROM_SYS, BB_BIN,
        		  BB_BIN_ORIG, CONTENT_FUND, TXNSRC, ACQ_COUNTRY, POS_ENTRY_CODE,
        		  POS_CONDITION_CODE, ADDRESPONSE, MVV, F4, F5, F6, F49, SETTLEMENT_CODE,
        		  SETTLEMENT_RATE, ISS_CONV_RATE, TCC, REFNUM, TRANDATE, TRANTIME, ACCEPTORNAME,
        		  TERMLOC, F15, PCODE_ORIG, ACCOUNT_NO, DEST_ACCOUNT, INS_PCODE
        		)
        		SELECT
        		  1,                                -- DATA_ID
        		  r.PROC_CODE_DEC,                  -- PPCODE
        		  CAST(? + r.rn AS DECIMAL(18,6)),  -- STT = iSTT + row_number()
        		  210,                              -- MSGTYPE
        		  SUBSTRING(r.CARD_NO,1,19),        -- PAN (char(19))
        		  r.PROC_CODE_DEC,                  -- PCODE
        		  r.AMOUNT_DIV100,                  -- AMOUNT
        		  704,                              -- ACQ_CURRENCY_CODE
        		  r.TRACE_CONCAT2,                  -- TRACE
        		  r.LOCAL_TIME_DEC,                 -- LOCAL_TIME
        		  r.LOCAL_DATE_D,                   -- LOCAL_DATE
        		  r.SETTLE_DATE_D,                  -- SETTLEMENT_DATE
        		  r.ACQ_RAW,                        -- ACQUIRER
        		  r.ACQ_RAW,                        -- ISSUER
        		  r.RESPCODE_SEED,                  -- RESPCODE (0/68)
        		  6011,                             -- MERCHANT_TYPE
        		  r.MCC_DEC,                        -- MERCHANT_TYPE_ORIG
        		  SUBSTRING(r.APPROVAL_CODE,1,6),   -- AUTHNUM (char(6))
        		  704,                              -- SETT_CURRENCY_CODE
        		  SUBSTRING(r.TERM_ID,1,8),         -- TERMID (char(8))
        		  r.ADD_INFO,                       -- ADD_INFO
        		  SUBSTRING(CONCAT(COALESCE(r.ACCOUNT_NO,' '),'|',COALESCE(r.DEST_ACCOUNT,'')),1,70) AS ACCTNUM, -- char(70)
        		  704,                              -- ISS_CURRENCY_CODE
        		  r.ORIGTRACE_DEC,                  -- ORIGTRACE
        		  CAST(r.ACQ_INT AS CHAR(10)),      -- ORIGISS (char(10)) ← FIX tràn độ dài
        		  97,                               -- ORIGRESPCODE
        		  704,                              -- CH_CURRENCY_CODE
        		  r.ACQ_FE, r.ACQ_RP, r.ISS_FE, r.ISS_RP,
        		  r.PCODE2_VAL, 'IBT',
        		  r.BB_BIN_VAL, r.BB_BIN_ORIG_VAL,
        		  r.IBFT_INFO,                      -- CONTENT_FUND
        		  'MTI=200',                        -- TXNSRC (giữ đúng store gốc)
        		  r.ACQ_COUNTRY, r.POS_ENTRY_CODE, r.POS_CONDITION_CODE,
        		  r.ADDRESPONSE, r.MVV,
        		  r.F4_D, r.F5_D, r.F6_D,
        		  r.F49, r.SETTLEMENT_CODE, r.SETTLEMENT_RATE, r.ISS_CONV_RATE, r.TCC,
        		  SUBSTRING(r.REF_NO,1,12),         -- REFNUM (char(12))
        		  DATE(r.TNX_STAMP),                -- TRANDATE
        		  CAST(DATE_FORMAT(r.TNX_STAMP,'%H%i%s') AS DECIMAL(18,6)), -- TRANTIME (DECIMAL)
        		  SUBSTRING(r.CARD_ACCEPT_NAME_LOCATION,1,40), -- ACCEPTORNAME (char(40))
        		  SUBSTRING(r.CARD_ACCEPT_ID_CODE,1,25),       -- TERMLOC (char(25))
        		  r.SETTLE_DATE_D,                  -- F15
        		  r.PROC_CODE_DEC,                  -- PCODE_ORIG
        		  r.ACCOUNT_NO, r.DEST_ACCOUNT,
        		  r.INS_PCODE_DEC                   -- INS_PCODE
        		FROM (
        		  SELECT s.*,
        		         ROW_NUMBER() OVER (ORDER BY s.TNX_STAMP, s.TRACE_NO_U) AS rn
        		  FROM (
        		    SELECT
        		      t.*,
        		      /* ép kiểu an toàn */
        		      CASE WHEN t.PROC_CODE REGEXP '^[0-9]+$' THEN CAST(t.PROC_CODE AS DECIMAL(18,6)) ELSE 0 END AS PROC_CODE_DEC,
        		      CASE WHEN t.TRACE_NO  REGEXP '^[0-9]+$' THEN CAST(t.TRACE_NO  AS UNSIGNED)      ELSE NULL END AS TRACE_NO_U,
        		      CASE WHEN t.LOCAL_TIME REGEXP '^[0-9]+$' THEN CAST(t.LOCAL_TIME AS DECIMAL(18,6)) ELSE NULL END AS LOCAL_TIME_DEC,
        		      CASE WHEN t.MCC REGEXP '^[0-9]+$' THEN CAST(t.MCC AS DECIMAL(18,6)) ELSE NULL END AS MCC_DEC,
        		      /* LOCAL_DATE/SETTLE_DATE: 'YYYYMMDD' hoặc 'MMDD' -> DATE; mặc định CURDATE() */
        		      CASE
        		        WHEN t.LOCAL_DATE  REGEXP '^[0-9]{8}$' THEN STR_TO_DATE(t.LOCAL_DATE,'%Y%m%d')
        		        WHEN t.LOCAL_DATE  REGEXP '^[0-9]{4}$' THEN STR_TO_DATE(CONCAT(YEAR(CURDATE()), t.LOCAL_DATE),'%Y%m%d')
        		        ELSE CURDATE()
        		      END AS LOCAL_DATE_D,
        		      CASE
        		        WHEN t.SETTLE_DATE REGEXP '^[0-9]{8}$' THEN STR_TO_DATE(t.SETTLE_DATE,'%Y%m%d')
        		        WHEN t.SETTLE_DATE REGEXP '^[0-9]{4}$' THEN STR_TO_DATE(CONCAT(YEAR(CURDATE()), t.SETTLE_DATE),'%Y%m%d')
        		        ELSE CURDATE()
        		      END AS SETTLE_DATE_D,
        		      /* số tiền/F4/F5/F6: bỏ 2 số lẻ cuối */
        		      CASE WHEN t.AMOUNT IS NULL THEN 0 ELSE TRUNCATE(t.AMOUNT/100, 0) END AS AMOUNT_DIV100,
        		      CASE WHEN t.F4     IS NULL THEN 0 ELSE TRUNCATE(t.F4    /100, 0) END AS F4_D,
        		      CASE WHEN t.F5     IS NULL THEN 0 ELSE TRUNCATE(t.F5    /100, 0) END AS F5_D,
        		      CASE WHEN t.F6     IS NULL THEN 0 ELSE TRUNCATE(t.F6    /100, 0) END AS F6_D,
        		      CAST(t.ACQ_ID AS DECIMAL(18,6)) AS ACQ_RAW,
        		      CASE WHEN t.ACQ_ID REGEXP '^[0-9]+$' THEN CAST(t.ACQ_ID AS UNSIGNED) ELSE NULL END AS ACQ_INT, -- dùng cho ORIGISS
        		      CASE WHEN t.TRACE_NO REGEXP '^[0-9]+$' THEN CAST(CONCAT('2', t.TRACE_NO) AS DECIMAL(18,6)) ELSE NULL END AS TRACE_CONCAT2,
        		      CASE WHEN t.TRACE_NO REGEXP '^[0-9]+$' THEN CAST(t.TRACE_NO AS DECIMAL(18,6)) ELSE NULL END AS ORIGTRACE_DEC,
        		      /* RESPCODE seed */
        		      CASE
        		        WHEN t.BEN_ID = '971133' AND (t.DEST_ACCOUNT LIKE 'NPDC%' OR t.DEST_ACCOUNT LIKE 'NQ%') THEN 68
        		        WHEN t.SERVICE_CODE = 'QR_PUSH' THEN 68
        		        WHEN t.BEN_ID = '971100' AND t.TCC = '99' THEN 68
        		        WHEN TRIM(t.ISS_ID) IN ('980471','980472') THEN 68
        		        ELSE 0
        		      END AS RESPCODE_SEED,
        		      /* PCODE2 */
        		      CASE
        		        WHEN t.TCC='99' THEN 930000
        		        WHEN t.TCC='95' THEN 950000
        		        WHEN t.SERVICE_CODE='QR_PUSH' THEN 890000
        		        WHEN t.TCC='97' THEN 720000
        		        WHEN t.TCC='98' THEN 730000
        		        ELSE 910000
        		      END AS PCODE2_VAL,
        		      /* *_FE/RP */
        		      CASE WHEN CAST(t.ACQ_ID AS UNSIGNED)=191919 THEN 970459
        		           WHEN CAST(t.ACQ_ID AS UNSIGNED)=970415 THEN 970489
        		           ELSE CAST(t.ACQ_ID AS UNSIGNED) END AS ACQ_FE,
        		      CASE WHEN TRIM(t.ISS_ID)='980471' THEN 980471
        		           WHEN TRIM(t.ISS_ID)='980475' THEN 980478
        		           WHEN CAST(t.ACQ_ID AS UNSIGNED)=191919 THEN 970459
        		           WHEN CAST(t.ACQ_ID AS UNSIGNED)=970415 THEN 970489
        		           ELSE CAST(t.ACQ_ID AS UNSIGNED) END AS ACQ_RP,
        		      CASE WHEN CAST(t.ACQ_ID AS UNSIGNED)=191919 THEN 970459
        		           WHEN CAST(t.ACQ_ID AS UNSIGNED)=970415 THEN 970489
        		           ELSE CAST(t.ACQ_ID AS UNSIGNED) END AS ISS_FE,
        		      CASE WHEN TRIM(t.ISS_ID)='980471' THEN 980471
        		           WHEN TRIM(t.ISS_ID)='980475' THEN 980478
        		           WHEN CAST(t.ACQ_ID AS UNSIGNED)=191919 THEN 970459
        		           WHEN CAST(t.ACQ_ID AS UNSIGNED)=970415 THEN 970489
        		           ELSE CAST(t.ACQ_ID AS UNSIGNED) END AS ISS_RP,
        		      /* BB_BIN & ORIG */
        		      CASE
        		        WHEN TRIM(t.ISS_ID)='980472' THEN 980471
        		        WHEN TRIM(t.ISS_ID)='980474' THEN 980478
        		        WHEN t.BEN_ID IS NOT NULL AND t.PROC_CODE IN ('912020','910020') THEN CAST(t.BEN_ID AS UNSIGNED)
        		        ELSE CAST(SUBSTRING(t.DEST_ACCOUNT,1,6) AS UNSIGNED)
        		      END AS BB_BIN_VAL,
        		      CASE
        		        WHEN TRIM(t.ISS_ID) IN ('980472','980474','980475') THEN
        		          CASE WHEN t.BEN_ID IS NOT NULL AND t.PROC_CODE IN ('912020','910020')
        		               THEN CAST(t.BEN_ID AS UNSIGNED)
        		               ELSE CAST(SUBSTRING(t.DEST_ACCOUNT,1,6) AS UNSIGNED)
        		          END
        		        ELSE CASE WHEN t.BEN_ID REGEXP '^[0-9]+$' THEN CAST(t.BEN_ID AS UNSIGNED) ELSE NULL END
        		      END AS BB_BIN_ORIG_VAL,
        		      /* INS_PCODE */
        		      CASE WHEN t.OF_YEAR REGEXP '^[0-9]{2,}$'
        		           THEN CAST(SUBSTRING(t.OF_YEAR,1,2) AS DECIMAL(18,6)) ELSE 0 END AS INS_PCODE_DEC
        		    FROM ISOMESSAGE_TMP_TURN t
        		    WHERE t.MTI='0200'              /* đúng thủ tục gốc */
        		      AND t.CARD_NO IS NOT NULL
        		      AND COALESCE(t.BEN_ID,'0') REGEXP '^[0-9]+$'
        		      AND t.TRACE_NO REGEXP '^[0-9]+$'
        		  ) s
        		) r
        		ON DUPLICATE KEY UPDATE
        		  RESPCODE = CASE WHEN (AMOUNT <> VALUES(AMOUNT)) AND RESPCODE = 0 THEN 116 ELSE RESPCODE END,
        		  TXNSRC   = CASE WHEN (AMOUNT <> VALUES(AMOUNT)) THEN 'RC=99' ELSE TXNSRC END,
        		  CONTENT_FUND = VALUES(CONTENT_FUND)
        		""";

        		// iStt = SELECT IFNULL(MAX(STT),0) FROM SHCLOG_SETT_IBFT
//        		jdbcTemplate.update(sql, iStt);

        
        
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
