package vn.napas.webrp.database.repo.store;

import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.regex.Pattern;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class GatherStats {
	private final JdbcTemplate jdbc;
	private static final Pattern NAME_OK = Pattern.compile("^[A-Za-z0-9_]+$");

	public GatherStats(JdbcTemplate tidbJdbc) {
		this.jdbc = tidbJdbc;
	}
	
	 /**
     * Thay thế cho RPT.GATHER_TABLE_FILL_DATA_DAILY trên TiDB.
     * - Ghi log START
     * - ANALYZE TABLE `<schema>`.`table`
     * - Đo thời gian, ghi log DURATION và FINISH
     * - Nếu lỗi: ghi log lỗi với CRITICAL=2
     */
    public void gatherTableFillDataDaily(String schema, String table) {
        String module = "GATHER_TABLE_FILL_DATA_DAILY";

        // Validate tên schema/table cho an toàn
        if (!isSafeName(schema) || !isSafeName(table)) {
            throw new IllegalArgumentException("Invalid schema/table name");
        }

        // Log START
        insertErrEx(nowStr(), "0",
                "START GATHER TABLE: " + table, module, null);

        Instant start = Instant.now();

        try {
            // tương đương GATHER_TABLE_STATS của Oracle
            String analyzeSql = "ANALYZE TABLE `" + schema + "`.`" + table + "`";
            jdbc.execute(analyzeSql);

            Instant end = Instant.now();
            long seconds = Duration.between(start, end).toSeconds();
            long minutes = seconds / 60;
            long remainSec = seconds % 60;

            String elapsedText = "END GATHER TABLE: " + table + " - Duration: "
                    + minutes + " min " + remainSec + " sec";

            // Log DURATION
            insertErrEx(nowStr(), "0", elapsedText, module, null);

            // Log FINISH
            insertErrEx(nowStr(), "0",
                    "FINISH GATHER TABLE: " + table, module, null);

        } catch (Exception ex) {
            // Lấy thông tin lỗi gần giống SQLCODE/SQLERRM
            String errCode = extractSqlStateOrCode(ex);
            String errMsg  = ex.getMessage();

            // Log error chi tiết
            insertErrEx(nowStr(), errCode, errMsg, module, 2);
        }
    }

    private boolean isSafeName(String s) {
        return s != null && NAME_OK.matcher(s).matches();
    }

    private String nowStr() {
        // TiDB DATETIME: format 'yyyy-MM-dd HH:mm:ss'
        return LocalDateTime.now().toString().replace('T', ' ');
    }

    private String extractSqlStateOrCode(Exception e) {
        // Thử lấy SQLState/ VendorCode nếu có
        if (e instanceof SQLException se) {
            if (se.getSQLState() != null) return se.getSQLState();
            return String.valueOf(se.getErrorCode());
        }
        return "ERR";
    }

    private void insertErrEx(String time, String code, String detail, String module, Integer critical) {
        // Lưu ý: ERR_TIME là DATETIME; dùng NOW() trên DB hoặc truyền chuỗi format chuẩn
        if (critical == null) {
            jdbc.update(
                "INSERT INTO ERR_EX (ERR_TIME, ERR_CODE, ERR_DETAIL, ERR_MODULE) VALUES (NOW(), ?, ?, ?)",
                code, detail, module
            );
        } else {
            jdbc.update(
                "INSERT INTO ERR_EX (ERR_TIME, ERR_CODE, ERR_DETAIL, ERR_MODULE, CRITICAL) VALUES (NOW(), ?, ?, ?, ?)",
                code, detail, module, critical
            );
        }
    }
}
