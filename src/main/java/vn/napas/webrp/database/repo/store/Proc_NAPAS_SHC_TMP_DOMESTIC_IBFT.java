package vn.napas.webrp.database.repo.store;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.napas.webrp.constant.TableConstant;
import vn.napas.webrp.database.dto.ShclogSettIbftAdjustDto;
import vn.napas.webrp.database.repo.TableMaintenanceRepository;
import vn.napas.webrp.report.service.ibft.CachedFeeFunctionsService;
import vn.napas.webrp.report.util.Helpers;
import vn.napas.webrp.report.util.SqlLogUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.ElementType;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.validation.SchemaFactoryLoader;

/**
 * Chuyển đổi từ PL/SQL: RPT.NAPAS_SHC_TMP_DOMESTIC_IBFT -> Java Repo
 * (TiDB/MySQL) ------------------------------------------------------------ Ghi
 * chú quan trọng: 1) TiDB dùng cú pháp MySQL: NVL -> IFNULL, DECODE -> CASE
 * WHEN, TRUNC(SYSDATE) -> CURDATE(), TO_DATE('dd/MM/yyyy') ->
 * STR_TO_DATE(:s,'%d/%m/%Y'), TO_CHAR(PCODE,'099999') -> LPAD(CAST(PCODE AS
 * CHAR),6,'0'), SUBSTR -> SUBSTRING, INSTR -> LOCATE, || -> CONCAT,
 * SYSTIMESTAMP -> NOW(3) 2) Các hàm/func DB đặc thù (NAPAS_GET_FEE_KEY,
 * OLD_FINAL_FEE_CAL_VER, NAPAS_FEE_OLD_TRAN, CREATE_PCODE, GET_BCCARD_ID,
 * SEND_SMS, DATA_TO_ECOM, Getkhoacuabang) được GIỮ NGUYÊN tên để gọi trực tiếp
 * trong SQL. Cần bảo đảm đã có sẵn UDF/Stored Function tương ứng bên TiDB. 3)
 * Các lệnh COMMIT trong PL/SQL được thay bởi @Transactional ở Java. Nếu cần
 * commit theo chặng, có thể tách phương thức hoặc dùng TransactionTemplate.
 */

@Slf4j
@Repository
@RequiredArgsConstructor
public class Proc_NAPAS_SHC_TMP_DOMESTIC_IBFT {
	private final NamedParameterJdbcTemplate jdbc;
	private final JdbcTemplate jdbcTemplate;

	private static final DateTimeFormatter DDMMYYYY = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	private static final DateTimeFormatter DDMMYYYY_HHMMSS = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
	private static final RowMapper<ShclogSettIbftAdjustDto> SHCLOG_ROW_MAPPER = new BeanPropertyRowMapper<>(
			ShclogSettIbftAdjustDto.class);
	@Autowired
	TableMaintenanceRepository tableMaintenanceRepository;
	@Autowired
	Proc_DATA_TO_ECOM proc_DATA_TO_ECOM;
	@Autowired
	Proc_GET_BCCARD_ID proc_gaBccard_ID;
	@Autowired
	CachedFeeFunctionsService cachedFee;

	/**
	 * Chạy tương đương procedure NAPAS_SHC_TMP_DOMESTIC_IBFT.
	 * 
	 * @param pQRY_FROM_DATE định dạng dd/MM/yyyy
	 * @param pQRY_TO_DATE   định dạng dd/MM/yyyy
	 * @param pCreated_User  user tạo
	 * @param pSett_Code     mã hạch toán (int)
	 */
	@Transactional
	public void run(LocalDate pQRY_FROM_DATE, LocalDate pQRY_TO_DATE, String pCreated_User, int pSett_Code) {
		long t0 = System.currentTimeMillis();
		LocalDate d1 = pQRY_FROM_DATE;
		LocalDate d2 = pQRY_TO_DATE;
		String fromddMMyyyy = pQRY_FROM_DATE.format(DDMMYYYY);
		String toddMMyyyy = pQRY_FROM_DATE.format(DDMMYYYY);
//		System.out.println("dd/MM/yyyy = " + ddMMyyyy);
		LocalDateTime day1 = d1.atTime(23, 0, 0);
		LocalDateTime day2 = d2.atTime(23, 0, 0);

		log.info("[NAPAS_SHC_TMP_DOMESTIC_IBFT] Start: {} - {} (settCode={})", pQRY_FROM_DATE, pQRY_TO_DATE,
				pSett_Code);
		insertErrExNow("-1", "Start:" + pQRY_FROM_DATE + " - " + pQRY_TO_DATE, "NAPAS_SHC_TMP_DOMESTIC_IBFT");

		// 1) WAIT: đợi đủ 10 job (theo logic UNION ALL trong store). Lặp lại mỗi 60s.
//		waitForPrerequisites();

		// 2) Đọc tham số hệ thống
		String vListSms = getParaOrDefault("LIST_SMS", "0983411005");
		LocalDate dLastSett = getParaDateOrDefault("LAST_SETT_USD", d1);
		BigDecimal oRate = getParaNumberOrNull("LAST_RATE");
//		tableMaintenanceRepository.truncateTable(TableConstant.SHCLOG_SETT_IBFT_ADJUST);
		// 3) TRUNCATE bảng tạm
//		jdbc.getJdbcOperations().execute("TRUNCATE TABLE SHCLOG_SETT_IBFT_ADJUST");

		// 4) Nếu d2 là hôm qua -> ghi NP_SETT_DATE
		if (d2.equals(LocalDate.now(ZoneId.systemDefault()).minusDays(1))) {
			String sql = "INSERT INTO NP_SETT_DATE(SETT_DATE, DT_TYPE, F_DATE, T_DATE) VALUES (NOW(), '000000', :f, :t)";
			MapSqlParameterSource ps = new MapSqlParameterSource().addValue("f", java.sql.Date.valueOf(d1))
					.addValue("t", java.sql.Date.valueOf(d2));
			jdbc.update(sql, ps);
		}

		// 5) %THUE từ SYS_TBLPARAMETERS
		BigDecimal thue = jdbc.queryForObject(
				"SELECT CAST(PARAMVALUE AS DECIMAL(10,3)) FROM SYS_TBLPARAMETERS WHERE PARAMNAME = 'THUE'",
				new MapSqlParameterSource(), BigDecimal.class);
		if (thue == null)
			thue = BigDecimal.ONE; // fallback

		// 6) Nhánh hạch toán ngoại tệ (900, 901) -> pause (logic gốc chỉ set
		// rl='PAUSE')
		if (pSett_Code == 900 || pSett_Code == 901) {
			log.info("[NAPAS_SHC_TMP_DOMESTIC_IBFT] settCode {} => PAUSE (ngoại VND/USD)", pSett_Code);
		} else {
			// 7) INSERT INTO SHCLOG_SETT_IBFT_ADJUST ... SELECT FROM shclog (đã chuyển cú
			// pháp MySQL/TiDB)
			insertAdjustFromShclog(d1, d2, dLastSett, oRate);
		}

		// 8) Các block UPDATE tính Fee_Key, FEE_* ... (giữ nguyên hàm tính trên DB).
		// NOTE:
		// - ĐÃ CHUYỂN cú pháp DECODE/NVL/TRUNC/TO_CHAR sang MySQL.
		// - Cần đảm bảo các function tồn tại trên TiDB.
		//block update 1
		recomputeFeesStage1_v2();
		//block update 2
		recomputeFeesStage2_OldRange();
		//block update 3
		recomputeFeesStage3_ByWhitelist();
		//block update 4
		recomputeFeesStage4_FillZeros(thue);
		//block update 5
		recomputeFeesStage5_PostMapFields(thue);
		//block update 6
		recomputeFeesStage6();
		recomputeFeesStage6_1();
		// block 7
		cleanupSpecialChars();
		// block 8 update Pcode_Orig cho gd C3-72 phuc vu sinh file BEN
		updatePcodeOrig();
		//block 9
		zeroFeesForPartialReversals();

		// 9) Ghi RP_LOG_SHC_TMP
		insertLogShcTmp(day1, day2, pCreated_User, pSett_Code);

		// 10) Gọi DATA_TO_ECOM (nếu đã có procedure tương ứng trên TiDB, dùng CALL)

		callDataToEcom(fromddMMyyyy, toddMMyyyy);

		long t1 = System.currentTimeMillis();
		Duration dur = Duration.ofMillis(t1 - t0);
		String vDetail = String.format(
				"Do du lieu(New) %d tc, user: %s. DL tu %s toi %s. Bat dau: %s hoan thanh: %s\nTong TG: %d gio, %d phut, %d giay.",
				pSett_Code, LocalDate.now(), d1.format(DDMMYYYY), d2.format(DDMMYYYY),
				LocalDateTime.ofInstant(Instant.ofEpochMilli(t0), ZoneId.systemDefault()).format(DDMMYYYY_HHMMSS),
				LocalDateTime.ofInstant(Instant.ofEpochMilli(t1), ZoneId.systemDefault()).format(DDMMYYYY_HHMMSS),
				dur.toHoursPart(), dur.toMinutesPart(), dur.toSecondsPart());
		insertErrExNow("-1", vDetail, "NAPAS_SHC_TMP_DOMESTIC_IBFT");
		insertErrExNow("0", "End fill data to SHC_TMP_DOMESTIC_IBFT", "NAPAS_SHC_TMP_DOMESTIC_IBFT");
	}

	/* ============================= Helpers ============================= */

	private void waitForPrerequisites() {
		// Lặp đến khi đủ 10 jobs. Dùng scalar subqueries để cộng tổng (MySQL/TiDB hợp
		// lệ).
		String sql = "SELECT "
				+ " (SELECT IFNULL(COUNT(*),0) FROM NP_EXEC_LOG WHERE DATE(exec_date)=CURDATE() AND STT='100100400200' AND EX_ERR=0 LIMIT 1) +"
				+ " (SELECT IFNULL(COUNT(*),0) FROM NP_EXEC_LOG WHERE DATE(exec_date)=CURDATE() AND STT='100100400255' AND EX_ERR=0 LIMIT 1) +"
				+ " (SELECT IFNULL(COUNT(*),0) FROM err_ex WHERE DATE(err_time)=CURDATE() AND ERR_MODULE='MATCH_FILE' AND ERR_CODE='0' AND ERR_DETAIL='Successfully' LIMIT 1) +"
				+ " (SELECT IFNULL(COUNT(*),0) FROM err_ex WHERE err_time>CURDATE() AND err_module='GET_DATA_IPSGW_TO_IBFT' AND err_detail='END PROCESS DATA FROM IPS GATEWAY' AND critical=0 LIMIT 1) +"
				+ " (SELECT IFNULL(COUNT(*),0) FROM err_ex WHERE err_time>CURDATE() AND err_module='ACH_READ_FILE' AND err_detail='READ FILE DONE' AND err_code=0 AND critical=0 LIMIT 1) +"
				+ " (SELECT IFNULL(COUNT(*),0) FROM err_ex WHERE DATE(err_time)=CURDATE() AND err_module='BACKEND_DOUBLE' AND err_detail='OK' LIMIT 1) +"
				+ " (SELECT IFNULL(COUNT(*),0) FROM NP_EXEC_LOG WHERE DATE(exec_date)=CURDATE() AND STT='100100300700' AND EX_ERR=0 LIMIT 1) +"
				+ " (SELECT IFNULL(COUNT(*),0) FROM err_ex WHERE DATE(err_time)=CURDATE() AND err_module='TC_NULL' AND err_detail='OK' LIMIT 1) +"
				+ " (SELECT IFNULL(COUNT(*),0) FROM err_ex WHERE DATE(err_time)=CURDATE() AND err_module='NAPAS_SYNC_ECOM' AND err_detail LIKE '%Success - MAX STT:%' LIMIT 1) +"
				+ " (SELECT IFNULL(COUNT(*),0) FROM err_ex WHERE DATE(err_time)=CURDATE() AND err_module IN ('CHECK_SPEC_CHAR','CHECK_SPEC_CHAR_IBFT') AND err_code=0 LIMIT 1) AS total";

		int total = 0;
		int loops = 0; // chặn khả năng wait vô tận
		while (total < 10 && loops < 1440) { // tối đa 24h
			total = jdbc.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
			if (total < 10) {
				insertErrExNow("0", "Wait check data ready", "NAPAS_SHC_TMP_DOMESTIC_IBFT");
				try {
					Thread.sleep(60_000L);
				} catch (InterruptedException ignored) {
				}
				loops++;
			}
		}
	}

	private String getParaOrDefault(String name, String defVal) {
		try {
			return jdbc.queryForObject("SELECT PARA_VALUE FROM NAPAS_PARA WHERE PARA_NAME=:n",
					new MapSqlParameterSource("n", name), String.class);
		} catch (DataAccessException ex) {
			return defVal;
		}
	}

	private LocalDate getParaDateOrDefault(String name, LocalDate defVal) {
		try {
			String s = jdbc.queryForObject("SELECT PARA_VALUE FROM NAPAS_PARA WHERE PARA_NAME=:n",
					new MapSqlParameterSource("n", name), String.class);
			return LocalDate.parse(s, DDMMYYYY);
		} catch (Exception ex) {
			return defVal;
		}
	}

	private BigDecimal getParaNumberOrNull(String name) {
		try {
			return jdbc.queryForObject("SELECT CAST(PARA_VALUE AS DECIMAL(28,15)) FROM NAPAS_PARA WHERE PARA_NAME=:n",
					new MapSqlParameterSource("n", name), BigDecimal.class);
		} catch (Exception ex) {
			return null;
		}
	}

	private void insertErrExNow(String code, String detail, String module) {
		String sql = "INSERT INTO ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE) VALUES (NOW(), :c, :d, :m)";
		jdbc.update(sql, new MapSqlParameterSource().addValue("c", code).addValue("d", detail).addValue("m", module));
	}

	/*
	 * Chuyển khối INSERT +APPEND //... FROM shclog (Oracle) sang MySQL/TiDB. Các
	 * chuyển đổi chính đã áp dụng: NVL->IFNULL, DECODE->CASE,
	 * TO_CHAR(PCODE,'099999')->LPAD(PCODE,6,'0'), SUBSTR->SUBSTRING, INSTR->LOCATE,
	 * concat bằng CONCAT, SYSDATE/SYSTIMESTAMP->NOW().
	 */

	private void insertAdjustFromShclog(LocalDate d1, LocalDate d2, LocalDate dLastSett, BigDecimal oRate) {

		// Đổi tên biến cho sạch ASCII
		String bankCardId = proc_gaBccard_ID.getBankCardID();

		String sql = """
				INSERT INTO SHCLOG_SETT_IBFT_ADJUST(
				    MSGTYPE,PAN,PCODE,AMOUNT,SETTLEMENT_AMOUNT,cardholder_amount,PRE_CARDHOLDER_AMOUNT,ACQ_CURRENCY_CODE,
				    SETT_CURRENCY_CODE,CH_currency_code,FEE_ISS,FEE_ACQ,TRACE,LOCAL_TIME,LOCAL_DATE,CAP_DATE,
				    SETTLEMENT_DATE,ACQUIRER,ISSUER,RESPCODE,MERCHANT_TYPE,TERMID,ACCTNUM,ISS_CURRENCY_CODE,
				    TRANDATE,TRANTIME,CARDPRODUCT,REVCODE,ORIGTRACE,ACCEPTORNAME,TERMLOC,TGGUIQT,TGXLNV,TGGUINV,TGGUIQTP,EDIT_DATE,
				    EDIT_USER,SML_VERIFY,ORIGISS,ORIGRESPCODE,isrev,preamount,ppcode,AUTHNUM,thu,thu_refun,tt1,tt2,tt3,tt4,
				    SETTLEMENT_CONV_RATE,CARDHOLDER_CONV_RATE,BB_BIN,FORWARD_INST,TRANSFEREE,refnum,MERCHANT_TYPE_ORIG,
				    ACQUIRER_FE, ACQUIRER_RP, ISSUER_FE, ISSUER_RP, FEE_NOTE,PCODE2, FROM_SYS, TRAN_CASE, BB_BIN_ORIG, SRC, `DES`,
				    PCODE_ORIG, RC_ISS, RC_BEN,napas_DATE,napas_EDIT_DATE, STT,Token, ADDRESPONSE, MVV,
				    FEE_KEY, ACQ_COUNTRY, POS_ENTRY_CODE, POS_CONDITION_CODE, F4,
				    F5, F6, F49, SETTLEMENT_CODE, SETTLEMENT_RATE,
				    ISS_CONV_RATE,TCC,FEE_IRF_ISS,FEE_SVF_ISS,FEE_IRF_ACQ,FEE_SVF_ACQ,FEE_IRF_BEN,FEE_SVF_BEN,Txnsrc,txndest,Content_Fund,ADD_INFO, DATA_ID,
				    F60_UPI,F100_UPI,PREAMOUNT_USD,IS_PART_REV,TRANSIT_CSRR,MSGTYPE_DETAIL
				)
				SELECT
				    t.MSGTYPE,
				    t.PAN,
				    SUBSTRING(LPAD(CAST(t.PCODE AS CHAR),6,'0'),1,2) AS PCODE,
				    CASE WHEN t.ACQ_CURRENCY_CODE IN (840,418)
				         THEN ROUND(CASE WHEN SUBSTRING(LPAD(CAST(t.PCODE AS CHAR),6,'0'),1,2) IN ('00','01','40','41','42','43','48','91','20') THEN t.AMOUNT ELSE 0 END,2)
				         ELSE ROUND(CASE WHEN SUBSTRING(LPAD(CAST(t.PCODE AS CHAR),6,'0'),1,2) IN ('00','01','40','41','42','43','48','91','20') THEN t.AMOUNT ELSE 0 END)
				    END AS AMOUNT,
				    t.SETTLEMENT_AMOUNT,

				    /* >>>> BỔ SUNG ĐỦ 2 GIÁ TRỊ THEO ĐÚNG THỨ TỰ CỘT ĐÍCH <<<< */
				    -- 1) cardholder_amount
				    CASE WHEN t.acquirer_rp = :bankcardid THEN t.CARDHOLDER_AMOUNT ELSE t.TRANSACTION_AMOUNT END AS cardholder_amount,
				    -- 2) PRE_CARDHOLDER_AMOUNT
				    t.PRE_CARDHOLDER_AMOUNT,

				    CASE WHEN t.ACQ_CURRENCY_CODE = 840 THEN 840 WHEN t.ACQ_CURRENCY_CODE = 418 THEN 418 ELSE 704 END AS ACQ_CURRENCY_CODE,
				    t.SETT_CURRENCY_CODE,
				    t.CH_currency_code,
				    CASE WHEN t.FEE_ISS IS NULL THEN 0 ELSE CASE WHEN t.ACQ_CURRENCY_CODE IN (840,418) THEN ROUND(t.FEE_ISS * (SELECT CAST(PARAMVALUE AS DECIMAL(10,3)) FROM SYS_TBLPARAMETERS WHERE PARAMNAME='THUE'),2)
				         ELSE CASE WHEN t.PCODE2 IN (810000,820000,830000,860000,870000,880000) OR TRIM(t.FEE_NOTE)='IBT 72 (VAT)' OR t.FROM_SYS='IBT' THEN ROUND(t.FEE_ISS,0) ELSE ROUND(t.FEE_ISS * (SELECT CAST(PARAMVALUE AS DECIMAL(10,3)) FROM SYS_TBLPARAMETERS WHERE PARAMNAME='THUE'),0) END END END AS FEE_ISS,
				    CASE WHEN t.FEE_ACQ IS NULL THEN 0
				         WHEN t.Acquirer = 970416 AND IFNULL(t.ACQUIRER_FE,t.ACQUIRER)=456123 AND t.Issuer NOT IN (602907,605609,220699) AND SUBSTRING(LPAD(CAST(t.PCODE AS CHAR),6,'0'),1,2)='00' AND t.Respcode=115 THEN 0
				         ELSE CASE WHEN t.ACQ_CURRENCY_CODE IN (840,418) THEN ROUND(t.FEE_ACQ * (SELECT CAST(PARAMVALUE AS DECIMAL(10,3)) FROM SYS_TBLPARAMETERS WHERE PARAMNAME='THUE'),2)
				                   ELSE CASE WHEN t.PCODE2 IN (810000,820000,830000,860000,870000,880000) OR TRIM(t.FEE_NOTE)='IBT 72 (VAT)' OR t.FROM_SYS='IBT' THEN ROUND(t.FEE_ACQ,0)
				                             ELSE ROUND(t.FEE_ACQ * (SELECT CAST(PARAMVALUE AS DECIMAL(10,3)) FROM SYS_TBLPARAMETERS WHERE PARAMNAME='THUE'),0) END END END AS FEE_ACQ,
				    t.TRACE,t.LOCAL_TIME,t.LOCAL_DATE,t.CAP_DATE,t.SETTLEMENT_DATE,TRIM(t.ACQUIRER) AS ACQUIRER,TRIM(t.ISSUER) AS ISSUER,
				    t.RESPCODE,
				    t.MERCHANT_TYPE,t.TERMID,
				    CASE WHEN IFNULL(t.BB_BIN,0)=970403 AND (TRIM(SUBSTRING(t.ACCTNUM,1,LOCATE('|', CONCAT(t.ACCTNUM,'|'))-1)) IS NULL OR TRIM(SUBSTRING(t.ACCTNUM,1,LOCATE('|', CONCAT(t.ACCTNUM,'|'))-1))='')
				         THEN CONCAT(TRIM(t.PAN),'|',TRIM(SUBSTRING(CONCAT(t.ACCTNUM,'|'), LOCATE('|', CONCAT(t.ACCTNUM,'|'))+1)))
				         ELSE t.ACCTNUM END AS ACCTNUM,
				    t.ISS_CURRENCY_CODE,
				    t.TRANDATE,t.TRANTIME,t.CARDPRODUCT,t.REVCODE,t.ORIGTRACE,t.ACCEPTORNAME,t.TERMLOC,t.TGGUIQT,t.TGXLNV,t.TGGUINV,t.TGGUIQTP,
				    t.EDIT_DATE,t.EDIT_USER,t.SML_VERIFY,t.ORIGISS,t.ORIGRESPCODE,
				    CASE WHEN t.respcode=110 THEN 0 WHEN t.respcode=115 AND t.origrespcode=225 THEN 0 WHEN t.Isrev IS NULL THEN 0 ELSE 1 END AS isrev,
				    CASE WHEN t.ACQ_CURRENCY_CODE IN (840,418) THEN ROUND(t.preamount,2) ELSE ROUND(t.preamount) END AS preamount,
				    t.pcode AS ppcode,t.AUTHNUM,0 AS thu,0 AS thu_refun,0 AS tt1,0 AS tt2,0 AS tt3,0 AS tt4,
				    t.CONV_RATE,
				    CASE WHEN t.Acquirer_Rp=970488 AND t.Acq_Currency_Code=840 AND t.SETTLEMENT_DATE < :dLastSett THEN IFNULL(:oRate, t.CARDHOLDER_CONV_RATE) ELSE t.CARDHOLDER_CONV_RATE END,
				    CASE WHEN SUBSTRING(LPAD(CAST(t.PCODE AS CHAR),6,'0'),1,2)='40' THEN NULL ELSE t.BB_BIN END,
				    t.FORWARD_INST,t.TRANSFEREE,t.refnum,
				    IFNULL(t.MERCHANT_TYPE_ORIG, t.MERCHANT_TYPE) AS MERCHANT_TYPE_ORIG,
				    IFNULL(t.ACQUIRER_FE, t.ACQUIRER) AS ACQUIRER_FE,
				    IFNULL(t.ACQUIRER_RP, t.ACQUIRER) AS ACQUIRER_RP,
				    IFNULL(t.ISSUER_FE, t.ISSUER) AS ISSUER_FE,
				    IFNULL(t.ISSUER_RP, t.ISSUER) AS ISSUER_RP,
				    t.FEE_NOTE,t.PCODE2,t.FROM_SYS,t.TRAN_CASE,
				    CASE WHEN SUBSTRING(LPAD(CAST(t.PCODE AS CHAR),6,'0'),1,2)='40' THEN NULL ELSE t.BB_BIN_ORIG END,
				    t.SRC,t.`DES`,t.PCODE_ORIG,
				    IFNULL(t.RC_ISS,'0000'), IFNULL(t.RC_BEN,'0000'),
				    CASE WHEN t.issuer = 602907 THEN IF(t.napas_edit_date IS NULL, t.napas_nd_date, t.napas_edit_date) ELSE IF(t.edit_date IS NULL, DATE_ADD(t.settlement_date, INTERVAL 1 DAY), DATE_ADD(DATE(t.edit_date), INTERVAL 1 DAY)) END AS napas_date,
				    CASE WHEN DATE(t.napas_EDIT_DATE)='2016-08-29' THEN DATE_SUB(t.napas_EDIT_DATE, INTERVAL 1 DAY) ELSE t.napas_EDIT_DATE END AS napas_EDIT_DATE,
				    t.STT,t.token,t.ADDRESPONSE,t.MVV,t.FEE_KEY,t.ACQ_COUNTRY,t.POS_ENTRY_CODE,t.POS_CONDITION_CODE,
				    IFNULL(t.F4, t.amount) AS F4,
				    CASE WHEN IFNULL(t.F5, t.amount) <= 0 AND SUBSTRING(LPAD(CAST(t.pcode AS CHAR),6,'0'),1,2)='42' THEN t.amount
				         WHEN SUBSTRING(LPAD(CAST(t.pcode AS CHAR),6,'0'),1,2) IN ('30','35','38','94') THEN 0
				         ELSE IFNULL(t.F5, t.amount) END AS F5,
				    t.F6,
				    t.F49,t.SETTLEMENT_CODE,t.SETTLEMENT_RATE,
				    t.ISS_CONV_RATE,t.TCC,
				    IFNULL(t.FEE_IRF_ISS,0),
				    CASE WHEN SUBSTRING(LPAD(CAST(t.PCODE AS CHAR),6,'0'),1,2) IN ('41','48') AND t.SETTLEMENT_DATE < STR_TO_DATE('2017-04-01','%Y-%m-%d') THEN -(ROUND(t.FEE_ISS * (SELECT CAST(PARAMVALUE AS DECIMAL(10,3)) FROM SYS_TBLPARAMETERS WHERE PARAMNAME='THUE'),0) + ROUND(IFNULL(t.FEE_IRF_ISS,0) * (SELECT CAST(PARAMVALUE AS DECIMAL(10,3)) FROM SYS_TBLPARAMETERS WHERE PARAMNAME='THUE'),0))
				         ELSE IFNULL(t.FEE_SVF_ISS,0) END AS FEE_SVF_ISS,
				    IFNULL(t.FEE_IRF_ACQ,0), IFNULL(t.FEE_SVF_ACQ,0), IFNULL(t.FEE_IRF_BEN,0), IFNULL(t.FEE_SVF_BEN,0),
				    t.Txnsrc,t.txndest,LEFT(t.content_fund,300),t.ADD_INFO,
				    CASE
				       WHEN t.Issuer_Rp = 600005 OR t.Acquirer_rp = 600005 THEN 4
				       WHEN t.Issuer_Rp = 600007 THEN 3
				       WHEN t.Issuer_Rp = 600006 THEN 2
				       WHEN t.Issuer_Rp = 602907 THEN 5
				       WHEN t.Acquirer_rp = :bankcardid THEN 8
				       WHEN t.Issuer_rp = 764000 OR t.BB_BIN = 764000 THEN 9
				       WHEN t.Issuer_rp = 600008 THEN 10
				       ELSE 1 END AS DATA_ID,
				    t.F60_UPI,t.F100_UPI,t.PREAMOUNT_USD,t.IS_PART_REV,t.TRANSIT_CSRR,t.MSGTYPE_DETAIL
				FROM shclog t
				WHERE t.edit_date BETWEEN :d1 AND DATE_ADD(:d2, INTERVAL 1 DAY)
				  AND LENGTH(TRIM(t.ACQUIRER)) = 6
				  AND t.FROM_SYS IS NOT NULL
				  AND (
				        (SUBSTRING(LPAD(CAST(t.PCODE AS CHAR),6,'0'),1,2)='42' AND IFNULL(t.FROM_SYS,'IST') LIKE '%IST%')
				       OR (t.FROM_SYS IS NOT NULL AND SUBSTRING(LPAD(CAST(t.PCODE AS CHAR),6,'0'),1,2) IN ('42','91'))
				      )
				  AND (
				        ( t.RESPCODE IN (110,111,113,115)
				          AND t.edit_date BETWEEN :d1 AND DATE_ADD(:d2, INTERVAL 1 DAY)
				          AND ( t.ISSUER NOT IN (602907,600005,600006,600007,605609,220699)
				                AND t.ACQUIRER NOT IN (605608,600005,605609,220699)
				                AND IFNULL(t.ISSUER_RP,0) NOT IN (764000,600008,600009,600011)
				                AND IFNULL(t.BB_BIN,0) NOT IN (764000,600009,600011)
				              )
				          AND t.PREAMOUNT IS NULL
				          AND t.SETTLEMENT_DATE < :d1
				        )
				        OR
				        ( t.RESPCODE IN (112,114)
				          AND t.edit_date BETWEEN :d1 AND DATE_ADD(:d2, INTERVAL 1 DAY)
				          AND ( t.ISSUER NOT IN (602907,600005,600006,600007,605609,220699)
				                AND t.ACQUIRER NOT IN (605608,600005,605609,220699)
				                AND IFNULL(t.ISSUER_RP,0) NOT IN (764000,600008,600009,600011)
				                AND IFNULL(t.BB_BIN,0) NOT IN (764000,600009,600011)
				              )
				          AND t.PREAMOUNT IS NOT NULL
				          AND t.SETTLEMENT_DATE < :d1
				        )
				      )
				""";

		String bankCarđI = proc_gaBccard_ID.getBankCardID();
		MapSqlParameterSource ps = new MapSqlParameterSource().addValue("bankcardid", bankCarđI)
				.addValue("d1", java.sql.Timestamp.valueOf(d1.atStartOfDay()))
				.addValue("d2", java.sql.Timestamp.valueOf(d2.atStartOfDay()))
				.addValue("dLastSett", java.sql.Date.valueOf(dLastSett)).addValue("oRate", oRate);
		log.info("insertAdjustFromShclog: {}", SqlLogUtils.renderSql(sql, ps.getValues()));
		jdbc.update(sql, ps);
	}

	private void recomputeFeesStage1() {
		// Block UPDATE #1: set Fee_Key, FEE_ISS, FEE_ACQ, FEE_IRF_ISS... theo công thức
		// NAPAS_* (giữ nguyên hàm DB)
		// LƯU Ý: Đã đổi DECODE -> CASE, NVL -> IFNULL, TRUNC/TO_CHAR -> MySQL
		// equivalents.
		String sqlSelect = """
				SELECT * FROM  SHCLOG_SETT_IBFT_ADJUST t
				WHERE t.msgtype=210 AND t.respcode=110 AND t.Fee_Key IS NULL AND t.FEE_NOTE NOT LIKE '%HACH TOAN DIEU CHINH%'
				""";
		String sql = """
				    UPDATE SHCLOG_SETT_IBFT_ADJUST t SET
				    FEE_KEY = NAPAS_GET_FEE_KEY(IFNULL(t.ACQUIRER_FE,t.ACQUIRER), IFNULL(t.ISSUER_FE,t.ISSUER),
				              CASE
				                WHEN t.PCODE2 IS NULL THEN t.PCODE
				                WHEN t.PCODE2 IN (810000,820000,830000,860000,870000,880000) THEN SUBSTRING(LPAD(CAST(t.PCODE2 AS CHAR),6,'0'),1,2)
				                WHEN t.PCODE2 IN (910000) AND (t.FROM_SYS='IST' OR t.TRAN_CASE='C3|72') THEN t.PCODE
				                ELSE SUBSTRING(LPAD(CAST(t.PCODE2 AS CHAR),6,'0'),1,2)
				              END,
				              CASE WHEN t.TRAN_CASE='72|C3' THEN 6011 WHEN t.PCODE2=880000 THEN 0 ELSE t.MERCHANT_TYPE END,
				              t.ACQ_CURRENCY_CODE,t.SETTLEMENT_DATE,t.TRACE),
				    FEE_ISS = OLD_FINAL_FEE_CAL_VER('ISS', FEE_KEY, IFNULL(t.ACQUIRER_FE,t.ACQUIRER), IFNULL(t.ISSUER_FE,t.ISSUER),
				              CASE WHEN t.FROM_SYS IS NULL THEN SUBSTRING(LPAD(CAST(t.PCODE AS CHAR),6,'0'),1,2) ELSE SUBSTRING(LPAD(CAST(t.PCODE2 AS CHAR),6,'0'),1,2) END,
				              t.ACQ_CURRENCY_CODE, IF(t.ACQ_CURRENCY_CODE=418, t.SETTLEMENT_AMOUNT, t.AMOUNT), t.CARDHOLDER_CONV_RATE, t.TRACE),
				    FEE_ACQ = OLD_FINAL_FEE_CAL_VER('ACQ', FEE_KEY, IFNULL(t.ACQUIRER_FE,t.ACQUIRER), IFNULL(t.ISSUER_FE,t.ISSUER),
				              CASE WHEN t.FROM_SYS IS NULL THEN SUBSTRING(LPAD(CAST(t.PCODE AS CHAR),6,'0'),1,2) ELSE SUBSTRING(LPAD(CAST(t.PCODE2 AS CHAR),6,'0'),1,2) END,
				              t.ACQ_CURRENCY_CODE, IF(t.ACQ_CURRENCY_CODE=418, t.SETTLEMENT_AMOUNT, t.AMOUNT), t.CARDHOLDER_CONV_RATE, t.TRACE),
				    FEE_IRF_ISS = NAPAS_FEE_OLD_TRAN(t.MSGTYPE,'IRF_ISS',FEE_KEY, IFNULL(t.ACQUIRER_FE,t.ACQUIRER), IFNULL(t.ISSUER_FE,t.ISSUER),
				              t.ACQ_CURRENCY_CODE, IF(t.ACQ_CURRENCY_CODE=418, t.SETTLEMENT_AMOUNT, t.AMOUNT), t.CARDHOLDER_CONV_RATE, t.TRACE),
				    FEE_SVF_ISS = NAPAS_FEE_OLD_TRAN(t.MSGTYPE,'SVF_ISS',FEE_KEY, IFNULL(t.ACQUIRER_FE,t.ACQUIRER), IFNULL(t.ISSUER_FE,t.ISSUER),
				              t.ACQ_CURRENCY_CODE, IF(t.ACQ_CURRENCY_CODE=418, t.SETTLEMENT_AMOUNT, t.AMOUNT), t.CARDHOLDER_CONV_RATE, t.TRACE),
				    FEE_IRF_ACQ = NAPAS_FEE_OLD_TRAN(t.MSGTYPE,'IRF_ACQ',FEE_KEY, IFNULL(t.ACQUIRER_FE,t.ACQUIRER), IFNULL(t.ISSUER_FE,t.ISSUER),
				              t.ACQ_CURRENCY_CODE, IF(t.ACQ_CURRENCY_CODE=418, t.SETTLEMENT_AMOUNT, t.AMOUNT), t.CARDHOLDER_CONV_RATE, t.TRACE),
				    FEE_SVF_ACQ = NAPAS_FEE_OLD_TRAN(t.MSGTYPE,'SVF_ACQ',FEE_KEY, IFNULL(t.ACQUIRER_FE,t.ACQUIRER), IFNULL(t.ISSUER_FE,t.ISSUER),
				              t.ACQ_CURRENCY_CODE, IF(t.ACQ_CURRENCY_CODE=418, t.SETTLEMENT_AMOUNT, t.AMOUNT), t.CARDHOLDER_CONV_RATE, t.TRACE),
				    FEE_IRF_BEN = NAPAS_FEE_OLD_TRAN(t.MSGTYPE,'IRF_BEN',FEE_KEY, IFNULL(t.ACQUIRER_FE,t.ACQUIRER), IFNULL(t.ISSUER_FE,t.ISSUER),
				              t.ACQ_CURRENCY_CODE, IF(t.ACQ_CURRENCY_CODE=418, t.SETTLEMENT_AMOUNT, t.AMOUNT), t.CARDHOLDER_CONV_RATE, t.TRACE),
				    FEE_SVF_BEN = NAPAS_FEE_OLD_TRAN(t.MSGTYPE,'SVF_BEN',FEE_KEY, IFNULL(t.ACQUIRER_FE,t.ACQUIRER), IFNULL(t.ISSUER_FE,t.ISSUER),
				              t.ACQ_CURRENCY_CODE, IF(t.ACQ_CURRENCY_CODE=418, t.SETTLEMENT_AMOUNT, t.AMOUNT), t.CARDHOLDER_CONV_RATE, t.TRACE),
				    FEE_NOTE = NAPAS_GET_FEE_NAME(FEE_KEY),
				    RE_FEE = 1
				    WHERE t.msgtype=210 AND t.respcode=110 AND t.Fee_Key IS NULL AND t.FEE_NOTE NOT LIKE '%HACH TOAN DIEU CHINH%';
				""";
		log.info("recomputeFeesStage1: {}", sql);
		jdbc.update(sql, new MapSqlParameterSource());
	}

	/**
	 * Block update 1
	 */
	private void recomputeFeesStage1_v2() {
		Long lastIndex = 0L;
		int pagesize = 5000;
		while (true) {
			List<ShclogSettIbftAdjustDto> listShclogSettIbftAdjustDto = fetchPage(lastIndex, pagesize);
			if (listShclogSettIbftAdjustDto.size() == 0)
				break;
			lastIndex = listShclogSettIbftAdjustDto.get(listShclogSettIbftAdjustDto.size() - 1).getId();
			for (ShclogSettIbftAdjustDto element : listShclogSettIbftAdjustDto) {
//				  public String getFeeKey(
//				            int acquirer,
//				            int issuer,
//				            String proCode,
//				            int merchantType,
//				            Integer currencyNullable,
//				            LocalDate settDate,
//				            long otrace
//				    ) 
				String acq, iss, procCode, merchantType;
				acq = element.getAcquirerFe() != null ? element.getAcquirerFe() + "" : (element.getAcquirer());
				iss = element.getIssuerFe() != null ? element.getIssuerFe() + "" : element.getIssuer();
				procCode = proCodeForGetFeeKey(element);
				merchantType = getMerchantType(element);
				String feeKey = cachedFee.getFeeKey(Helpers.str2Int(acq), Helpers.str2Int(iss), procCode,
						Helpers.str2Int(merchantType), element.getAcqCurrencyCode().intValue(),
						element.getSettlementDate(), element.getTrace().intValue());
				BigDecimal FEE_ISS, FEE_ACQ, FEE_IRF_ISS, FEE_SVF_ISS, FEE_IRF_ACQ, FEE_SVF_ACQ, FEE_IRF_BEN,
						FEE_SVF_BEN, FEE_NOTE;
				BigDecimal amount = getAmount(element);
				String proCodeForCalFee = getProCodeForCalFee(element);
//				   public BigDecimal oldFinalFeeCalVer(
//				            String feeFor,
//				            String feeKey,
//				            int acquirer,
//				            int issuer,
//				            String proCode,
//				            Integer currencyNullable,
//				            BigDecimal amount,
//				            BigDecimal conrate,
//				            long otrace
//				    ) {
				FEE_ISS = cachedFee.oldFinalFeeCalVer("ISS", feeKey, Helpers.str2Int(acq), Helpers.str2Int(iss),
						proCodeForCalFee, element.getAcqCurrencyCode().intValue(), amount, element.getIssConvRate(),
						element.getTrace().intValue());
				FEE_ACQ = cachedFee.oldFinalFeeCalVer("ACQ", feeKey, Helpers.str2Int(acq), Helpers.str2Int(iss),
						proCodeForCalFee, element.getAcqCurrencyCode().intValue(), amount, element.getIssConvRate(),
						element.getTrace().intValue());
//				NAPAS_FEE_OLD_TRAN(t.MSGTYPE,'SVF_ACQ',FEE_KEY, IFNULL(t.ACQUIRER_FE,t.ACQUIRER), IFNULL(t.ISSUER_FE,t.ISSUER),
//			              t.ACQ_CURRENCY_CODE, IF(t.ACQ_CURRENCY_CODE=418, t.SETTLEMENT_AMOUNT, t.AMOUNT), t.CARDHOLDER_CONV_RATE, t.TRACE),
//				 public BigDecimal napasFeeOldTran(
//				            int msgType,
//				            String feeFor,
//				            String feeKey,
//				            int acquirer,
//				            int issuer,
//				            Integer currencyNullable,
//				            BigDecimal amount,
//				            BigDecimal conrate,
//				            long otrace
//				    )
				FEE_IRF_ISS = cachedFee.napasFeeOldTran(element.getMsgtype().intValue(), "IRF_ISS", feeKey,
						Helpers.str2Int(acq), Helpers.str2Int(iss), element.getAcqCurrencyCode().intValue(), amount,
						element.getIssConvRate(), element.getTrace().intValue());
				FEE_SVF_ISS = cachedFee.napasFeeOldTran(element.getMsgtype().intValue(), "SVF_ISS", feeKey,
						Helpers.str2Int(acq), Helpers.str2Int(iss), element.getAcqCurrencyCode().intValue(), amount,
						element.getIssConvRate(), element.getTrace().intValue());
				FEE_IRF_ACQ = cachedFee.napasFeeOldTran(element.getMsgtype().intValue(), "IRF_ACQ", feeKey,
						Helpers.str2Int(acq), Helpers.str2Int(iss), element.getAcqCurrencyCode().intValue(), amount,
						element.getIssConvRate(), element.getTrace().intValue());
				FEE_SVF_ACQ = cachedFee.napasFeeOldTran(element.getMsgtype().intValue(), "SVF_ACQ", feeKey,
						Helpers.str2Int(acq), Helpers.str2Int(iss), element.getAcqCurrencyCode().intValue(), amount,
						element.getIssConvRate(), element.getTrace().intValue());
				FEE_IRF_BEN = cachedFee.napasFeeOldTran(element.getMsgtype().intValue(), "IRF_BEN", feeKey,
						Helpers.str2Int(acq), Helpers.str2Int(iss), element.getAcqCurrencyCode().intValue(), amount,
						element.getIssConvRate(), element.getTrace().intValue());
				FEE_SVF_BEN = cachedFee.napasFeeOldTran(element.getMsgtype().intValue(), "SVF_BEN", feeKey,
						Helpers.str2Int(acq), Helpers.str2Int(iss), element.getAcqCurrencyCode().intValue(), amount,
						element.getIssConvRate(), element.getTrace().intValue());
				FEE_NOTE = cachedFee.napasFeeOldTran(element.getMsgtype().intValue(), "NOTE", feeKey,
						Helpers.str2Int(acq), Helpers.str2Int(iss), element.getAcqCurrencyCode().intValue(), amount,
						element.getIssConvRate(), element.getTrace().intValue());
				element.setFeeAcq(FEE_ACQ);
				element.setFeeIss(FEE_ISS);
				element.setFeeIrfIss(FEE_IRF_ISS);
				element.setFeeSvfIss(FEE_SVF_ISS);
				element.setFeeIrfAcq(FEE_IRF_ACQ);
				element.setFeeSvfAcq(FEE_SVF_ACQ);
				element.setFeeIrfBen(FEE_IRF_BEN);
				element.setFeeSvfBen(FEE_SVF_BEN);
				element.setFeeNote(FEE_NOTE + "");
				doUpdate(element);
			}
		}

	}

	private void recomputeFeesStage(String wherecommand) {
		Long lastIndex = 0L;
		int pagesize = 5000;
		while (true) {
			List<ShclogSettIbftAdjustDto> listShclogSettIbftAdjustDto = fetchPage(wherecommand, lastIndex, pagesize);
			if (listShclogSettIbftAdjustDto.size() == 0)
				break;
			lastIndex = listShclogSettIbftAdjustDto.get(listShclogSettIbftAdjustDto.size() - 1).getId();
			for (ShclogSettIbftAdjustDto element : listShclogSettIbftAdjustDto) {
//				  public String getFeeKey(
//				            int acquirer,
//				            int issuer,
//				            String proCode,
//				            int merchantType,
//				            Integer currencyNullable,
//				            LocalDate settDate,
//				            long otrace
//				    ) 
				String acq, iss, procCode, merchantType;
				acq = element.getAcquirerFe() != null ? element.getAcquirerFe() + "" : (element.getAcquirer());
				iss = element.getIssuerFe() != null ? element.getIssuerFe() + "" : element.getIssuer();
				procCode = proCodeForGetFeeKey(element);
				merchantType = getMerchantType(element);
				String feeKey = cachedFee.getFeeKey(Helpers.str2Int(acq), Helpers.str2Int(iss), procCode,
						Helpers.str2Int(merchantType), element.getAcqCurrencyCode().intValue(),
						element.getSettlementDate(), element.getTrace().intValue());
				BigDecimal FEE_ISS, FEE_ACQ, FEE_IRF_ISS, FEE_SVF_ISS, FEE_IRF_ACQ, FEE_SVF_ACQ, FEE_IRF_BEN,
						FEE_SVF_BEN, FEE_NOTE;
				BigDecimal amount = getAmount(element);
				String proCodeForCalFee = getProCodeForCalFee(element);
//				   public BigDecimal oldFinalFeeCalVer(
//				            String feeFor,
//				            String feeKey,
//				            int acquirer,
//				            int issuer,
//				            String proCode,
//				            Integer currencyNullable,
//				            BigDecimal amount,
//				            BigDecimal conrate,
//				            long otrace
//				    ) {
				FEE_ISS = cachedFee.oldFinalFeeCalVer("ISS", feeKey, Helpers.str2Int(acq), Helpers.str2Int(iss),
						proCodeForCalFee, element.getAcqCurrencyCode().intValue(), amount, element.getIssConvRate(),
						element.getTrace().intValue());
				FEE_ACQ = cachedFee.oldFinalFeeCalVer("ACQ", feeKey, Helpers.str2Int(acq), Helpers.str2Int(iss),
						proCodeForCalFee, element.getAcqCurrencyCode().intValue(), amount, element.getIssConvRate(),
						element.getTrace().intValue());
//				NAPAS_FEE_OLD_TRAN(t.MSGTYPE,'SVF_ACQ',FEE_KEY, IFNULL(t.ACQUIRER_FE,t.ACQUIRER), IFNULL(t.ISSUER_FE,t.ISSUER),
//			              t.ACQ_CURRENCY_CODE, IF(t.ACQ_CURRENCY_CODE=418, t.SETTLEMENT_AMOUNT, t.AMOUNT), t.CARDHOLDER_CONV_RATE, t.TRACE),
//				 public BigDecimal napasFeeOldTran(
//				            int msgType,
//				            String feeFor,
//				            String feeKey,
//				            int acquirer,
//				            int issuer,
//				            Integer currencyNullable,
//				            BigDecimal amount,
//				            BigDecimal conrate,
//				            long otrace
//				    )
				FEE_IRF_ISS = cachedFee.napasFeeOldTran(element.getMsgtype().intValue(), "IRF_ISS", feeKey,
						Helpers.str2Int(acq), Helpers.str2Int(iss), element.getAcqCurrencyCode().intValue(), amount,
						element.getIssConvRate(), element.getTrace().intValue());
				FEE_SVF_ISS = cachedFee.napasFeeOldTran(element.getMsgtype().intValue(), "SVF_ISS", feeKey,
						Helpers.str2Int(acq), Helpers.str2Int(iss), element.getAcqCurrencyCode().intValue(), amount,
						element.getIssConvRate(), element.getTrace().intValue());
				FEE_IRF_ACQ = cachedFee.napasFeeOldTran(element.getMsgtype().intValue(), "IRF_ACQ", feeKey,
						Helpers.str2Int(acq), Helpers.str2Int(iss), element.getAcqCurrencyCode().intValue(), amount,
						element.getIssConvRate(), element.getTrace().intValue());
				FEE_SVF_ACQ = cachedFee.napasFeeOldTran(element.getMsgtype().intValue(), "SVF_ACQ", feeKey,
						Helpers.str2Int(acq), Helpers.str2Int(iss), element.getAcqCurrencyCode().intValue(), amount,
						element.getIssConvRate(), element.getTrace().intValue());
				FEE_IRF_BEN = cachedFee.napasFeeOldTran(element.getMsgtype().intValue(), "IRF_BEN", feeKey,
						Helpers.str2Int(acq), Helpers.str2Int(iss), element.getAcqCurrencyCode().intValue(), amount,
						element.getIssConvRate(), element.getTrace().intValue());
				FEE_SVF_BEN = cachedFee.napasFeeOldTran(element.getMsgtype().intValue(), "SVF_BEN", feeKey,
						Helpers.str2Int(acq), Helpers.str2Int(iss), element.getAcqCurrencyCode().intValue(), amount,
						element.getIssConvRate(), element.getTrace().intValue());
				FEE_NOTE = cachedFee.napasFeeOldTran(element.getMsgtype().intValue(), "NOTE", feeKey,
						Helpers.str2Int(acq), Helpers.str2Int(iss), element.getAcqCurrencyCode().intValue(), amount,
						element.getIssConvRate(), element.getTrace().intValue());
				element.setFeeAcq(FEE_ACQ);
				element.setFeeIss(FEE_ISS);
				element.setFeeIrfIss(FEE_IRF_ISS);
				element.setFeeSvfIss(FEE_SVF_ISS);
				element.setFeeIrfAcq(FEE_IRF_ACQ);
				element.setFeeSvfAcq(FEE_SVF_ACQ);
				element.setFeeIrfBen(FEE_IRF_BEN);
				element.setFeeSvfBen(FEE_SVF_BEN);
				element.setFeeNote(FEE_NOTE + "");
				doUpdate(element);
			}
		}

	}

	private void recomputeFeesStage3(String wherecommand) {
		Long lastIndex = 0L;
		int pagesize = 5000;
		while (true) {
			List<ShclogSettIbftAdjustDto> listShclogSettIbftAdjustDto = fetchPage(wherecommand, lastIndex, pagesize);
			if (listShclogSettIbftAdjustDto.size() == 0)
				break;
			lastIndex = listShclogSettIbftAdjustDto.get(listShclogSettIbftAdjustDto.size() - 1).getId();
			for (ShclogSettIbftAdjustDto element : listShclogSettIbftAdjustDto) {
//				  public String getFeeKey(
//				            int acquirer,
//				            int issuer,
//				            String proCode,
//				            int merchantType,
//				            Integer currencyNullable,
//				            LocalDate settDate,
//				            long otrace
//				    ) 
				String acq, iss, procCode, merchantType;
				acq = element.getAcquirerFe() != null ? element.getAcquirerFe() + "" : (element.getAcquirer());
				iss = element.getIssuerFe() != null ? element.getIssuerFe() + "" : element.getIssuer();
				procCode = proCodeForGetFeeKey(element);
				merchantType = getMerchantType(element);
				String feeKey = cachedFee.getFeeKey(Helpers.str2Int(acq), Helpers.str2Int(iss), procCode,
						Helpers.str2Int(merchantType), element.getAcqCurrencyCode().intValue(),
						element.getSettlementDate(), element.getTrace().intValue());
				BigDecimal FEE_ISS, FEE_ACQ, FEE_IRF_ISS, FEE_SVF_ISS, FEE_IRF_ACQ, FEE_SVF_ACQ, FEE_IRF_BEN,
						FEE_SVF_BEN, FEE_NOTE;
				BigDecimal amount = getAmount(element);
				String proCodeForCalFee = getProCodeForCalFee(element);
//				   public BigDecimal oldFinalFeeCalVer(
//				            String feeFor,
//				            String feeKey,
//				            int acquirer,
//				            int issuer,
//				            String proCode,
//				            Integer currencyNullable,
//				            BigDecimal amount,
//				            BigDecimal conrate,
//				            long otrace
//				    ) {
				FEE_ISS = cachedFee.oldFinalFeeCalVer("ISS", feeKey, Helpers.str2Int(acq), Helpers.str2Int(iss),
						proCodeForCalFee, element.getAcqCurrencyCode().intValue(), amount, element.getIssConvRate(),
						element.getTrace().intValue());
				FEE_ACQ = cachedFee.oldFinalFeeCalVer("ACQ", feeKey, Helpers.str2Int(acq), Helpers.str2Int(iss),
						proCodeForCalFee, element.getAcqCurrencyCode().intValue(), amount, element.getIssConvRate(),
						element.getTrace().intValue());
				FEE_IRF_ISS = cachedFee.napasFeeOldTran(element.getMsgtype().intValue(), "IRF_ISS", feeKey,
						Helpers.str2Int(acq), Helpers.str2Int(iss), element.getAcqCurrencyCode().intValue(), amount,
						element.getIssConvRate(), element.getTrace().intValue());
				FEE_SVF_ISS = cachedFee.napasFeeOldTran(element.getMsgtype().intValue(), "SVF_ISS", feeKey,
						Helpers.str2Int(acq), Helpers.str2Int(iss), element.getAcqCurrencyCode().intValue(), amount,
						element.getIssConvRate(), element.getTrace().intValue());
				FEE_IRF_ACQ = cachedFee.napasFeeOldTran(element.getMsgtype().intValue(), "IRF_ACQ", feeKey,
						Helpers.str2Int(acq), Helpers.str2Int(iss), element.getAcqCurrencyCode().intValue(), amount,
						element.getIssConvRate(), element.getTrace().intValue());
				FEE_SVF_ACQ = cachedFee.napasFeeOldTran(element.getMsgtype().intValue(), "SVF_ACQ", feeKey,
						Helpers.str2Int(acq), Helpers.str2Int(iss), element.getAcqCurrencyCode().intValue(), amount,
						element.getIssConvRate(), element.getTrace().intValue());
				FEE_IRF_BEN = cachedFee.napasFeeOldTran(element.getMsgtype().intValue(), "IRF_BEN", feeKey,
						Helpers.str2Int(acq), Helpers.str2Int(iss), element.getAcqCurrencyCode().intValue(), amount,
						element.getIssConvRate(), element.getTrace().intValue());
				FEE_SVF_BEN = cachedFee.napasFeeOldTran(element.getMsgtype().intValue(), "SVF_BEN", feeKey,
						Helpers.str2Int(acq), Helpers.str2Int(iss), element.getAcqCurrencyCode().intValue(), amount,
						element.getIssConvRate(), element.getTrace().intValue());
				FEE_NOTE = cachedFee.napasFeeOldTran(element.getMsgtype().intValue(), "NOTE", feeKey,
						Helpers.str2Int(acq), Helpers.str2Int(iss), element.getAcqCurrencyCode().intValue(), amount,
						element.getIssConvRate(), element.getTrace().intValue());
//				element.setFeeAcq(FEE_ACQ);
//				element.setFeeIss(FEE_ISS);
				element.setFeeIrfIss(FEE_IRF_ISS);
				element.setFeeSvfIss(FEE_SVF_ISS);
				element.setFeeIrfAcq(FEE_IRF_ACQ);
				element.setFeeSvfAcq(FEE_SVF_ACQ);
				element.setFeeIrfBen(FEE_IRF_BEN);
				element.setFeeSvfBen(FEE_SVF_BEN);
//				element.setFeeNote(FEE_NOTE + "");
				doUpdate2(element);
			}
		}

	}

	private void doUpdate(ShclogSettIbftAdjustDto u) throws DataAccessException {
		String sql = """
				    UPDATE SHCLOG_SETT_IBFT_ADJUST
				    SET FEE_KEY=:feeKey,
				        FEE_ISS=:feeIss,
				        FEE_ACQ=:feeAcq,
				        FEE_IRF_ISS=:feeIrfIss,
				        FEE_SVF_ISS=:feeSvfIss,
				        FEE_IRF_ACQ=:feeIrfAcq,
				        FEE_SVF_ACQ=:feeSvfAcq,
				        FEE_IRF_BEN=:feeIrfBen,
				        FEE_SVF_BEN=:feeSvfBen,
				        FEE_NOTE=:feeNote,
				        RE_FEE=1
				    WHERE id=:id
				""";
		MapSqlParameterSource p = new MapSqlParameterSource().addValue("id", u.getId())
				.addValue("feeKey", u.getFeeKey()).addValue("feeIss", u.getFeeIss()).addValue("feeAcq", u.getFeeAcq())
				.addValue("feeIrfIss", u.getFeeIrfIss()).addValue("feeSvfIss", u.getFeeSvfIss())
				.addValue("feeIrfAcq", u.getFeeIrfAcq()).addValue("feeSvfAcq", u.getFeeSvfAcq())
				.addValue("feeIrfBen", u.getFeeIrfBen()).addValue("feeSvfBen", u.getFeeSvfBen())
				.addValue("feeNote", u.getFeeNote());
//	                .addValue("reFee", u.reFee());
		log.info("Sql log: {}",SqlLogUtils.renderSql(sql, p.getValues()));
		jdbc.update(sql, p);
	}

	private void recomputeFeesStage2(String wherecommand) {
		Long lastIndex = 0L;
		int pagesize = 5000;
		while (true) {
			List<ShclogSettIbftAdjustDto> listShclogSettIbftAdjustDto = fetchPage(wherecommand, lastIndex, pagesize);
			if (listShclogSettIbftAdjustDto.size() == 0)
				break;
			lastIndex = listShclogSettIbftAdjustDto.get(listShclogSettIbftAdjustDto.size() - 1).getId();
			for (ShclogSettIbftAdjustDto element : listShclogSettIbftAdjustDto) {
//				  public String getFeeKey(
//				            int acquirer,
//				            int issuer,
//				            String proCode,
//				            int merchantType,
//				            Integer currencyNullable,
//				            LocalDate settDate,
//				            long otrace
//				    ) 
				String acq, iss, procCode, merchantType;
				acq = element.getAcquirerFe() != null ? element.getAcquirerFe() + "" : (element.getAcquirer());
				iss = element.getIssuerFe() != null ? element.getIssuerFe() + "" : element.getIssuer();
				procCode = proCodeForGetFeeKey(element);
				merchantType = getMerchantType(element);
				String feeKey = element.getFeeKey();
				BigDecimal FEE_ISS, FEE_ACQ, FEE_IRF_ISS, FEE_SVF_ISS, FEE_IRF_ACQ, FEE_SVF_ACQ, FEE_IRF_BEN,
						FEE_SVF_BEN, FEE_NOTE;
				BigDecimal amount = getAmount(element);
				String proCodeForCalFee = getProCodeForCalFee(element);
//				   public BigDecimal oldFinalFeeCalVer(
//				            String feeFor,
//				            String feeKey,
//				            int acquirer,
//				            int issuer,
//				            String proCode,
//				            Integer currencyNullable,
//				            BigDecimal amount,
//				            BigDecimal conrate,
//				            long otrace
//				    ) {
//				FEE_ISS = cachedFee.oldFinalFeeCalVer("ISS", feeKey, Helpers.str2Int(acq), Helpers.str2Int(iss),
//						proCodeForCalFee, element.getAcqCurrencyCode().intValue(), amount, element.getIssConvRate(),
//						element.getTrace().intValue());
//				FEE_ACQ = cachedFee.oldFinalFeeCalVer("ACQ", feeKey, Helpers.str2Int(acq), Helpers.str2Int(iss),
//						proCodeForCalFee, element.getAcqCurrencyCode().intValue(), amount, element.getIssConvRate(),
//						element.getTrace().intValue());
//				NAPAS_FEE_OLD_TRAN(t.MSGTYPE,'SVF_ACQ',FEE_KEY, IFNULL(t.ACQUIRER_FE,t.ACQUIRER), IFNULL(t.ISSUER_FE,t.ISSUER),
//			              t.ACQ_CURRENCY_CODE, IF(t.ACQ_CURRENCY_CODE=418, t.SETTLEMENT_AMOUNT, t.AMOUNT), t.CARDHOLDER_CONV_RATE, t.TRACE),
//				 public BigDecimal napasFeeOldTran(
//				            int msgType,
//				            String feeFor,
//				            String feeKey,
//				            int acquirer,
//				            int issuer,
//				            Integer currencyNullable,
//				            BigDecimal amount,
//				            BigDecimal conrate,
//				            long otrace
//				    )
				FEE_IRF_ISS = cachedFee.napasFeeOldTran(element.getMsgtype().intValue(), "IRF_ISS", feeKey,
						Helpers.str2Int(acq), Helpers.str2Int(iss), element.getAcqCurrencyCode().intValue(), amount,
						element.getIssConvRate(), element.getTrace().intValue());
				FEE_SVF_ISS = cachedFee.napasFeeOldTran(element.getMsgtype().intValue(), "SVF_ISS", feeKey,
						Helpers.str2Int(acq), Helpers.str2Int(iss), element.getAcqCurrencyCode().intValue(), amount,
						element.getIssConvRate(), element.getTrace().intValue());
				FEE_IRF_ACQ = cachedFee.napasFeeOldTran(element.getMsgtype().intValue(), "IRF_ACQ", feeKey,
						Helpers.str2Int(acq), Helpers.str2Int(iss), element.getAcqCurrencyCode().intValue(), amount,
						element.getIssConvRate(), element.getTrace().intValue());
				FEE_SVF_ACQ = cachedFee.napasFeeOldTran(element.getMsgtype().intValue(), "SVF_ACQ", feeKey,
						Helpers.str2Int(acq), Helpers.str2Int(iss), element.getAcqCurrencyCode().intValue(), amount,
						element.getIssConvRate(), element.getTrace().intValue());
				FEE_IRF_BEN = cachedFee.napasFeeOldTran(element.getMsgtype().intValue(), "IRF_BEN", feeKey,
						Helpers.str2Int(acq), Helpers.str2Int(iss), element.getAcqCurrencyCode().intValue(), amount,
						element.getIssConvRate(), element.getTrace().intValue());
				FEE_SVF_BEN = cachedFee.napasFeeOldTran(element.getMsgtype().intValue(), "SVF_BEN", feeKey,
						Helpers.str2Int(acq), Helpers.str2Int(iss), element.getAcqCurrencyCode().intValue(), amount,
						element.getIssConvRate(), element.getTrace().intValue());
//				FEE_NOTE = cachedFee.napasFeeOldTran(element.getMsgtype().intValue(), "NOTE", feeKey,
//						Helpers.str2Int(acq), Helpers.str2Int(iss), element.getAcqCurrencyCode().intValue(), amount,
//						element.getIssConvRate(), element.getTrace().intValue());
//				element.setFeeAcq(FEE_ACQ);
//				element.setFeeIss(FEE_ISS);
				element.setFeeIrfIss(FEE_IRF_ISS);
				element.setFeeSvfIss(FEE_SVF_ISS);
				element.setFeeIrfAcq(FEE_IRF_ACQ);
				element.setFeeSvfAcq(FEE_SVF_ACQ);
				element.setFeeIrfBen(FEE_IRF_BEN);
				element.setFeeSvfBen(FEE_SVF_BEN);
//				element.setFeeNote(FEE_NOTE + "");
				doUpdate2(element);
			}
		}

	}

	private void doUpdate2(ShclogSettIbftAdjustDto u) throws DataAccessException {
		String sql = """
				    UPDATE SHCLOG_SETT_IBFT_ADJUST
				    SET FEE_IRF_ISS=:feeIrfIss,
				        FEE_SVF_ISS=:feeSvfIss,
				        FEE_IRF_ACQ=:feeIrfAcq,
				        FEE_SVF_ACQ=:feeSvfAcq,
				        FEE_IRF_BEN=:feeIrfBen,
				        FEE_SVF_BEN=:feeSvfBen,
				        RE_FEE=1
				    WHERE id=:id
				""";
		MapSqlParameterSource p = new MapSqlParameterSource().addValue("id", u.getId())
				.addValue("feeIrfIss", u.getFeeIrfIss()).addValue("feeSvfIss", u.getFeeSvfIss())
				.addValue("feeIrfAcq", u.getFeeIrfAcq()).addValue("feeSvfAcq", u.getFeeSvfAcq())
				.addValue("feeIrfBen", u.getFeeIrfBen()).addValue("feeSvfBen", u.getFeeSvfBen());
//	                .addValue("reFee", u.reFee());
		log.info("Sql log: {}",SqlLogUtils.renderSql(sql, p.getValues()));
		jdbc.update(sql, p);
	}

//	 CASE
//    WHEN t.PCODE2 IS NULL THEN t.PCODE
//    WHEN t.PCODE2 IN (810000,820000,830000,860000,870000,880000) THEN SUBSTRING(LPAD(CAST(t.PCODE2 AS CHAR),6,'0'),1,2)
//    WHEN t.PCODE2 IN (910000) AND (t.FROM_SYS='IST' OR t.TRAN_CASE='C3|72') THEN t.PCODE
//    ELSE SUBSTRING(LPAD(CAST(t.PCODE2 AS CHAR),6,'0'),1,2)
//  END,
	private String proCodeForGetFeeKey(ShclogSettIbftAdjustDto r) {
		if (r.getPcode2() == null) {
			return String.valueOf(r.getPcode());
		}
		// các case đặc biệt
		if (Helpers.in(r.getPcode2().intValue(), 810000, 820000, 830000, 860000, 870000, 880000)) {
			return Helpers.left2Padded(r.getPcode2().intValue());
		}
		if (r.getPcode2() != null && r.getPcode2().intValue() == 910000
				&& ("IST".equalsIgnoreCase(Helpers.nv(r.getFromSys()))
						|| "C3|72".equalsIgnoreCase(Helpers.nv(r.getTranCase())))) {
			return String.valueOf(r.getPcode());
		}
		return Helpers.left2Padded(r.getPcode2().intValue());
	}

//	CASE WHEN t.FROM_SYS IS NULL THEN SUBSTRING(LPAD(CAST(t.PCODE AS CHAR),6,'0'),1,2) ELSE SUBSTRING(LPAD(CAST(t.PCODE2 AS CHAR),6,'0'),1,2) END ban chuyen
	private String getProCodeForCalFee(ShclogSettIbftAdjustDto r) {
		if (r.getFromSys() == null) {
			// Xử lý PCODE
			String pcodeStr = String.format("%06d", r.getPcode()); // LPAD(...,6,'0')
			return pcodeStr.substring(0, 2); // SUBSTRING(...,1,2)
		} else {
			// Xử lý PCODE2
			String pcode2Str = String.format("%06d", r.getPcode2().intValue());
			return pcode2Str.substring(0, 2);
		}
	}

	private String getMerchantType(ShclogSettIbftAdjustDto r) {
//		   CASE WHEN t.TRAN_CASE='72|C3' THEN 6011 WHEN t.PCODE2=880000 THEN 0 ELSE t.MERCHANT_TYPE END,
		if (Helpers.in(r.getTranCase(), "C3", "72")) {
			return "6011";
		}
		if (r.getPcode2().intValue() == 880000)
			return "0";
		return r.getMerchantType().intValue() + "";
	}

	private BigDecimal getAmount(ShclogSettIbftAdjustDto r) {
//		  IF(t.ACQ_CURRENCY_CODE=418, t.SETTLEMENT_AMOUNT, t.AMOUNT)

		if (r.getAcqCurrencyCode().intValue() == 418) {
			return r.getSettlementAmount();
		} else
			return r.getAmount();
	}

	public List<ShclogSettIbftAdjustDto> fetchPage(long lastIdExclusive, int pageSize) {
		String sqlSelect = """
				  SELECT
				  tidb_id as ID,
				  MSGTYPE, PAN, PCODE, AMOUNT, ACQ_CURRENCY_CODE,
				  FEE_ISS, FEE_ACQ, TRACE, LOCAL_TIME, LOCAL_DATE, SETTLEMENT_DATE,
				  ACQUIRER, ISSUER, RESPCODE, MERCHANT_TYPE, CH_CURRENCY_CODE, TERMID,
				  ACCTNUM, ISS_CURRENCY_CODE, TRANDATE, TRANTIME, CARDPRODUCT, REVCODE,
				  ORIGTRACE, ACCEPTORNAME, TERMLOC, TGGUIQT, TGXLNV, TGGUINV, TGGUIQTP,
				  EDIT_DATE, EDIT_USER, SML_VERIFY, ORIGISS, ORIGRESPCODE, ISREV, PREAMOUNT,
				  CAP_DATE, AUTHNUM, THU, THU_REFUN, TT1, TT2, TT3, TT4, PPCODE,
				  SETTLEMENT_AMOUNT, CARDHOLDER_AMOUNT, SETTLEMENT_CONV_RATE, CARDHOLDER_CONV_RATE,
				  BB_BIN, FORWARD_INST, TRANSFEREE, REFNUM, SETT_CURRENCY_CODE, PRE_CARDHOLDER_AMOUNT,
				  MERCHANT_TYPE_ORIG, ONLY_SML, FEE_NOTE, ACQUIRER_FE, ACQUIRER_RP, ISSUER_FE, ISSUER_RP,
				  RC_ISS_72, RC_ACQ_72, PCODE2, FROM_SYS, TRAN_CASE, BB_BIN_ORIG, SRC, DES, PCODE_ORIG,
				  RC_ISS, RC_BEN, NAPAS_DATE, NAPAS_EDIT_DATE, ADDRESPONSE, MVV, STT,
				  FEE_IRF_ISS, FEE_SVF_ISS, FEE_IRF_ACQ, FEE_SVF_ACQ, FEE_IRF_PAY_AT, FEE_SVF_PAY_AT,
				  FEE_IRF_REC_AT, FEE_SVF_REC_AT, FEE_IRF_BEN, FEE_SVF_BEN,
				  POS_CONDITION_CODE, POS_ENTRY_CODE, TCC, FEE_KEY, ACQ_COUNTRY, TERMID1, TOKEN,
				  RC, SETTLEMENT_CODE, SETTLEMENT_RATE, ISS_CONV_RATE, F49, F5, F4, F6,
				  SVFISSNP, IRFISSACQ, IRFISSBNB, SVFACQNP, IRFACQISS, IRFACQBNB,
				  SVFBNBNP, IRFBNBISS, IRFBNBACQ, RE_FEE, ISS_SEND, ACQ_SEND, BNB_SEND,
				  TXNSRC, TXNDEST, PREAMOUNT_USD, CONTENT_FUND, DATA_ID, ADD_INFO, PREAMOUNT_ACQ,
				  F60_UPI, F100_UPI, IS_PART_REV, TRANSIT_CSRR, MSGTYPE_DETAIL
				FROM SHCLOG_SETT_IBFT_ADJUST t
				WHERE tidb_id > ? AND t.msgtype=210 AND t.respcode=110 AND t.Fee_Key IS NULL AND t.FEE_NOTE NOT LIKE '%HACH TOAN DIEU CHINH%'
				ORDER BY tidb_id
				LIMIT ?
				""";
		return jdbcTemplate.query(sqlSelect, SHCLOG_ROW_MAPPER, lastIdExclusive, pageSize);
	}

	public List<ShclogSettIbftAdjustDto> fetchPage(String wherecommand, long lastIdExclusive, int pageSize) {
		String sqlSelect = """
								  SELECT
								  tidb_id As ID,
								  MSGTYPE, PAN, PCODE, AMOUNT, ACQ_CURRENCY_CODE,
								  FEE_ISS, FEE_ACQ, TRACE, LOCAL_TIME, LOCAL_DATE, SETTLEMENT_DATE,
								  ACQUIRER, ISSUER, RESPCODE, MERCHANT_TYPE, CH_CURRENCY_CODE, TERMID,
								  ACCTNUM, ISS_CURRENCY_CODE, TRANDATE, TRANTIME, CARDPRODUCT, REVCODE,
								  ORIGTRACE, ACCEPTORNAME, TERMLOC, TGGUIQT, TGXLNV, TGGUINV, TGGUIQTP,
								  EDIT_DATE, EDIT_USER, SML_VERIFY, ORIGISS, ORIGRESPCODE, ISREV, PREAMOUNT,
								  CAP_DATE, AUTHNUM, THU, THU_REFUN, TT1, TT2, TT3, TT4, PPCODE,
								  SETTLEMENT_AMOUNT, CARDHOLDER_AMOUNT, SETTLEMENT_CONV_RATE, CARDHOLDER_CONV_RATE,
								  BB_BIN, FORWARD_INST, TRANSFEREE, REFNUM, SETT_CURRENCY_CODE, PRE_CARDHOLDER_AMOUNT,
								  MERCHANT_TYPE_ORIG, ONLY_SML, FEE_NOTE, ACQUIRER_FE, ACQUIRER_RP, ISSUER_FE, ISSUER_RP,
								  RC_ISS_72, RC_ACQ_72, PCODE2, FROM_SYS, TRAN_CASE, BB_BIN_ORIG, SRC, DES, PCODE_ORIG,
								  RC_ISS, RC_BEN, NAPAS_DATE, NAPAS_EDIT_DATE, ADDRESPONSE, MVV, STT,
								  FEE_IRF_ISS, FEE_SVF_ISS, FEE_IRF_ACQ, FEE_SVF_ACQ, FEE_IRF_PAY_AT, FEE_SVF_PAY_AT,
								  FEE_IRF_REC_AT, FEE_SVF_REC_AT, FEE_IRF_BEN, FEE_SVF_BEN,
								  POS_CONDITION_CODE, POS_ENTRY_CODE, TCC, FEE_KEY, ACQ_COUNTRY, TERMID1, TOKEN,
								  RC, SETTLEMENT_CODE, SETTLEMENT_RATE, ISS_CONV_RATE, F49, F5, F4, F6,
								  SVFISSNP, IRFISSACQ, IRFISSBNB, SVFACQNP, IRFACQISS, IRFACQBNB,
								  SVFBNBNP, IRFBNBISS, IRFBNBACQ, RE_FEE, ISS_SEND, ACQ_SEND, BNB_SEND,
								  TXNSRC, TXNDEST, PREAMOUNT_USD, CONTENT_FUND, DATA_ID, ADD_INFO, PREAMOUNT_ACQ,
								  F60_UPI, F100_UPI, IS_PART_REV, TRANSIT_CSRR, MSGTYPE_DETAIL
								FROM SHCLOG_SETT_IBFT_ADJUST AS t
								""";
		String sqlFormat = String.format("%s %s AND tidb_id > ? ORDER BY tidb_id LIMIT ?", sqlSelect, wherecommand);
		return jdbcTemplate.query(sqlFormat, SHCLOG_ROW_MAPPER, lastIdExclusive, pageSize);
	}

	/**
	 * block update 2
	 */
	private void recomputeFeesStage2_OldRange() {
		// Block UPDATE #2: áp dụng cho khoảng valid_to <= 2017-10-01 và resp in
		// (110,112,113,114,115)
//		String sql = """
//				    UPDATE SHCLOG_SETT_IBFT_ADJUST t
//				    JOIN (
//				        SELECT Fee_Key FROM Gr_Fee_Config_New WHERE DATE(Valid_To) <= '2017-10-01'
//				    ) g ON t.Fee_Key = g.Fee_Key
//				    SET
//				      t.FEE_IRF_ISS = NAPAS_FEE_OLD_TRAN(t.MSGTYPE,'IRF_ISS',t.Fee_Key, IFNULL(t.ACQUIRER_FE,t.ACQUIRER), IFNULL(t.ISSUER_FE,t.ISSUER), t.ACQ_CURRENCY_CODE, IF(t.ACQ_CURRENCY_CODE=418, t.SETTLEMENT_AMOUNT, t.AMOUNT), t.CARDHOLDER_CONV_RATE, t.TRACE),
//				      t.FEE_SVF_ISS = NAPAS_FEE_OLD_TRAN(t.MSGTYPE,'SVF_ISS',t.Fee_Key, IFNULL(t.ACQUIRER_FE,t.ACQUIRER), IFNULL(t.ISSUER_FE,t.ISSUER), t.ACQ_CURRENCY_CODE, IF(t.ACQ_CURRENCY_CODE=418, t.SETTLEMENT_AMOUNT, t.AMOUNT), t.CARDHOLDER_CONV_RATE, t.TRACE),
//				      t.FEE_IRF_ACQ = NAPAS_FEE_OLD_TRAN(t.MSGTYPE,'IRF_ACQ',t.Fee_Key, IFNULL(t.ACQUIRER_FE,t.ACQUIRER), IFNULL(t.ISSUER_FE,t.ISSUER), t.ACQ_CURRENCY_CODE, IF(t.ACQ_CURRENCY_CODE=418, t.SETTLEMENT_AMOUNT, t.AMOUNT), t.CARDHOLDER_CONV_RATE, t.TRACE),
//				      t.FEE_SVF_ACQ = NAPAS_FEE_OLD_TRAN(t.MSGTYPE,'SVF_ACQ',t.Fee_Key, IFNULL(t.ACQUIRER_FE,t.ACQUIRER), IFNULL(t.ISSUER_FE,t.ISSUER), t.ACQ_CURRENCY_CODE, IF(t.ACQ_CURRENCY_CODE=418, t.SETTLEMENT_AMOUNT, t.AMOUNT), t.CARDHOLDER_CONV_RATE, t.TRACE),
//				      t.FEE_IRF_BEN = NAPAS_FEE_OLD_TRAN(t.MSGTYPE,'IRF_BEN',t.Fee_Key, IFNULL(t.ACQUIRER_FE,t.ACQUIRER), IFNULL(t.ISSUER_FE,t.ISSUER), t.ACQ_CURRENCY_CODE, IF(t.ACQ_CURRENCY_CODE=418, t.SETTLEMENT_AMOUNT, t.AMOUNT), t.CARDHOLDER_CONV_RATE, t.TRACE),
//				      t.FEE_SVF_BEN = NAPAS_FEE_OLD_TRAN(t.MSGTYPE,'SVF_BEN',t.Fee_Key, IFNULL(t.ACQUIRER_FE,t.ACQUIRER), IFNULL(t.ISSUER_FE,t.ISSUER), t.ACQ_CURRENCY_CODE, IF(t.ACQ_CURRENCY_CODE=418, t.SETTLEMENT_AMOUNT, t.AMOUNT), t.CARDHOLDER_CONV_RATE, t.TRACE),
//				      t.RE_FEE = 1
//				    WHERE t.msgtype=210 AND t.respcode IN (110,112,113,114,115);
//				""";
//		log.info("recomputeFeesStage2_OldRange: {}", sql);
//		jdbc.update(sql, new MapSqlParameterSource());
		String wherecommand = """
						WHERE msgtype = 210
				AND respcode IN (110,112,113,114,115)
				AND Fee_Key IN (
				    SELECT Fee_Key
				    FROM Gr_Fee_Config_New
				    WHERE DATE(Valid_To) <= STR_TO_DATE('20171001','%Y%m%d')
				)
						""";
		recomputeFeesStage3(wherecommand);
	}

	/**
	 * Block update 3
	 */
	private void recomputeFeesStage3_ByWhitelist() {
		// Block UPDATE #3: whitelist 2 Fee_Key trong khoảng settle_date
		// 2017-07-01..2017-10-01
//		String sql = """
//				    UPDATE SHCLOG_SETT_IBFT_ADJUST t
//				    SET
//				      t.FEE_IRF_ISS = NAPAS_FEE_OLD_TRAN(t.MSGTYPE,'IRF_ISS',t.Fee_Key, IFNULL(t.ACQUIRER_FE,t.ACQUIRER), IFNULL(t.ISSUER_FE,t.ISSUER), t.ACQ_CURRENCY_CODE, IF(t.ACQ_CURRENCY_CODE=418, t.SETTLEMENT_AMOUNT, t.AMOUNT), t.CARDHOLDER_CONV_RATE, t.TRACE),
//				      t.FEE_SVF_ISS = NAPAS_FEE_OLD_TRAN(t.MSGTYPE,'SVF_ISS',t.Fee_Key, IFNULL(t.ACQUIRER_FE,t.ACQUIRER), IFNULL(t.ISSUER_FE,t.ISSUER), t.ACQ_CURRENCY_CODE, IF(t.ACQ_CURRENCY_CODE=418, t.SETTLEMENT_AMOUNT, t.AMOUNT), t.CARDHOLDER_CONV_RATE, t.TRACE),
//				      t.FEE_IRF_ACQ = NAPAS_FEE_OLD_TRAN(t.MSGTYPE,'IRF_ACQ',t.Fee_Key, IFNULL(t.ACQUIRER_FE,t.ACQUIRER), IFNULL(t.ISSUER_FE,t.ISSUER), t.ACQ_CURRENCY_CODE, IF(t.ACQ_CURRENCY_CODE=418, t.SETTLEMENT_AMOUNT, t.AMOUNT), t.CARDHOLDER_CONV_RATE, t.TRACE),
//				      t.FEE_SVF_ACQ = NAPAS_FEE_OLD_TRAN(t.MSGTYPE,'SVF_ACQ',t.Fee_Key, IFNULL(t.ACQUIRER_FE,t.ACQUIRER), IFNULL(t.ISSUER_FE,t.ISSUER), t.ACQ_CURRENCY_CODE, IF(t.ACQ_CURRENCY_CODE=418, t.SETTLEMENT_AMOUNT, t.AMOUNT), t.CARDHOLDER_CONV_RATE, t.TRACE),
//				      t.FEE_IRF_BEN = NAPAS_FEE_OLD_TRAN(t.MSGTYPE,'IRF_BEN',t.Fee_Key, IFNULL(t.ACQUIRER_FE,t.ACQUIRER), IFNULL(t.ISSUER_FE,t.ISSUER), t.ACQ_CURRENCY_CODE, IF(t.ACQ_CURRENCY_CODE=418, t.SETTLEMENT_AMOUNT, t.AMOUNT), t.CARDHOLDER_CONV_RATE, t.TRACE),
//				      t.FEE_SVF_BEN = NAPAS_FEE_OLD_TRAN(t.MSGTYPE,'SVF_BEN',t.Fee_Key, IFNULL(t.ACQUIRER_FE,t.ACQUIRER), IFNULL(t.ISSUER_FE,t.ISSUER), t.ACQ_CURRENCY_CODE, IF(t.ACQ_CURRENCY_CODE=418, t.SETTLEMENT_AMOUNT, t.AMOUNT), t.CARDHOLDER_CONV_RATE, t.TRACE),
//				      t.RE_FEE = 1
//				    WHERE t.msgtype=210 AND t.respcode IN (110,112,113,114,115)
//				      AND t.Fee_Key IN ('C4J94BD3-E9K8-46EC-Q8A7-FE46J0E95P1E','LC01751F-0C65-4FA2-9T34-8H86C13CDTC0')
//				      AND DATE(t.SETTLEMENT_DATE) BETWEEN '2017-07-01' AND '2017-10-01';
//				""";\
		String wherecommand = """
				WHERE t.msgtype=210 AND t.respcode IN (110,112,113,114,115)
				      AND t.Fee_Key IN ('C4J94BD3-E9K8-46EC-Q8A7-FE46J0E95P1E','LC01751F-0C65-4FA2-9T34-8H86C13CDTC0')
				      AND DATE(t.SETTLEMENT_DATE) BETWEEN '2017-07-01' AND '2017-10-01'
				""";
		recomputeFeesStage2(wherecommand);
	}

	/**
	 * block update 4
	 * 
	 * @param thue
	 */
	private void recomputeFeesStage4_FillZeros(BigDecimal thue) {
		// Block UPDATE #4: scale thue & fill null -> 0

		String wherecommand = """
				where msgtype = 210
				and respcode In (110,112,113,114,115)
				And Fee_Key In
				(
				    Select Fee_Key
				    From Gr_Fee_Config_New
				    Where Date(Valid_To) <= STR_TO_DATE('20171001','yyyymmdd')
				)
				And FEE_SVF_ISS = 0
				And FEE_IRF_ISS = 0
				And FEE_IRF_ACQ = 0
				And FEE_SVF_ACQ = 0
				""";
		recomputeFeesStage3(wherecommand);
//		jdbc.update(sql, new MapSqlParameterSource("th", thue));
	}

	/**
	 * block update 5
	 */
	private int recomputeFeesStage5_PostMapFields(BigDecimal thue) {
	    final String sql = """
	        UPDATE SHCLOG_SETT_IBFT_ADJUST
	        SET
	          FEE_IRF_ISS = CASE
	                          WHEN FEE_IRF_ISS IS NULL THEN 0
	                          ELSE CASE
	                                 WHEN ACQ_CURRENCY_CODE IN (840,418) THEN ROUND(FEE_IRF_ISS * :thue, 2)
	                                 ELSE ROUND(FEE_IRF_ISS * :thue, 0)
	                               END
	                        END,
	          FEE_SVF_ISS = CASE
	                          WHEN FEE_SVF_ISS IS NULL THEN 0
	                          ELSE CASE
	                                 WHEN ACQ_CURRENCY_CODE IN (840,418) THEN ROUND(FEE_SVF_ISS * :thue, 2)
	                                 ELSE ROUND(FEE_SVF_ISS * :thue, 0)
	                               END
	                        END,
	          FEE_IRF_ACQ = CASE
	                          WHEN FEE_IRF_ACQ IS NULL THEN 0
	                          ELSE CASE
	                                 WHEN ACQ_CURRENCY_CODE IN (840,418) THEN ROUND(FEE_IRF_ACQ * :thue, 2)
	                                 ELSE ROUND(FEE_IRF_ACQ * :thue, 0)
	                               END
	                        END,
	          FEE_SVF_ACQ = CASE
	                          WHEN FEE_SVF_ACQ IS NULL THEN 0
	                          ELSE CASE
	                                 WHEN ACQ_CURRENCY_CODE IN (840,418) THEN ROUND(FEE_SVF_ACQ * :thue, 2)
	                                 ELSE ROUND(FEE_SVF_ACQ * :thue, 0)
	                               END
	                        END,
	          FEE_IRF_BEN = CASE
	                          WHEN FEE_IRF_BEN IS NULL THEN 0
	                          ELSE CASE
	                                 WHEN ACQ_CURRENCY_CODE IN (840,418) THEN ROUND(FEE_IRF_BEN * :thue, 2)
	                                 ELSE ROUND(FEE_IRF_BEN * :thue, 0)
	                               END
	                        END,
	          FEE_SVF_BEN = CASE
	                          WHEN FEE_SVF_BEN IS NULL THEN 0
	                          ELSE CASE
	                                 WHEN ACQ_CURRENCY_CODE IN (840,418) THEN ROUND(FEE_SVF_BEN * :thue, 2)
	                                 ELSE ROUND(FEE_SVF_BEN * :thue, 0)
	                               END
	                        END
	        WHERE IFNULL(RE_FEE, 0) = 1
	    """;

	    MapSqlParameterSource params = new MapSqlParameterSource()
	            .addValue("thue", thue); // BigDecimal

	    int rows = jdbc.update(sql, params);
	    return rows;
	    // optional: log rows
	}

	/**
	 * block update 6
	 */
	private void recomputeFeesStage6() {
		// Block UPDATE #5: map các trường IRF*/SVF* theo điều kiện resp/acq/iss (rút
		// gọn theo store)
		String sql = """
				UPDATE SHCLOG_SETT_IBFT_ADJUST
				SET FEE_SVF_ISS = FEE_ISS + FEE_IRF_ISS
				WHERE FEE_KEY IN (
				  'A300C4EB-E648-4F98-99FC-9C11BFACE6D6', /* 41 cũ */
				  '46206160-7462-46C0-81D0-6DA81C367958', /* 42 cũ ATM */
				  'A775AB95-CF99-411E-83CE-F42E5AB4F61B'  /* 48 cũ */
				);
								""";
		jdbc.update(sql, new MapSqlParameterSource());
	}

	/**
	 * block update 6.1
	 */
	private void recomputeFeesStage6_1() {
		// Block UPDATE #5: map các trường IRF*/SVF* theo điều kiện resp/acq/iss (rút
		// gọn theo store)
		String sql = """
								UPDATE SHCLOG_SETT_IBFT_ADJUST t
				SET
				  SVFISSNP  = IFNULL(t.FEE_SVF_ISS, 0),

				  IRFISSACQ = CASE
				                WHEN t.ACQUIRER_RP = 220699 OR t.ISSUER_RP = 220699
				                  THEN IFNULL(t.FEE_IRF_ACQ, 0)
				                ELSE
				                  CASE
				                    WHEN t.FEE_IRF_ACQ > 0 THEN IFNULL(t.FEE_IRF_ISS, 0)
				                    ELSE 0
				                  END
				              END,

				  IRFISSBNB = CASE
				                WHEN t.FEE_IRF_BEN > 0 THEN IFNULL(t.FEE_IRF_ISS, 0)
				                ELSE 0
				              END,

				  SVFACQNP  = IFNULL(t.FEE_SVF_ACQ, 0),

				  IRFACQISS = CASE
				                WHEN t.FEE_IRF_ISS > 0 THEN IFNULL(t.FEE_IRF_ACQ, 0)
				                ELSE 0
				              END,

				  IRFACQBNB = CASE
				                WHEN t.FEE_IRF_BEN > 0 THEN IFNULL(t.FEE_IRF_ACQ, 0)
				                ELSE 0
				              END,

				  SVFBNBNP  = IFNULL(t.FEE_SVF_BEN, 0),

				  IRFBNBISS = CASE
				                WHEN t.FEE_IRF_ISS > 0 THEN IFNULL(t.FEE_IRF_BEN, 0)
				                ELSE 0
				              END,

				  IRFBNBACQ = CASE
				                WHEN t.FEE_IRF_ACQ > 0 THEN IFNULL(t.FEE_IRF_BEN, 0)
				                ELSE 0
				              END
				WHERE t.RESPCODE IN (0, 110, 115, 113);""";
		jdbc.update(sql, new MapSqlParameterSource());
	}

	/**
	 * Block 7
	 */
	private void cleanupSpecialChars() {
		// Loại bỏ ký tự tab/CR/LF trong CONTENT_FUND
		String sql1 = "UPDATE SHCLOG_SETT_IBFT_ADJUST SET CONTENT_FUND = REPLACE(CONTENT_FUND, CHAR(9), '') WHERE CONTENT_FUND LIKE CONCAT('%',CHAR(9),'%') OR CONTENT_FUND LIKE CONCAT('%',CHAR(10),'%') OR CONTENT_FUND LIKE CONCAT('%',CHAR(13),'%')";
		String sql2 = "UPDATE SHCLOG_SETT_IBFT_ADJUST SET CONTENT_FUND = REPLACE(CONTENT_FUND, CHAR(10), '') WHERE CONTENT_FUND LIKE CONCAT('%',CHAR(9),'%') OR CONTENT_FUND LIKE CONCAT('%',CHAR(10),'%') OR CONTENT_FUND LIKE CONCAT('%',CHAR(13),'%')";
		String sql3 = "UPDATE SHCLOG_SETT_IBFT_ADJUST SET CONTENT_FUND = REPLACE(CONTENT_FUND, CHAR(13), '') WHERE CONTENT_FUND LIKE CONCAT('%',CHAR(9),'%') OR CONTENT_FUND LIKE CONCAT('%',CHAR(10),'%') OR CONTENT_FUND LIKE CONCAT('%',CHAR(13),'%')";
		jdbc.update(sql1, new MapSqlParameterSource());
		jdbc.update(sql2, new MapSqlParameterSource());
		jdbc.update(sql3, new MapSqlParameterSource());
	}

	/**
	 * Block 8 update pcode update Pcode_Orig cho gd C3-72 phuc vu sinh file BEN
	 */
	private void updatePcodeOrig() {
		// Cập nhật PCODE_ORIG theo 3 rule trong store
		String sqlA = """
				-- (1) IST|IBT + C3|72
				UPDATE SHCLOG_SETT_IBFT_ADJUST t
				SET Pcode_Orig = CONCAT(
				  '91',
				  /* createPcode(ACQUIRER, PAN) */
				  CASE
				    WHEN EXISTS (
				      SELECT 1
				      FROM PCODE72 p
				      WHERE TRIM(p.BANKID) = TRIM(t.ACQUIRER)
				        AND INSTR(p.PRANGE, LEFT(TRIM(t.PAN), 6)) > 0
				    ) THEN '00' ELSE '20'
				  END,
				  /* createPcode(BB_BIN, tail(ACCTNUM)) */
				  CASE
				    WHEN EXISTS (
				      SELECT 1
				      FROM PCODE72 p
				      WHERE TRIM(p.BANKID) = TRIM(CAST(t.BB_BIN AS CHAR))
				        AND INSTR(
				              p.PRANGE,
				              LEFT(
				                SUBSTRING_INDEX(CONCAT(t.ACCTNUM,'|'), '|', -1),  -- phần sau dấu '|', rỗng nếu không có
				                6
				              )
				            ) > 0
				    ) THEN '00' ELSE '20'
				  END
				)
				WHERE t.From_Sys = 'IST|IBT'
				  AND t.Tran_Case = 'C3|72';

								""";
		String sqlB = """
												-- (2) PCODE = 43 và thuộc nhóm BEN_72 = 1
								UPDATE SHCLOG_SETT_IBFT_ADJUST t
								SET Pcode_Orig = CONCAT(
								  '91',
								  CASE
								    WHEN EXISTS (
								      SELECT 1
								      FROM PCODE72 p
								      WHERE TRIM(p.BANKID) = TRIM(t.ACQUIRER)
								        AND INSTR(p.PRANGE, LEFT(TRIM(t.PAN), 6)) > 0
								    ) THEN '00' ELSE '20'
								  END,
								  CASE
								    WHEN EXISTS (
								      SELECT 1
								      FROM PCODE72 p
								      WHERE TRIM(p.BANKID) = TRIM(CAST(t.BB_BIN AS CHAR))
								        AND INSTR(
								              p.PRANGE,
								              LEFT(SUBSTRING_INDEX(CONCAT(t.ACCTNUM,'|'), '|', -1), 6)
								            ) > 0
								    ) THEN '00' ELSE '20'
								  END
								)
								WHERE t.Pcode = '43'
								  AND t.BB_BIN IN (
								        SELECT CAST(r.SHCLOG_ID AS UNSIGNED)
								        FROM RP_INSTITUTION r
								        WHERE IFNULL(r.BEN_72, 0) = 1
								          AND r.SHCLOG_ID REGEXP '^[0-9]+$'
								    );
				""";
		String sqlC = """
								-- (3) PCODE = 43 và KHÔNG thuộc nhóm BEN_72 = 1
				UPDATE SHCLOG_SETT_IBFT_ADJUST t
				SET Pcode_Orig = '420000'
				WHERE t.Pcode = '43'
				  AND t.BB_BIN NOT IN (
				        SELECT CAST(r.SHCLOG_ID AS UNSIGNED)
				        FROM RP_INSTITUTION r
				        WHERE IFNULL(r.BEN_72, 0) = 1
				          AND r.SHCLOG_ID REGEXP '^[0-9]+$'
				    );

								""";
		jdbc.update(sqlA, new MapSqlParameterSource());
		jdbc.update(sqlB, new MapSqlParameterSource());
		jdbc.update(sqlC, new MapSqlParameterSource());
	}

	/**
	 * block 9
	 */
	private void zeroFeesForPartialReversals() {
		String sql = """
				    UPDATE SHCLOG_SETT_IBFT_ADJUST
				    SET FEE_IRF_ISS=0, FEE_SVF_ISS=0, FEE_IRF_BEN=0, FEE_SVF_BEN=0,
				        SVFISSNP=0, IRFISSACQ=0, IRFISSBNB=0, SVFACQNP=0, IRFACQISS=0, IRFACQBNB=0, SVFBNBNP=0, IRFBNBISS=0, IRFBNBACQ=0
				    WHERE PCODE IN (42,91) AND IFNULL(PCODE2,0) <> 890000 AND RESPCODE IN (112,113,114,115);
				""";
		jdbc.update(sql, new MapSqlParameterSource());
	}

	private void insertLogShcTmp(LocalDateTime day1, LocalDateTime day2, String createdUser, int settCode) {
		// TiDB không có sequence Getkhoacuabang -> dùng func tương đương hoặc
		// AUTO_INCREMENT
		// Ở đây giả định Getkhoacuabang tồn tại trên DB TiDB như 1 function trả về số.
		String sql = """
				    INSERT INTO RP_LOG_SHC_TMP(TUTG,DENTG,TGDODL,CREATED_USER,SETT_CODE)
				    VALUES (:tu, :den, NOW(), :u, CASE WHEN :sc=900 THEN 7041 WHEN :sc=901 THEN 8401 ELSE :sc END)
				""";
		MapSqlParameterSource ps = new MapSqlParameterSource().addValue("tu", day1.format(DDMMYYYY_HHMMSS))
				.addValue("den", day2.format(DDMMYYYY_HHMMSS)).addValue("u", createdUser).addValue("sc", settCode);
		log.info("Sql: {}", SqlLogUtils.renderSql(sql, ps.getValues()));
		jdbc.update(sql, ps);
	}

	private void callDataToEcom(String from, String to) {
		// Nếu đã dựng procedure DATA_TO_ECOM trên TiDB (MySQL mode), có thể gọi bằng
		// CALL
		try {

			String call = "CALL DATA_TO_ECOM(:f,:t)";
			proc_DATA_TO_ECOM.run(from, to);
//			jdbc.update(call, new MapSqlParameterSource().addValue("f", from).addValue("t", to));
		} catch (Exception ex) {
			log.warn("DATA_TO_ECOM not executed: {}", ex.getMessage());
		}
	}
}
