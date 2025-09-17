package vn.napas.webrp.database.repo.store;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.napas.webrp.database.repo.sql.DbLoggerRepository;
import vn.napas.webrp.noti.SmsNotifier;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Slf4j
@Repository
@RequiredArgsConstructor
public class Proc_CHECK_SPEC_CHAR_IBFT {

	private final NamedParameterJdbcTemplate jdbc;
	private final DbLoggerRepository dbLog;
	private final SmsNotifier smsNotifier;
	private final String module = "CHECK_SPEC_CHAR_IBFT";

//    /* ==========================
//       Helpers ghi log vào ERR_EX
//       ========================== */
//    private void logErrExBegin() {
//        String sql = """
//            INSERT INTO ERR_EX(ERR_TIME, ERR_CODE, ERR_DETAIL, ERR_MODULE)
//            VALUES (NOW(), '1', 'BEGIN', 'CHECK_SPEC_CHAR_IBFT')
//            """;
//        jdbc.update(sql, Map.of());
//    }
//
//    private void logErrExInfo(String detail) {
//        String sql = """
//            INSERT INTO ERR_EX(ERR_TIME, ERR_CODE, ERR_DETAIL, ERR_MODULE)
//            VALUES (NOW(), '1', :detail, 'CHECK_SPEC_CHAR_IBFT')
//            """;
//        jdbc.update(sql, Map.of("detail", detail));
//    }
//
//    private void logErrExDone(String module, String detail) {
//        String sql = """
//            INSERT INTO ERR_EX(ERR_TIME, ERR_CODE, ERR_DETAIL, ERR_MODULE)
//            VALUES (NOW(), '0', :detail, :module)
//            """;
//        jdbc.update(sql, Map.of("detail", detail, "module", module));
//    }
//
//    private void logErrExError(String module, String errDetail) {
//        String sql = """
//            INSERT INTO ERR_EX(ERR_TIME, ERR_CODE, ERR_DETAIL, ERR_MODULE)
//            VALUES (NOW(), -1, :detail, :module)
//            """;
//        jdbc.update(sql, Map.of("detail", errDetail, "module", module));
//    }

	/*
	 * ======================================================= Hàm chính: tương
	 * đương RPT.CHECK_SPEC_CHAR_IBFT (Oracle)
	 * =======================================================
	 */
	@Transactional
	public void runCheckSpecCharIbft() {
//        final String MODULE = "CHECK_SPEC_CHAR_IBFT";
		try {
			// BEGIN
//            logErrExBegin();
			dbLog.begin(module, "Begin");

			// 1) INSERT INTO IBFT_DOUBLE: đánh dấu các STT có ký tự đặc biệt trong ngày
			String insertCandidates = """
					INSERT INTO IBFT_DOUBLE(TRACE, LOCAL_TIME, LOCAL_DATE, TERMID, RUNTIME, MODULE, STT)
					SELECT
					    ORIGTRACE AS TRACE,
					    LOCAL_TIME, LOCAL_DATE, TERMID,
					    NOW() AS RUNTIME,
					    :module AS MODULE,
					    STT
					FROM SHCLOG_SETT_IBFT
					WHERE MSGTYPE = 210
					  AND (
					        -- acctnum có TAB/LF/CR
					        ACCTNUM LIKE CONCAT('%', CHAR(9),  '%')
					     OR ACCTNUM LIKE CONCAT('%', CHAR(10), '%')
					     OR ACCTNUM LIKE CONCAT('%', CHAR(13), '%')

					        -- pan có TAB/LF/CR
					     OR PAN     LIKE CONCAT('%', CHAR(9),  '%')
					     OR PAN     LIKE CONCAT('%', CHAR(10), '%')
					     OR PAN     LIKE CONCAT('%', CHAR(13), '%')

					        -- content_fund có dấu nháy đơn, ASCII 29, hoặc dấu ?
					     OR CONTENT_FUND LIKE CONCAT('%', CHAR(39), '%')
					     OR CONTENT_FUND LIKE CONCAT('%', CHAR(29), '%')
					     OR CONTENT_FUND LIKE '%?%'

					        -- termloc có TAB
					     OR TERMLOC LIKE CONCAT('%', CHAR(9), '%')

					        -- [ hoặc ] chỉ khi BB_BIN = 970430
					     OR (CONTENT_FUND LIKE CONCAT('%', CHAR(91), '%') AND BB_BIN = 970430)
					     OR (CONTENT_FUND LIKE CONCAT('%', CHAR(93), '%') AND BB_BIN = 970430)
					  )
					""";
			int num = jdbc.update(insertCandidates, Map.of("module", module));

			if (num != 0) {
				String detail = "Co " + num + " giao dich co ky tu dac biet, vui long kiem tra";
//                logErrExInfo(detail);
				dbLog.log("1", detail, module, 2);

				// 2) UPDATE làm sạch – chỉ áp dụng cho những STT vừa được đưa vào IBFT_DOUBLE
				// trong ngày
				// Điều kiện STT IN (SELECT STT FROM IBFT_DOUBLE WHERE DATE(RUNTIME)=CURDATE()
				// AND MODULE='CHECK_SPEC_CHAR_IBFT')

				// 2.1 acctnum: thay TAB bằng space (nếu acctnum/pan có TAB)
				String upAcctTab = """
						UPDATE SHCLOG_SETT_IBFT t
						JOIN (
						    SELECT STT FROM IBFT_DOUBLE
						    WHERE DATE(RUNTIME) = CURDATE() AND MODULE = :module
						) d ON d.STT = t.STT
						SET t.ACCTNUM = REPLACE(t.ACCTNUM, CHAR(9), ' ')
						WHERE t.MSGTYPE = 210
						  AND (t.ACCTNUM LIKE CONCAT('%', CHAR(9), '%')
						       OR t.PAN LIKE CONCAT('%', CHAR(9), '%'))
						""";
				jdbc.update(upAcctTab, Map.of("module", module));

				// 2.2 acctnum: thay LF bằng space
				String upAcctLf = """
						UPDATE SHCLOG_SETT_IBFT t
						JOIN (
						    SELECT STT FROM IBFT_DOUBLE
						    WHERE DATE(RUNTIME) = CURDATE() AND MODULE = :module
						) d ON d.STT = t.STT
						SET t.ACCTNUM = REPLACE(t.ACCTNUM, CHAR(10), ' ')
						WHERE t.MSGTYPE = 210
						  AND (t.ACCTNUM LIKE CONCAT('%', CHAR(10), '%')
						       OR t.PAN LIKE CONCAT('%', CHAR(10), '%'))
						""";
				jdbc.update(upAcctLf, Map.of("module", module));

				// 2.3 acctnum: thay CR bằng space
				String upAcctCr = """
						UPDATE SHCLOG_SETT_IBFT t
						JOIN (
						    SELECT STT FROM IBFT_DOUBLE
						    WHERE DATE(RUNTIME) = CURDATE() AND MODULE = :module
						) d ON d.STT = t.STT
						SET t.ACCTNUM = REPLACE(t.ACCTNUM, CHAR(13), ' ')
						WHERE t.MSGTYPE = 210
						  AND (t.ACCTNUM LIKE CONCAT('%', CHAR(13), '%')
						       OR t.PAN LIKE CONCAT('%', CHAR(13), '%'))
						""";
				jdbc.update(upAcctCr, Map.of("module", module));

				// 2.4 content_fund: thay dấu nháy đơn ' (ASCII 39) bằng space
				String upCfApos = """
						UPDATE SHCLOG_SETT_IBFT t
						JOIN (
						    SELECT STT FROM IBFT_DOUBLE
						    WHERE DATE(RUNTIME) = CURDATE() AND MODULE = :module
						) d ON d.STT = t.STT
						SET t.CONTENT_FUND = REPLACE(t.CONTENT_FUND, CHAR(39), ' ')
						WHERE t.MSGTYPE = 210
						  AND t.CONTENT_FUND LIKE CONCAT('%', CHAR(39), '%')
						""";
				jdbc.update(upCfApos, Map.of("module", module));

				// 2.5 termloc: thay TAB bằng space
				String upTermTab = """
						UPDATE SHCLOG_SETT_IBFT t
						JOIN (
						    SELECT STT FROM IBFT_DOUBLE
						    WHERE DATE(RUNTIME) = CURDATE() AND MODULE = :module
						) d ON d.STT = t.STT
						SET t.TERMLOC = REPLACE(t.TERMLOC, CHAR(9), ' ')
						WHERE t.MSGTYPE = 210
						  AND t.TERMLOC LIKE CONCAT('%', CHAR(9), '%')
						""";
				jdbc.update(upTermTab, Map.of("module", module));

				// 2.6 content_fund: thay ký tự ASCII 29 bằng space
				String upCfAscii29 = """
						UPDATE SHCLOG_SETT_IBFT t
						JOIN (
						    SELECT STT FROM IBFT_DOUBLE
						    WHERE DATE(RUNTIME) = CURDATE() AND MODULE = :module
						) d ON d.STT = t.STT
						SET t.CONTENT_FUND = REPLACE(t.CONTENT_FUND, CHAR(29), ' ')
						WHERE t.MSGTYPE = 210
						  AND t.CONTENT_FUND LIKE CONCAT('%', CHAR(29), '%')
						""";
				jdbc.update(upCfAscii29, Map.of("module", module));

				// 2.7 content_fund: thay dấu hỏi ? bằng space
				String upCfQmark = """
						UPDATE SHCLOG_SETT_IBFT t
						JOIN (
						    SELECT STT FROM IBFT_DOUBLE
						    WHERE DATE(RUNTIME) = CURDATE() AND MODULE = :module
						) d ON d.STT = t.STT
						SET t.CONTENT_FUND = REPLACE(t.CONTENT_FUND, '?', ' ')
						WHERE t.MSGTYPE = 210
						  AND t.CONTENT_FUND LIKE '%?%'
						""";
				jdbc.update(upCfQmark, Map.of("module", module));

				// 2.8 content_fund: thay [ (ASCII 91) bằng space, chỉ BB_BIN = 970430
				String upCfLBracket = """
						UPDATE SHCLOG_SETT_IBFT t
						JOIN (
						    SELECT STT FROM IBFT_DOUBLE
						    WHERE DATE(RUNTIME) = CURDATE() AND MODULE = :module
						) d ON d.STT = t.STT
						SET t.CONTENT_FUND = REPLACE(t.CONTENT_FUND, CHAR(91), ' ')
						WHERE t.MSGTYPE = 210
						  AND t.BB_BIN = 970430
						  AND t.CONTENT_FUND LIKE CONCAT('%', CHAR(91), '%')
						""";
				jdbc.update(upCfLBracket, Map.of("module", module));

				// 2.9 content_fund: thay ] (ASCII 93) bằng space, chỉ BB_BIN = 970430
				String upCfRBracket = """
						UPDATE SHCLOG_SETT_IBFT t
						JOIN (
						    SELECT STT FROM IBFT_DOUBLE
						    WHERE DATE(RUNTIME) = CURDATE() AND MODULE = :module
						) d ON d.STT = t.STT
						SET t.CONTENT_FUND = REPLACE(t.CONTENT_FUND, CHAR(93), ' ')
						WHERE t.MSGTYPE = 210
						  AND t.BB_BIN = 970430
						  AND t.CONTENT_FUND LIKE CONCAT('%', CHAR(93), '%')
						""";
				jdbc.update(upCfRBracket, Map.of("module", module));

				// 3) Kiểm tra đã có "CHECK_SPEC_CHAR_IST" (ERR_CODE=0) trong ngày chưa để chốt
				// module log
				String countIstDone = """
						SELECT COUNT(*) FROM ERR_EX
						WHERE ERR_TIME > CURDATE()
						  AND ERR_MODULE = 'CHECK_SPEC_CHAR_IST'
						  AND ERR_CODE = 0
						""";
				Integer iCompleted = jdbc.getJdbcOperations().queryForObject(countIstDone, Integer.class);

				if (iCompleted != null && iCompleted >= 0) {
					// Match logic Oracle: nhánh này luôn >=0 => ghi vào CHECK_SPEC_CHAR
//                    logErrExDone("CHECK_SPEC_CHAR", detail);
					dbLog.error("CHECK_SPEC_CHAR", detail);
				} else {
					dbLog.error("CHECK_SPEC_CHAR_IBFT", detail);
				}
			} else {
				// Không phát hiện giao dịch có ký tự đặc biệt
				String detail = "Khong co giao dich IBFT co ky tu dac biet tren backend";
				String countIstDone = """
						SELECT COUNT(*) FROM ERR_EX
						WHERE ERR_TIME > CURDATE()
						  AND ERR_MODULE = 'CHECK_SPEC_CHAR_IST'
						  AND ERR_CODE = 0
						""";
				Integer iCompleted = jdbc.getJdbcOperations().queryForObject(countIstDone, Integer.class);

				if (iCompleted != null && iCompleted > 0) {
//                    logErrExDone("CHECK_SPEC_CHAR", detail);
					dbLog.error("CHECK_SPEC_CHAR", detail);
				} else {
//                    logErrExDone("CHECK_SPEC_CHAR_IBFT", detail);
					dbLog.error("CHECK_SPEC_CHAR_IBFT", detail);
				}
			}
		} catch (DataAccessException ex) {
			String msg = "CHECK_SPEC_CHAR Error: " + ex.getMessage();
			log.error(msg, ex);
			// Store gốc ghi vào module 'CHECK_SPEC_CHAR' khi lỗi
//            logErrExError("CHECK_SPEC_CHAR", msg);
			// Rethrow nếu muốn rollback toàn bộ
			dbLog.error("CHECK_SPEC_CHAR", msg);
			throw ex;
		}
	}
}
