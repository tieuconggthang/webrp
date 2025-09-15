package vn.napas.webrp.database.repo.sql;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DbLoggerRepository {

	private final JdbcTemplate jdbc;

	private static final String INSERT_SQL = "INSERT INTO ERR_EX(err_time, err_code, err_detail, err_module, critical) "
			+ "VALUES (NOW(), ?, ?, ?, ?)";

	public void log(String code, String detail, String module, int critical) {
		if (detail != null && detail.length() > 900) {
			detail = detail.substring(0, 900); // tránh lỗi nếu cột err_detail quá ngắn
		}
		jdbc.update(INSERT_SQL, code, detail, module, critical);
	}

	public void logInfo(String detail, String module) {
		log("INFO", detail, module, 0);
	}

	public void logError(String detail, String module) {
		log("ERROR", detail, module, 1);
	}

	public void info(String detail, String module) {
		log("INFO", detail, module, 0);
	}

	public void begin(String module, String detail) {
		log("BEGIN", detail, module, 0);
	}

	public void end(String module, String detail) {
		log("END", detail, module, 0);
	}

	public void error(String module, String detail) {
		log("ERROR", detail, module, 1);
	}
}
