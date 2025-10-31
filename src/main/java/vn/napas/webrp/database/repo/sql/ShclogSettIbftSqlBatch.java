package vn.napas.webrp.database.repo.sql;



import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.napas.webrp.report.util.SqlLogUtils;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Repo batch chuyên trách cập nhật SEQUENCE_IN_MONTH cho bảng SHCLOG_SETT_IBFT.
 * - TiDB/MySQL: chuyển từ MERGE (Oracle) sang UPDATE ... JOIN + window function.
 * - Có 2 entrypoints:
 *   1) recalcAll(): chạy toàn bộ phạm vi như lệnh gốc (không filter theo ngày)
 *   2) recalcForRange(String fromDate, String toDate): chạy theo khoảng ngày (tùy chọn)
 *
 * Lưu ý:
 * - LOCAL_DATE/LOCAL_TIME nếu là kiểu VARCHAR trong DB thì truyền String (yyyyMMdd / HHmmss).
 * - Nếu là DATE/TIME hay DATETIME, hãy đổi kiểu tham số tương ứng và sửa biểu thức WHERE.
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class ShclogSettIbftSqlBatch {

    private final NamedParameterJdbcTemplate jdbc;

    // =========================
    // SQL: Bản ĐÚNG LOGIC GỐC (không giới hạn range)
    // =========================
    private static final String SQL_UPDATE_ALL = """
        UPDATE SHCLOG_SETT_IBFT AS A
        JOIN (
          SELECT
              T.PAN, T.AMOUNT, T.LOCAL_DATE, T.LOCAL_TIME, T.ORIGTRACE,
              T.ISSUER_RP, T.TERMID, T.BB_BIN,
              (
                ROW_NUMBER() OVER (
                  PARTITION BY T.ISSUER_RP
                  ORDER BY T.LOCAL_DATE, T.LOCAL_TIME, T.ORIGTRACE
                )
                + IFNULL(S.SEQUENCE_START, 0)
                + IFNULL(S.TRANSACTION_SUM, 0)
              ) AS SEQUENCE_TMP
          FROM (
              SELECT PAN, AMOUNT, LOCAL_DATE, LOCAL_TIME, ORIGTRACE, ISSUER_RP, TERMID, BB_BIN, ACCOUNT_NO
              FROM SHCLOG_SETT_IBFT
              WHERE PCODE2 IN (910000, 930000, 950000)
                AND RESPCODE = 0
                AND NOT (
                      IFNULL(ISSUER_RP,0) IN (980471, 980478)
                  OR  IFNULL(BB_BIN,0) = 971100
                  OR (IFNULL(ISSUER_RP,0) = 970412 AND ACCOUNT_NO = '110014886114')
                )
          ) AS T
          LEFT JOIN FEE_TIER_IN_MONTH AS S
                 ON T.ISSUER_RP = S.ISSUER_RP
        ) AS B
          ON  A.PAN        = B.PAN
          AND A.AMOUNT     = B.AMOUNT
          AND A.LOCAL_DATE = B.LOCAL_DATE
          AND A.LOCAL_TIME = B.LOCAL_TIME
          AND A.ORIGTRACE  = B.ORIGTRACE
          AND A.ISSUER_RP  = B.ISSUER_RP
          AND A.TERMID     = B.TERMID
          AND IFNULL(A.BB_BIN,0) = IFNULL(B.BB_BIN,0)
        SET A.SEQUENCE_IN_MONTH = B.SEQUENCE_TMP
        WHERE A.PCODE2 IN (910000, 930000, 950000)
          AND A.RESPCODE = 0
          AND NOT (
                IFNULL(A.ISSUER_RP,0) IN (980471, 980478)
            OR  IFNULL(A.BB_BIN,0) = 971100
            OR (IFNULL(A.ISSUER_RP,0) = 970412 AND A.ACCOUNT_NO = '110014886114')
          );
        """;

    // =========================
    // SQL: Bản CÓ RANGE (tùy chọn, chạy batch theo ngày)
    // fromDate/toDate truyền đúng định dạng cột LOCAL_DATE của bạn (ví dụ '20250801'..'20250831')
    // =========================
    private static final String SQL_UPDATE_WITH_RANGE = """
        UPDATE SHCLOG_SETT_IBFT AS A
        JOIN (
          SELECT
              T.PAN, T.AMOUNT, T.LOCAL_DATE, T.LOCAL_TIME, T.ORIGTRACE,
              T.ISSUER_RP, T.TERMID, T.BB_BIN,
              (
                ROW_NUMBER() OVER (
                  PARTITION BY T.ISSUER_RP
                  ORDER BY T.LOCAL_DATE, T.LOCAL_TIME, T.ORIGTRACE
                )
                + IFNULL(S.SEQUENCE_START, 0)
                + IFNULL(S.TRANSACTION_SUM, 0)
              ) AS SEQUENCE_TMP
          FROM (
              SELECT PAN, AMOUNT, LOCAL_DATE, LOCAL_TIME, ORIGTRACE, ISSUER_RP, TERMID, BB_BIN, ACCOUNT_NO
              FROM SHCLOG_SETT_IBFT
              WHERE PCODE2 IN (910000, 930000, 950000)
                AND RESPCODE = 0
                AND NOT (
                      IFNULL(ISSUER_RP,0) IN (980471, 980478)
                  OR  IFNULL(BB_BIN,0) = 971100
                  OR (IFNULL(ISSUER_RP,0) = 970412 AND ACCOUNT_NO = '110014886114')
                )
                AND LOCAL_DATE BETWEEN :fromDate AND :toDate
          ) AS T
          LEFT JOIN FEE_TIER_IN_MONTH AS S
                 ON T.ISSUER_RP = S.ISSUER_RP
        ) AS B
          ON  A.PAN        = B.PAN
          AND A.AMOUNT     = B.AMOUNT
          AND A.LOCAL_DATE = B.LOCAL_DATE
          AND A.LOCAL_TIME = B.LOCAL_TIME
          AND A.ORIGTRACE  = B.ORIGTRACE
          AND A.ISSUER_RP  = B.ISSUER_RP
          AND A.TERMID     = B.TERMID
          AND IFNULL(A.BB_BIN,0) = IFNULL(B.BB_BIN,0)
        SET A.SEQUENCE_IN_MONTH = B.SEQUENCE_TMP
        WHERE A.PCODE2 IN (910000, 930000, 950000)
          AND A.RESPCODE = 0
          AND NOT (
                IFNULL(A.ISSUER_RP,0) IN (980471, 980478)
            OR  IFNULL(A.BB_BIN,0) = 971100
            OR (IFNULL(A.ISSUER_RP,0) = 970412 AND A.ACCOUNT_NO = '110014886114')
          )
          AND A.LOCAL_DATE BETWEEN :fromDate AND :toDate;
        """;

    private static final String SQL_SYNC_FEE_PKG = """
            UPDATE SHCLOG_SETT_IBFT AS A
            JOIN RP_INSTITUTION AS B
              ON (CASE
                    WHEN A.ISSUER_RP IN (980471, 980478) THEN A.ISSUER
                    ELSE A.ISSUER_RP
                  END) = B.SHCLOG_ID
            SET A.FEE_PACKAGE_TYPE = B.FEE_PACKAGE_TYPE,
                A.INS_TYPE_FEE     = B.INS_TYPE_FEE;
            """;

        // ====== SQL: bản ép kiểu an toàn (nếu hai cột khác kiểu) ======
        private static final String SQL_SYNC_FEE_PKG_CAST = """
            UPDATE SHCLOG_SETT_IBFT AS A
            JOIN RP_INSTITUTION AS B
              ON CAST(CASE
                        WHEN A.ISSUER_RP IN (980471, 980478) THEN A.ISSUER
                        ELSE A.ISSUER_RP
                      END AS CHAR(32)) = CAST(B.SHCLOG_ID AS CHAR(32))
            SET A.FEE_PACKAGE_TYPE = B.FEE_PACKAGE_TYPE,
                A.INS_TYPE_FEE     = B.INS_TYPE_FEE;
            """;
    
    
    /**
     * Chạy đúng như lệnh MERGE gốc (không giới hạn phạm vi) 19.stt_100100400170.sql.
     * Cân nhắc thời gian chạy & mức khóa. Nên chạy ngoài giờ hoặc trên snapshot phù hợp.
     * @return số dòng được cập nhật
     */
    @Transactional/*(timeout = 600)*/  // có thể đặt timeout tùy môi trường
    public int maskFeeTransactionAll() {
        long start = System.currentTimeMillis();
        try {
//        	SqlLogUtils.renderSql(SQL_UPDATE_ALL, new ma);
        	log.info("Sql: " + SQL_UPDATE_ALL);
            int updated = jdbc.update(SQL_UPDATE_ALL, new MapSqlParameterSource());
            log.info("recalcAll() updated {} rows in {} ms", updated, (System.currentTimeMillis() - start));
            return updated;
        } catch (DataAccessException ex) {
            log.error("recalcAll() failed: {}", ex.getMessage(), ex);
            throw ex;
        }
    }

    /**
     * Chạy theo khoảng ngày (tùy chọn) để giảm phạm vi cập nhật.
     * fromDate/toDate: truyền đúng định dạng của cột LOCAL_DATE (ví dụ '20250801').
     * @return số dòng được cập nhật
     */
    @Transactional/*(timeout = 600)*/
    public int recalcForRange(String fromDate, String toDate) {
        long start = System.currentTimeMillis();
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("fromDate", fromDate)
                .addValue("toDate",   toDate);
        try {
            int updated = jdbc.update(SQL_UPDATE_WITH_RANGE, params);
            log.info("recalcForRange({}, {}) updated {} rows in {} ms",
                    fromDate, toDate, updated, (System.currentTimeMillis() - start));
            return updated;
        } catch (DataAccessException ex) {
            log.error("recalcForRange({}, {}) failed: {}", fromDate, toDate, ex.getMessage(), ex);
            throw ex;
        }
    }
    
    /**
     * 20.stt_100100400180.sql
     * @param forceCast
     * @return
     */
    @Transactional/*(timeout = 300)*/
    
    public int syncTransactionFeePackageFromInstitution(boolean forceCast) {
        long start = System.currentTimeMillis();
        String sql = SQL_SYNC_FEE_PKG;
        try {
            int updated = jdbc.update(sql, new MapSqlParameterSource());
            log.info("syncTransactionFeePackageFromInstitution(forceCast={}) updated {} rows in {} ms",
                    forceCast, updated, (System.currentTimeMillis() - start));
            return updated;
        } catch (Exception ex) {
            log.error("syncFeePackageFromInstitution(forceCast={}) failed: {}", forceCast, ex.getMessage(), ex);
            throw ex;
        }
    }
    
    
    private static final String SQL_UPDATE_ISSUER_FE = """
            UPDATE SHCLOG_SETT_IBFT
            SET ISSUER_FE = CASE
              WHEN IFNULL(ISSUER_RP,0) = 970412 AND ACCOUNT_NO = '110014886114' THEN 128050
              WHEN ACQUIRER_RP <> 605609
                   AND IFNULL(PCODE2,0) NOT IN (890000, 720000, 730000)
                   AND IFNULL(BB_BIN,0) NOT IN (971101, 971100)
                   AND SUBSTR(LPAD(CAST(PCODE AS CHAR), 6, '0'), 1, 2) IN ('42','91')
                   AND ISSUER_RP NOT IN (980471, 980478)
              THEN
                CASE
                  WHEN Amount BETWEEN 1 AND 499999999 AND SEQUENCE_IN_MONTH IS NOT NULL THEN
                    CASE
                      WHEN SEQUENCE_IN_MONTH BETWEEN 0 AND 10  AND FEE_PACKAGE_TYPE IS NULL THEN 130004
                      WHEN SEQUENCE_IN_MONTH BETWEEN 11 AND 20 AND FEE_PACKAGE_TYPE IS NULL THEN 130005
                      WHEN SEQUENCE_IN_MONTH BETWEEN 21 AND 40 AND FEE_PACKAGE_TYPE IS NULL THEN 130006
                      WHEN SEQUENCE_IN_MONTH BETWEEN 41 AND 60 AND FEE_PACKAGE_TYPE IS NULL THEN 130007
                      WHEN SEQUENCE_IN_MONTH BETWEEN 61 AND 80 AND FEE_PACKAGE_TYPE IS NULL THEN 130013
                      WHEN SEQUENCE_IN_MONTH BETWEEN 81 AND 100 AND FEE_PACKAGE_TYPE IS NULL THEN 130015
                      WHEN SEQUENCE_IN_MONTH BETWEEN 101 AND 120 AND FEE_PACKAGE_TYPE IS NULL THEN 130016
                      WHEN SEQUENCE_IN_MONTH > 120 AND FEE_PACKAGE_TYPE IS NULL THEN 130017
                      WHEN SEQUENCE_IN_MONTH BETWEEN 0 AND 10  AND FEE_PACKAGE_TYPE = 'G2_2022' THEN 130008
                      WHEN SEQUENCE_IN_MONTH BETWEEN 11 AND 20 AND FEE_PACKAGE_TYPE = 'G2_2022' THEN 130009
                      WHEN SEQUENCE_IN_MONTH BETWEEN 21 AND 40 AND FEE_PACKAGE_TYPE = 'G2_2022' THEN 130010
                      WHEN SEQUENCE_IN_MONTH BETWEEN 41 AND 60 AND FEE_PACKAGE_TYPE = 'G2_2022' THEN 130011
                      WHEN SEQUENCE_IN_MONTH BETWEEN 61 AND 80 AND FEE_PACKAGE_TYPE = 'G2_2022' THEN 130014
                      WHEN SEQUENCE_IN_MONTH BETWEEN 81 AND 100 AND FEE_PACKAGE_TYPE = 'G2_2022' THEN 130018
                      WHEN SEQUENCE_IN_MONTH BETWEEN 101 AND 120 AND FEE_PACKAGE_TYPE = 'G2_2022' THEN 130019
                      WHEN SEQUENCE_IN_MONTH > 120 AND FEE_PACKAGE_TYPE = 'G2_2022' THEN 130020
                    END
                END
              WHEN ISSUER_RP = 980471 THEN 980471
              WHEN ISSUER_RP = 980478 THEN 980478
              WHEN BB_BIN = 971100 THEN 971100
              WHEN BB_BIN = 971101 THEN 971101
              WHEN IFNULL(PCODE2,0) = 720000 THEN 128013
              WHEN IFNULL(PCODE2,0) = 730000 THEN
                CASE
                  WHEN INS_TYPE_FEE = 'TGTT' THEN
                    CASE
                      WHEN Amount <= 300000000 THEN 128015
                      WHEN Amount BETWEEN 300000001 AND 499999999 THEN 128016
                    END
                  ELSE
                    CASE
                      WHEN Amount <= 300000000 THEN 128017
                      WHEN Amount BETWEEN 300000001 AND 499999999 THEN 128018
                    END
                END
              ELSE ISSUER_FE
            END
            """;

        /**21.stt_100100400200.sql
         * @return
         */
        @Transactional/*(timeout = 600)*/
        public int updateIssuerFe() {
            long t0 = System.currentTimeMillis();
            int n = jdbc.update(SQL_UPDATE_ISSUER_FE, new MapSqlParameterSource());
            log.info("updateIssuerFe() updated {} rows in {} ms", n, (System.currentTimeMillis() - t0));
            return n;
        }
}
