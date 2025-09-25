package vn.napas.webrp.database.repo.store;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.napas.webrp.report.util.SqlLogUtils;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Port Java/TiDB cho procedure RPT.NAPAS_MASTER_VIEW_DOMESTIC_IBFT
 *
 * Ghi chú QUAN TRỌNG cho TiDB/MySQL: 1) TRUNCATE là DDL => auto-commit. Nếu
 * muốn giữ toàn bộ thao tác trong 1 transaction, dùng DELETE FROM ... thay cho
 * TRUNCATE. 2) Định dạng ngày: dùng STR_TO_DATE(:str, '%d/%m/%Y') và
 * DATE_FORMAT(dt,'%d/%m/%Y') khi cần. 3) Cố gắng gom các bước thành những câu
 * INSERT ... SELECT lớn, giữ nguyên semantics từ Oracle. 4) Logging thay cho
 * INSERT vào ERR_EX có thể thực hiện trực tiếp bằng JDBC; nếu cần, map sang
 * entity JPA sau.
 */

/* ========================= Repository layer ========================= */
@Slf4j
@Repository
@RequiredArgsConstructor
public class Proc_NAPAS_MASTER_VIEW_DOMESTIC_IBFT {
	private final NamedParameterJdbcTemplate jdbc;

	private static final DateTimeFormatter DMY = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	/**
	 * Inline SQL tạo danh sách ngân hàng thuộc nhóm LIQUIDITY (port từ
	 * RPT.GET_LIQUIDITY_BANK).
	 */
	private String liquidityBankCte() {
		return """
				    WITH liquidity_bank AS (
				        SELECT CAST(bank_id AS SIGNED) AS bank_id
				        FROM citad_liquidity_risk
				        WHERE status = 'Hiệu lực' AND NOW() > app_date

				        UNION ALL
				        SELECT CAST(bank_id AS SIGNED) AS bank_id
				        FROM citad_merge
				        WHERE is_active = 'Y'
				          AND merge_to IN (
				              SELECT bank_id FROM citad_liquidity_risk
				              WHERE status = 'Hiệu lực' AND NOW() > app_date
				          )

				        UNION ALL
				        SELECT CAST(tgtt_id AS SIGNED) AS bank_id
				        FROM tgtt_config
				        WHERE report_bank IN (
				            SELECT bank_id FROM citad_liquidity_risk
				            WHERE status = 'Hiệu lực' AND NOW() > app_date
				        )
				    )
				""";
	}

	/* ================= Helper: log vào ERR_EX ================= */
	public void logErrEx(String code, String detail, String module, int critical) {
		String sql = """
				    INSERT INTO ERR_EX(ERR_TIME, ERR_CODE, ERR_DETAIL, ERR_MODULE, CRITICAL)
				    VALUES (NOW(), :code, :detail, :module, :critical)
				""";
		MapSqlParameterSource p = new MapSqlParameterSource().addValue("code", code).addValue("detail", detail)
				.addValue("module", module).addValue("critical", critical);
		jdbc.update(sql, p);
	}

	/* ================= đọc LIST_SMS từ NAPAS_PARA ================= */
	public String getSmsListOrDefault(String def) {
		try {
			String sql = "SELECT PARA_VALUE FROM NAPAS_PARA WHERE PARA_NAME = 'LIST_SMS' LIMIT 1";
			return jdbc.query(sql, rs -> rs.next() ? rs.getString(1) : def);
		} catch (DataAccessException e) {
			log.warn("NAPAS_PARA not found, fallback LIST_SMS={}", def);
			return def;
		}
	}

	/* ================= tiền xử lý phiên ================= */
	public int insertSessionDomesticIbft(LocalDate from, LocalDate to, String user) {
		// Nếu đã có bản TiDB tương đương procedure INSERT_TCKT_SESSION_DOMESTIC_IBFT
		// thì thay câu dưới bằng CALL ...
		// Ở đây minh hoạ: ghi dấu vào bảng session (hoặc view materialized tạm) – tuỳ
		// implement thực tế của bạn.
		String sql = """
				    INSERT INTO TCKT_SESSION_DOMESTIC_IBFT(p_from_date, p_to_date, p_user, created_time)
				    VALUES (STR_TO_DATE(:from, '%d/%m/%Y'), STR_TO_DATE(:to, '%d/%m/%Y'), :user, NOW())
				""";
		MapSqlParameterSource p = new MapSqlParameterSource().addValue("from", from.format(DMY))
				.addValue("to", to.format(DMY)).addValue("user", user);
		return jdbc.update(sql, p);
	}

	/* ================= làm sạch bảng đích ================= */
	public int clearTargetTable() {
		// TRUNCATE sẽ auto-commit, nên dùng DELETE để giữ transaction nguyên khối
		String sql = "DELETE FROM TCKT_NAPAS_IBFT";
		return jdbc.update(sql, new MapSqlParameterSource());
	}

	/* ================= khối INSERT chính ================= */
	public int insertBlock_SuccessByRole(LocalDate from, LocalDate to) {
		String sql = liquidityBankCte()
				+ """
						    INSERT INTO TCKT_NAPAS_IBFT(
						        BANK_ID, GROUP_ROLE, GROUP_TRAN, TRAN_TYPE,
						        DB_TOTAL_TRAN, DB_AMOUNT, DB_IR_FEE, DB_SV_FEE, DB_TOTAL_FEE, DB_TOTAL_MONEY,
						        CD_TOTAL_TRAN, CD_AMOUNT, CD_IR_FEE, CD_SV_FEE, CD_TOTAL_FEE, CD_TOTAL_MONEY,
						        SERVICE_CODE, LIQUIDITY, SETTLEMENT_DATE
						    )
						    SELECT
						        CASE
						            WHEN ISSUER_RP = '970426' AND SUBSTRING(TRIM(PAN),1,8) = '97046416' THEN 970464
						            ELSE COALESCE(CAST(ISSUER_RP AS UNSIGNED), 0)
						        END AS BANK_ID,
						        CASE WHEN from_sys LIKE '%ISS%' THEN 'ISS' ELSE 'ACQ' END AS GROUP_ROLE,
						        'SUCCESS' AS GROUP_TRAN,
						        'DOMESTIC_IBFT' AS TRAN_TYPE,
						        SUM(CASE WHEN RESPCODE = 0 THEN 1 ELSE 0 END) AS DB_TOTAL_TRAN,
						        SUM(CASE WHEN RESPCODE = 0 THEN COALESCE(AMOUNT,0) ELSE 0 END) AS DB_AMOUNT,
						        SUM(CASE WHEN RESPCODE = 0 THEN COALESCE(IR_FEE,0) ELSE 0 END) AS DB_IR_FEE,
						        SUM(CASE WHEN RESPCODE = 0 THEN COALESCE(SV_FEE,0) ELSE 0 END) AS DB_SV_FEE,
						        SUM(CASE WHEN RESPCODE = 0 THEN COALESCE(IR_FEE,0)+COALESCE(SV_FEE,0) ELSE 0 END) AS DB_TOTAL_FEE,
						        SUM(CASE WHEN RESPCODE = 0 THEN COALESCE(AMOUNT,0) + COALESCE(IR_FEE,0)+COALESCE(SV_FEE,0) ELSE 0 END) AS DB_TOTAL_MONEY,
						        0,0,0,0,0,0,
						        COALESCE(SERVICE_CODE,'') AS SERVICE_CODE,
						        /* LIQUIDITY = Y nếu ISSUER_RP hoặc ACQUIRER_RP hoặc BB_BIN nằm trong tập liquidity_bank */
						        CASE WHEN (lb_iss.bank_id IS NOT NULL OR lb_acq.bank_id IS NOT NULL OR lb_bb.bank_id IS NOT NULL) THEN 'Y' ELSE 'N' END AS LIQUIDITY,
						        CASE
						            WHEN SETTLEMENT_DATE < STR_TO_DATE(:from, '%d/%m/%Y') THEN STR_TO_DATE(:from, '%d/%m/%Y')
						            WHEN SETTLEMENT_DATE > STR_TO_DATE(:to, '%d/%m/%Y') THEN STR_TO_DATE(:to, '%d/%m/%Y')
						            ELSE SETTLEMENT_DATE
						        END AS SETTLEMENT_DATE
						    FROM SHCLOG_SETT_IBFT A
						    /* Port FROM ... LEFT JOIN TABLE(GET_LIQUIDITY_BANK) ... */
						    LEFT JOIN liquidity_bank lb_iss ON CAST(A.ISSUER_RP AS SIGNED)   = lb_iss.bank_id
						    LEFT JOIN liquidity_bank lb_acq ON CAST(A.ACQUIRER_RP AS SIGNED) = lb_acq.bank_id
						    LEFT JOIN liquidity_bank lb_bb  ON CAST(A.BB_BIN AS SIGNED)      = lb_bb.bank_id
						    WHERE RESPCODE = 0
						      AND SETTLEMENT_DATE BETWEEN STR_TO_DATE(:from, '%d/%m/%Y') AND STR_TO_DATE(:to, '%d/%m/%Y')
						    GROUP BY
						        CASE
						            WHEN ISSUER_RP = '970426' AND SUBSTRING(TRIM(PAN),1,8) = '97046416' THEN 970464
						            ELSE COALESCE(CAST(ISSUER_RP AS UNSIGNED), 0)
						        END,
						        CASE WHEN from_sys LIKE '%ISS%' THEN 'ISS' ELSE 'ACQ' END,
						        COALESCE(SERVICE_CODE,''),
						        CASE
						            WHEN (
						                CASE
						                    WHEN ISSUER_RP = '970426' AND SUBSTRING(TRIM(PAN),1,8) = '97046416' THEN 970464
						                    ELSE COALESCE(CAST(ISSUER_RP AS UNSIGNED), 0)
						                END
						            ) IN (SELECT bank_id FROM liquidity_bank) THEN 'Y' ELSE 'N'
						        END,
						        CASE
						            WHEN SETTLEMENT_DATE < STR_TO_DATE(:from, '%d/%m/%Y') THEN STR_TO_DATE(:from, '%d/%m/%Y')
						            WHEN SETTLEMENT_DATE > STR_TO_DATE(:to, '%d/%m/%Y') THEN STR_TO_DATE(:to, '%d/%m/%Y')
						            ELSE SETTLEMENT_DATE
						        END
						""";
		MapSqlParameterSource p = new MapSqlParameterSource().addValue("from", from.format(DMY)).addValue("to",
				to.format(DMY));
		log.info("insertBlock_SuccessByRole: " + SqlLogUtils.renderSql(sql, p.getValues()));
		return jdbc.update(sql, p);
	}

	public int insertBlock_ErrorAdjust(LocalDate from, LocalDate to) {
		String sql = liquidityBankCte()
				+ """
						    INSERT INTO TCKT_NAPAS_IBFT(
						        BANK_ID, GROUP_ROLE, GROUP_TRAN, TRAN_TYPE,
						        DB_TOTAL_TRAN, DB_AMOUNT, DB_IR_FEE, DB_SV_FEE, DB_TOTAL_FEE, DB_TOTAL_MONEY,
						        CD_TOTAL_TRAN, CD_AMOUNT, CD_IR_FEE, CD_SV_FEE, CD_TOTAL_FEE, CD_TOTAL_MONEY,
						        SERVICE_CODE, LIQUIDITY, SETTLEMENT_DATE
						    )
						    SELECT
						        COALESCE(CAST(ISSUER_RP AS UNSIGNED), 0) AS BANK_ID,
						        CASE WHEN from_sys LIKE '%ISS%' THEN 'ISS' ELSE 'ACQ' END AS GROUP_ROLE,
						        'ERROR' AS GROUP_TRAN,
						        'DOMESTIC_IBFT' AS TRAN_TYPE,
						        0,0,0,0,0,0,
						        SUM(CASE WHEN RESPCODE <> 0 THEN 1 ELSE 0 END) AS CD_TOTAL_TRAN,
						        SUM(CASE WHEN RESPCODE <> 0 THEN COALESCE(AMOUNT,0) ELSE 0 END) AS CD_AMOUNT,
						        SUM(CASE WHEN RESPCODE <> 0 THEN COALESCE(IR_FEE,0) ELSE 0 END) AS CD_IR_FEE,
						        SUM(CASE WHEN RESPCODE <> 0 THEN COALESCE(SV_FEE,0) ELSE 0 END) AS CD_SV_FEE,
						        SUM(CASE WHEN RESPCODE <> 0 THEN COALESCE(IR_FEE,0)+COALESCE(SV_FEE,0) ELSE 0 END) AS CD_TOTAL_FEE,
						        SUM(CASE WHEN RESPCODE <> 0 THEN COALESCE(AMOUNT,0) + COALESCE(IR_FEE,0)+COALESCE(SV_FEE,0) ELSE 0 END) AS CD_TOTAL_MONEY,
						        COALESCE(SERVICE_CODE,'') AS SERVICE_CODE,
						        CASE WHEN COALESCE(CAST(ISSUER_RP AS UNSIGNED),0) IN (SELECT bank_id FROM liquidity_bank) THEN 'Y' ELSE 'N' END AS LIQUIDITY,
						        CASE
						            WHEN COALESCE(EDIT_DATE, SETTLEMENT_DATE) < STR_TO_DATE(:from, '%d/%m/%Y') THEN STR_TO_DATE(:from, '%d/%m/%Y')
						            WHEN COALESCE(EDIT_DATE, SETTLEMENT_DATE) > STR_TO_DATE(:to, '%d/%m/%Y') THEN STR_TO_DATE(:to, '%d/%m/%Y')
						            ELSE COALESCE(EDIT_DATE, SETTLEMENT_DATE)
						        END AS SETTLEMENT_DATE
						    FROM SHCLOG_SETT_IBFT A
						    LEFT JOIN liquidity_bank lb_iss ON CAST(A.ISSUER_RP AS SIGNED)   = lb_iss.bank_id
						    LEFT JOIN liquidity_bank lb_acq ON CAST(A.ACQUIRER_RP AS SIGNED) = lb_acq.bank_id
						    LEFT JOIN liquidity_bank lb_bb  ON CAST(A.BB_BIN AS SIGNED)      = lb_bb.bank_id
						    WHERE (COALESCE(EDIT_DATE, SETTLEMENT_DATE) BETWEEN STR_TO_DATE(:from, '%d/%m/%Y') AND STR_TO_DATE(:to, '%d/%m/%Y'))
						      AND RESPCODE <> 0
						    GROUP BY
						        COALESCE(CAST(ISSUER_RP AS UNSIGNED), 0),
						        CASE WHEN from_sys LIKE '%ISS%' THEN 'ISS' ELSE 'ACQ' END,
						        COALESCE(SERVICE_CODE,''),
						        CASE WHEN COALESCE(CAST(ISSUER_RP AS UNSIGNED),0) IN (SELECT bank_id FROM liquidity_bank) THEN 'Y' ELSE 'N' END,
						        CASE
						            WHEN COALESCE(EDIT_DATE, SETTLEMENT_DATE) < STR_TO_DATE(:from, '%d/%m/%Y') THEN STR_TO_DATE(:from, '%d/%m/%Y')
						            WHEN COALESCE(EDIT_DATE, SETTLEMENT_DATE) > STR_TO_DATE(:to, '%d/%m/%Y') THEN STR_TO_DATE(:to, '%d/%m/%Y')
						            ELSE COALESCE(EDIT_DATE, SETTLEMENT_DATE)
						        END
						""";
		MapSqlParameterSource p = new MapSqlParameterSource().addValue("from", from.format(DMY)).addValue("to",
				to.format(DMY));
		log.info("insertBlock_ErrorAdjust: " + SqlLogUtils.renderSql(sql, p.getValues()));
		return jdbc.update(sql, p);
	}

	/* ================= snapshot NAPAS_FEE_MONTH & backup ================= */
	public int upsertNapasFeeMonth(LocalDate from, LocalDate to) {
		// Ví dụ snapshot tổng phí trong khoảng – thay SELECT theo store thực tế
		String sql = """
				    REPLACE INTO NAPAS_FEE_MONTH(
				        CALC_FROM_DATE, CALC_TO_DATE, TOTAL_IR_FEE, TOTAL_SV_FEE, TOTAL_NAPAS_FEE, CREATED_TIME
				    )
				    SELECT
				        STR_TO_DATE(:from, '%d/%m/%Y'),
				        STR_TO_DATE(:to, '%d/%m/%Y'),
				        COALESCE(SUM(DB_IR_FEE + CD_IR_FEE),0),
				        COALESCE(SUM(DB_SV_FEE + CD_SV_FEE),0),
				        COALESCE(SUM(DB_IR_FEE + CD_IR_FEE + DB_SV_FEE + CD_SV_FEE),0),
				        NOW()
				    FROM TCKT_NAPAS_IBFT
				    WHERE SETTLEMENT_DATE BETWEEN STR_TO_DATE(:from, '%d/%m/%Y') AND STR_TO_DATE(:to, '%d/%m/%Y')
				""";
		MapSqlParameterSource p = new MapSqlParameterSource().addValue("from", from.format(DMY)).addValue("to",
				to.format(DMY));
		log.info("upsertNapasFeeMonth: " + SqlLogUtils.renderSql(sql, p.getValues()));
		return jdbc.update(sql, p);
	}

	public int backupNapasFeeMonth() {
		String sql = """
				    INSERT INTO NAPAS_FEE_MONTH_AUTO_BACKUP(
				        CALC_FROM_DATE, CALC_TO_DATE, TOTAL_IR_FEE, TOTAL_SV_FEE, TOTAL_NAPAS_FEE, BACKUP_TIME
				    )
				    SELECT CALC_FROM_DATE, CALC_TO_DATE, TOTAL_IR_FEE, TOTAL_SV_FEE, TOTAL_NAPAS_FEE, NOW()
				    FROM NAPAS_FEE_MONTH
				    WHERE DATE(NOW()) BETWEEN DATE(CALC_FROM_DATE) AND DATE(CALC_TO_DATE)
				""";
		log.info("upsertNapasFeeMonth: " + sql);
		return jdbc.update(sql, new MapSqlParameterSource());
	}
}
