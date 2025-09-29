package vn.napas.webrp.database.repo.store;






import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Repository chuyển từ store RPT.INSERT_TCKT_SESSION_DOMESTIC_IBFT
 * Mapping 1–1 các block trong store:
 *
 *  [STORE BLOCK: BEGIN_LOG]            -> logBegin()
 *  [STORE BLOCK: INSERT_SESSION_HEADER] -> insertSessionHeader(...)
 *  [STORE BLOCK: BACKUP_AUTO_TABLE]     -> backupAutoTable()        (nếu store có)
 *  [STORE BLOCK: INSERT_DOMESTIC_BODY]  -> insertDomesticBody(...)
 *  [STORE BLOCK: END_LOG]               -> logEnd()
 *  [STORE BLOCK: EXCEPTION]             -> logError(...), rethrow
 *
 * Lưu ý chuyển đổi Oracle -> MySQL/TiDB:
 *  - SYSDATE -> NOW()
 *  - TRUNC(col) -> DATE(col)
 *  - TO_DATE(:s,'dd/MM/yyyy') -> STR_TO_DATE(:s,'%d/%m/%Y')
 *  - NVL -> IFNULL, DECODE -> CASE
 *  - SUBSTR -> SUBSTRING, TO_CHAR+LPAD -> LPAD(CAST(... AS CHAR), ...)
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class Proc_INSERT_TCKT_SESSION_DOMESTIC_IBFT {

    private final NamedParameterJdbcTemplate jdbc;
    private static final DateTimeFormatter DMY = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // =========================
    // [STORE BLOCK: BEGIN_LOG]
    // =========================
    private static final String SQL_LOG_BEGIN = """
        INSERT INTO ERR_EX(ERR_TIME, ERR_CODE, ERR_DETAIL, ERR_MODULE)
        VALUES (NOW(), 0, 'Begin', 'INSERT_TCKT_SESSION_DOMESTIC_IBFT')
        """;

    // =======================================
    // [STORE BLOCK: INSERT_SESSION_HEADER]
    // =======================================
    // Ghi lại tham số phiên: p_from_date/p_to_date/p_user
    private static final String SQL_INSERT_SESSION_HEADER = """
        INSERT INTO TCKT_SESSION_DOMESTIC_IBFT(p_from_date, p_to_date, p_user, created_time)
        VALUES (STR_TO_DATE(:fromDateStr, '%d/%m/%Y'),
                STR_TO_DATE(:toDateStr,   '%d/%m/%Y'),
                :user, NOW())
        """;

    // ===================================
    // [STORE BLOCK: BACKUP_AUTO_TABLE]
    // ===================================
    // Nếu trong store có bước backup/rotate, nhúng SQL vào đây (có thể để trống nếu không dùng)
    private static final String SQL_BACKUP_AUTO_TABLE = """
        /* Optional backup – sửa/hoặc comment nếu không dùng trong môi trường của bạn */
        INSERT INTO TCKT_SESSION_DOMESTIC_AUTO_BKUP (bkup_time, p_from_date, p_to_date, p_user)
        SELECT NOW(), p_from_date, p_to_date, p_user
        FROM TCKT_SESSION_DOMESTIC_IBFT
        WHERE DATE(created_time) = CURRENT_DATE
        """;

    // ==================================
    // [STORE BLOCK: INSERT_DOMESTIC_BODY]
    // ==================================
    // TOÀN BỘ phần INSERT ... SELECT lớn từ store (đã convert Oracle -> TiDB/MySQL)
    // ===> DÁN NGUYÊN VĂN VÀO HẰNG NÀY.
    private static final String SQL_INSERT_DOMESTIC_BODY = """
        /* TODO: Paste full INSERT ... SELECT here (converted).
           Gợi ý điều kiện ngày theo store:
             - Success (Respcode = 0): so theo SETTLEMENT_DATE
             - Lỗi/điều chỉnh (Respcode <> 0): so theo DATE(Edit_Date)
           Ví dụ khung:
        */
        INSERT INTO TCKT_SESSION_DOMESTIC(
            CREATED_TIME, SETT_DATE, EDIT_DATE,
            ACQUIRER, ISSUER, PCODE, MERCHANT_TYPE,
            TXN_COUNT, TXN_MONEY, SUCC_COUNT, SUCC_MONEY,
            FAIL_COUNT, FAIL_MONEY, REV_COUNT, REV_MONEY,
            IST_IN_COUNT, IST_IN_MONEY, IST_OUT_COUNT, IST_OUT_MONEY,
            CD_TOTAL_MONEY, NAPAS_FEE, ADJ_FEE, NP_ADJ_FEE,
            MERCHANT_TYPE_CALC, STEP, FEE_TYPE, SERVICE_TYPE
        )
        SELECT
            NOW() AS CREATED_TIME,
            CASE
                WHEN Respcode = 0 AND SETTLEMENT_DATE < :settFrom THEN :settFrom
                WHEN Respcode = 0 AND SETTLEMENT_DATE > :settTo   THEN :settTo
                WHEN Respcode = 0 AND SETTLEMENT_DATE BETWEEN :settFrom AND :settTo THEN SETTLEMENT_DATE
                ELSE NULL
            END AS SETT_DATE,
            CASE
                WHEN Respcode = 0 THEN NULL
                ELSE CASE
                    WHEN DATE(Edit_Date) < :settFrom THEN :settFrom
                    ELSE DATE(Edit_Date)
                END
            END AS EDIT_DATE,
            ACQUIRER,
            ISSUER,
            SUBSTRING(LPAD(CAST(PCODE AS CHAR), 6, '0'), 1, 2) AS PCODE,
            /* MERCHANT_TYPE giữ các nhánh nghiệp vụ từ store – paste đầy đủ tại đây nếu có */
            MERCHANT_TYPE,
            COUNT(*)                                  AS TXN_COUNT,
            SUM(IFNULL(AMOUNT,0))                     AS TXN_MONEY,
            SUM(CASE WHEN Respcode = 0 THEN 1 ELSE 0 END)                 AS SUCC_COUNT,
            SUM(CASE WHEN Respcode = 0 THEN IFNULL(AMOUNT,0) ELSE 0 END)  AS SUCC_MONEY,
            SUM(CASE WHEN Respcode <> 0 THEN 1 ELSE 0 END)                AS FAIL_COUNT,
            SUM(CASE WHEN Respcode <> 0 THEN IFNULL(AMOUNT,0) ELSE 0 END) AS FAIL_MONEY,
            /* các chỉ tiêu còn lại paste/hoàn thiện theo store */
            0 AS REV_COUNT,
            0 AS REV_MONEY,
            0 AS IST_IN_COUNT,
            0 AS IST_IN_MONEY,
            0 AS IST_OUT_COUNT,
            0 AS IST_OUT_MONEY,
            /* tiền tổng/các phí theo CASE trong store */
            SUM(CASE WHEN Respcode = 0 AND SETTLEMENT_DATE BETWEEN :settFrom AND :settTo THEN IFNULL(AMOUNT,0) ELSE 0 END) AS CD_TOTAL_MONEY,
            0 AS NAPAS_FEE,
            0 AS ADJ_FEE,
            0 AS NP_ADJ_FEE,
            MERCHANT_TYPE AS MERCHANT_TYPE_CALC,
            NULL AS STEP,
            NULL AS FEE_TYPE,
            NULL AS SERVICE_TYPE
        FROM SHCLOG_SETT_IBFT
        WHERE
            (Respcode = 0   AND SETTLEMENT_DATE BETWEEN :settFrom AND :settTo)
         OR (Respcode <> 0  AND DATE(Edit_Date)    BETWEEN :settFrom AND :settTo)
        GROUP BY
            ACQUIRER, ISSUER,
            SUBSTRING(LPAD(CAST(PCODE AS CHAR), 6, '0'), 1, 2),
            MERCHANT_TYPE, Respcode
        """;

    // =====================
    // [STORE BLOCK: END_LOG]
    // =====================
    private static final String SQL_LOG_END = """
        INSERT INTO ERR_EX(ERR_TIME, ERR_CODE, ERR_DETAIL, ERR_MODULE)
        VALUES (NOW(), 0, 'End', 'INSERT_TCKT_SESSION_DOMESTIC_IBFT')
        """;

    // ========================
    // [STORE BLOCK: EXCEPTION]
    // ========================
    private static final String SQL_LOG_ERROR = """
        INSERT INTO ERR_EX(ERR_TIME, ERR_CODE, ERR_DETAIL, ERR_MODULE, CRITICAL)
        VALUES (NOW(), :errCode, :errMsg, 'INSERT_TCKT_SESSION_DOMESTIC_IBFT', 2)
        """;

    // ======================================================================
    // Orchestrator: chạy lần lượt đúng thứ tự block của store (BEGIN -> BODY)
    // ======================================================================
    @Transactional
    public int run(String fromDateStr, String toDateStr, String user) {
        final LocalDate from = LocalDate.parse(fromDateStr, DMY);
        final LocalDate to   = LocalDate.parse(toDateStr,   DMY);

        try {
            logBegin();

            insertSessionHeader(fromDateStr, toDateStr, user);

            // Nếu store của bạn có bước backup/rotate thì bật dòng này
            // backupAutoTable();

            int affected = insertDomesticBody(from, to, user);

            logEnd();
            return affected;
        } catch (DataAccessException ex) {
            logError(ex);
            throw ex; // giữ nguyên semantics của store: raise để rollback
        }
    }

    // ==========
    // BLOCK API
    // ==========
    // [STORE BLOCK: BEGIN_LOG]
    void logBegin() {
        jdbc.update(SQL_LOG_BEGIN, new MapSqlParameterSource());
    }

    // [STORE BLOCK: INSERT_SESSION_HEADER]
    void insertSessionHeader(String fromDateStr, String toDateStr, String user) {
        MapSqlParameterSource p = new MapSqlParameterSource()
                .addValue("fromDateStr", fromDateStr)
                .addValue("toDateStr",   toDateStr)
                .addValue("user",        user);
        jdbc.update(SQL_INSERT_SESSION_HEADER, p);
    }

    // [STORE BLOCK: BACKUP_AUTO_TABLE]
    void backupAutoTable() {
        jdbc.update(SQL_BACKUP_AUTO_TABLE, new MapSqlParameterSource());
    }

    // [STORE BLOCK: INSERT_DOMESTIC_BODY]
    int insertDomesticBody(LocalDate from, LocalDate to, String user) {
        MapSqlParameterSource p = new MapSqlParameterSource()
                .addValue("settFrom", java.sql.Date.valueOf(from))
                .addValue("settTo",   java.sql.Date.valueOf(to))
                .addValue("user",     user);
        int rows = jdbc.update(SQL_INSERT_DOMESTIC_BODY, p);
        log.info("[INSERT_TCKT_SESSION_DOMESTIC_IBFT] affected rows={}", rows);
        return rows;
    }

    // [STORE BLOCK: END_LOG]
    void logEnd() {
        jdbc.update(SQL_LOG_END, new MapSqlParameterSource());
    }

    // [STORE BLOCK: EXCEPTION]
    void logError(Exception ex) {
        String msg = ex.getMessage();
        MapSqlParameterSource p = new MapSqlParameterSource()
                .addValue("errCode", -1)
                .addValue("errMsg",  msg != null ? safeLeft(msg, 500) : "unknown");
        jdbc.update(SQL_LOG_ERROR, p);
    }

    private static String safeLeft(String s, int max) {
        return s.length() <= max ? s : s.substring(0, max);
    }
}
