package vn.napas.webrp.database.repo.store;



import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.napas.webrp.database.repo.sql.DbLoggerRepository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Chuyển đổi từ RPT.CHECK_TC_NULL (Oracle) sang Java/TiDB.
 * Nghiệp vụ:
 *  - INSERT ... SELECT vào IBFT_DOUBLE với các GD có BB_BIN IS NULL.
 *  - Nếu có bản ghi: ghi ERR_EX với nội dung cảnh báo + gọi hook SMS.
 *  - Nếu không có: ghi ERR_EX "OK".
 *  - Bắt lỗi và ghi ERR_EX với mã lỗi.
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class Proc_CHECK_TC_NULL {

    private final JdbcTemplate jdbc;
    private final DbLoggerRepository dbLog;
    private final vn.napas.webrp.noti.SmsNotifier smsNotifier;
private final String module = "TC_NULL";



    private static final String SQL_INSERT_DOUBLE = """
        INSERT INTO ibft_double (PAN, TRACE, LOCAL_TIME, LOCAL_DATE, TERMID, RUNTIME, MODULE)
        SELECT PAN, ORIGTRACE AS TRACE, LOCAL_TIME, LOCAL_DATE, TERMID, NOW(), ?
        FROM shclog_sett_ibft
        WHERE msgtype = 210
          AND BB_BIN IS NULL
          AND respcode = 0
        """;

 
    /**
     * Thực thi tương đương procedure CHECK_TC_NULL.
     * - Giao dịch chính REQUIRED để đảm bảo tính toàn vẹn (INSERT ... SELECT).
     * - Log OK/INFO/ERROR thực hiện ở REQUIRES_NEW để luôn ghi được log dù main tx lỗi.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void runCheckTcNull() {
        try {
            int affected = jdbc.update(SQL_INSERT_DOUBLE, module);

            if (affected != 0) {
                String detail = "Co " + affected + " giao dich IBFT khong xac dinh chieu CK, vui long kiem tra";
//                logInfoNewTx(detail);
                dbLog.info(detail, module);
                // Gửi SMS qua hook (giữ nguyên format message như store gốc)
                // "CHECK_TC_NULL#0983411005;0936535868;0988766330#<detail>"
//                smsNotifier.send("CHECK_TC_NULL#0983411005;0936535868;0988766330#" + detail);
                smsNotifier.notifyError(module, "CHECK_TC_NULL#0983411005;0936535868;0988766330#" + detail);
            } else {
//                logOkNewTx();
                dbLog.info("Ok", module);
            }
        } catch (Exception ex) {
            // Lấy SQL state/code nếu có
            String errCode = extractSqlStateOrDefault(ex, "-1");
            String errMsg  = ex.getMessage() == null ? ex.toString() : ex.getMessage();
            String detail  = "CHECK_TC_NULL Err: " + errCode + " - " + errMsg;
           dbLog.log( errCode + "", detail, module, 2);
            // Có thể lựa chọn ném lại hoặc nuốt lỗi. Store gốc commit log và không raise.
            // Ở đây mình chỉ log, KHÔNG ném lại để hành vi sát với store.
            log.error("[{}] {}", module, detail, ex);
        }
    }

    private String extractSqlStateOrDefault(Throwable ex, String fallback) {
        // MySQL/TiDB: SQLException có SQLState / ErrorCode
        Throwable cur = ex;
        while (cur != null) {
            if (cur instanceof java.sql.SQLException sqlEx) {
                String sqlState = sqlEx.getSQLState();
                if (sqlState != null && !sqlState.isBlank()) return sqlState;
                return String.valueOf(sqlEx.getErrorCode());
            }
            cur = cur.getCause();
        }
        return fallback;
    }



}
