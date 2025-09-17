package vn.napas.webrp.database.repo.sql;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ZenFeeValueIbftBatch {

	private final NamedParameterJdbcTemplate jdbc;

	// Chỉ 1 câu lệnh duy nhất (Oracle -> TiDB/MySQL)
	private static final String SQL_INSERT_ACQUIRER = """
			INSERT INTO ZEN_FEE_VALUE_IBFT (ZEN_TYPE, ZEN_VALUE)
			SELECT
			  'ACQUIRER' AS ZEN_TYPE,
			  IFNULL(ACQUIRER_FE, ACQUIRER) AS ZEN_VALUE
			FROM SHCLOG_SETT_IBFT
			WHERE DATE(SETTLEMENT_DATE) = :settlementDate
			  AND msgtype = 210
			  AND respcode = 0
			  AND isrev IS NULL
			  AND (
			      (
			        (
			          (SUBSTR(TRIM(LPAD(CAST(PCODE AS CHAR), 6, '0')), 1, 2) IN ('00','01','30','35','40','41','42','43','48','20')
			           AND IFNULL(MSGTYPE_DETAIL, 'NA') NOT IN ('VPREC')
			          )
			          OR
			          (SUBSTR(TRIM(LPAD(CAST(PCODE AS CHAR), 6, '0')), 1, 2) = '94' AND MERCHANT_TYPE = 6011)
			        )
			        AND IFNULL(FROM_SYS, 'IST') LIKE '%IST%'
			      )
			      OR
			      (
			        FROM_SYS IS NOT NULL
			        AND SUBSTR(TRIM(LPAD(CAST(PCODE AS CHAR), 6, '0')), 1, 2) IN ('01','42','91')
			      )
			  )
			GROUP BY IFNULL(ACQUIRER_FE, ACQUIRER)
			""";

	/**
	 * Chạy đúng một câu INSERT…SELECT theo logic gốc 22.stt_100100400210.sql.
	 * 
	 * @param settlementDate ngày filter theo cột SETTLEMENT_DATE
	 * @return số dòng được chèn
	 */
	@Transactional
	public int insertAcquirer(LocalDate settlementDate) {
		long t0 = System.currentTimeMillis();
		try {
			MapSqlParameterSource p = new MapSqlParameterSource().addValue("settlementDate",
					Date.valueOf(settlementDate)); // dùng DATE() ở SQL
			int rows = jdbc.update(SQL_INSERT_ACQUIRER, p);
			log.info("insertAcquirer(date={}): {} rows in {} ms", settlementDate, rows,
					System.currentTimeMillis() - t0);
			return rows;
		} catch (DataAccessException e) {
			log.error("insertAcquirer(date={}) failed: {}", settlementDate, e.getMessage(), e);
			throw e;
		}
	}

	// --- ISSUER: chỉ 1 câu lệnh duy nhất ---
	private static final String SQL_INSERT_ISSUER = """
			INSERT INTO ZEN_FEE_VALUE_IBFT (ZEN_TYPE, ZEN_VALUE)
			SELECT
			  'ISSUER' AS ZEN_TYPE,
			  IFNULL(ISSUER_FE, ISSUER) AS ZEN_VALUE
			FROM SHCLOG_SETT_IBFT
			WHERE DATE(SETTLEMENT_DATE) = :settlementDate
			  AND msgtype = 210
			  AND respcode = 0
			  AND isrev IS NULL
			  AND (
			      (
			        (
			          (SUBSTR(TRIM(LPAD(CAST(PCODE AS CHAR), 6, '0')), 1, 2) IN ('00','01','30','35','40','41','42','43','48','20')
			           AND IFNULL(MSGTYPE_DETAIL, 'NA') NOT IN ('VPREC')
			          )
			          OR
			          (SUBSTR(TRIM(LPAD(CAST(PCODE AS CHAR), 6, '0')), 1, 2) = '94' AND MERCHANT_TYPE = 6011)
			        )
			        AND IFNULL(FROM_SYS, 'IST') LIKE '%IST%'
			      )
			      OR
			      (
			        FROM_SYS IS NOT NULL
			        AND SUBSTR(TRIM(LPAD(CAST(PCODE AS CHAR), 6, '0')), 1, 2) IN ('01','42','91')
			      )
			  )
			GROUP BY IFNULL(ISSUER_FE, ISSUER)
			""";

	/**
	 * Chạy đúng một câu INSERT…SELECT theo logic gốc 23.stt_100100400215.sql.
	 * 
	 * @param settlementDate ngày filter theo cột SETTLEMENT_DATE
	 * @return số dòng được chèn
	 */
	@Transactional
	public int insertIssuer(LocalDate settlementDate) {
		long t0 = System.currentTimeMillis();
		try {
			MapSqlParameterSource p = new MapSqlParameterSource().addValue("settlementDate",
					java.sql.Date.valueOf(settlementDate));
			int rows = jdbc.update(SQL_INSERT_ISSUER, p);
			log.info("insertIssuer(date={}): {} rows in {} ms", settlementDate, rows, System.currentTimeMillis() - t0);
			return rows;
		} catch (DataAccessException e) {
			log.error("insertIssuer(date={}) failed: {}", settlementDate, e.getMessage(), e);
			throw e;
		}
	}

	// === MERCHANT_TYPE: 1 câu lệnh duy nhất (Oracle -> TiDB/MySQL) ===
	private static final String SQL_INSERT_MERCHANT_TYPE = """
			INSERT INTO ZEN_FEE_VALUE_IBFT (ZEN_TYPE, ZEN_VALUE)
			SELECT
			  'MERCHANT_TYPE' AS ZEN_TYPE,
			  CASE
			    WHEN TRAN_CASE = '72|C3' THEN 6011
			    WHEN Pcode2 IN (890000,880000,720000,730000) THEN 0
			    WHEN Pcode2 IN (840000,968400,978400,988400,998400) THEN Merchant_type
			    WHEN SUBSTR(TRIM(LPAD(CAST(PCODE2 AS CHAR), 6, '0')), 1, 2) IN ('98','99')
			         AND SUBSTR(TRIM(LPAD(CAST(PCODE  AS CHAR), 6, '0')), 1, 2) = '30'
			         AND Merchant_type = 6012
			      THEN Merchant_type
			    WHEN Pcode2 IN (960000,970000,980000,990000,968500,978500,988500,998500,967700,977700,987700,997700,
			                    967500,977500,987500,997500,967600,977600,987600,997600,
			                    967800,977800,987800,997800,967900,977900,987900,997900,
			                    966100,976100,986100,996100,966200,976200,986200,996200)
			         AND SUBSTR(TRIM(LPAD(CAST(PCODE AS CHAR), 6, '0')), 1, 2) IN ('00','20')
			         AND MERCHANT_TYPE_ORIG IN (
			               SELECT MERCHANT_TYPE
			               FROM GR_FEE_CONFIG_NEW
			               WHERE NOW() BETWEEN VALID_FROM AND VALID_TO
			                 AND SUBSTR(PRO_CODE, 3, 2) = SUBSTR(TRIM(LPAD(CAST(PCODE2 AS CHAR), 6, '0')), 1, 2)
			         )
			      THEN MERCHANT_TYPE_ORIG
			    WHEN SUBSTR(CAST(IFNULL(PCODE2,0) AS CHAR), 1, 2) NOT IN ('96','97','98','99')
			         AND SUBSTR(TRIM(LPAD(CAST(PCODE AS CHAR), 6, '0')), 1, 2) IN ('00','20')
			         AND MERCHANT_TYPE_ORIG IN (4111, 4131, 5172, 9211, 9222, 9223, 9311, 9399, 8398, 7523, 7524)
			      THEN MERCHANT_TYPE_ORIG
			    ELSE Merchant_type
			  END AS ZEN_VALUE
			FROM SHCLOG_SETT_IBFT
			WHERE DATE(SETTLEMENT_DATE) = :settlementDate
			  AND msgtype = 210
			  AND respcode = 0
			  AND isrev IS NULL
			  AND (
			       (
			         (
			           (SUBSTR(TRIM(LPAD(CAST(PCODE AS CHAR), 6, '0')), 1, 2) IN ('00','01','30','35','40','41','42','43','48','20','03')
			            AND IFNULL(MSGTYPE_DETAIL, 'NA') NOT IN ('VPREC')
			           )
			           OR
			           (SUBSTR(TRIM(LPAD(CAST(PCODE AS CHAR), 6, '0')), 1, 2) = '94' AND MERCHANT_TYPE = 6011)
			         )
			         AND IFNULL(FROM_SYS, 'IST') LIKE '%IST%'
			       )
			       OR
			       (
			         FROM_SYS IS NOT NULL
			         AND SUBSTR(TRIM(LPAD(CAST(PCODE AS CHAR), 6, '0')), 1, 2) IN ('01','42','91')
			       )
			  )
			GROUP BY
			  CASE
			    WHEN TRAN_CASE = '72|C3' THEN 6011
			    WHEN Pcode2 IN (890000,880000,720000,730000) THEN 0
			    WHEN Pcode2 IN (840000,968400,978400,988400,998400) THEN Merchant_type
			    WHEN SUBSTR(TRIM(LPAD(CAST(PCODE2 AS CHAR), 6, '0')), 1, 2) IN ('98','99')
			         AND SUBSTR(TRIM(LPAD(CAST(PCODE  AS CHAR), 6, '0')), 1, 2) = '30'
			         AND Merchant_type = 6012
			      THEN Merchant_type
			    WHEN Pcode2 IN (960000,970000,980000,990000,968500,978500,988500,998500,967700,977700,987700,997700,
			                    967500,977500,987500,997500,967600,977600,987600,997600,
			                    967800,977800,987800,997800,967900,977900,987900,997900,
			                    966100,976100,986100,996100,966200,976200,986200,996200)
			         AND SUBSTR(TRIM(LPAD(CAST(PCODE AS CHAR), 6, '0')), 1, 2) IN ('00','20')
			         AND MERCHANT_TYPE_ORIG IN (
			               SELECT MERCHANT_TYPE
			               FROM GR_FEE_CONFIG_NEW
			               WHERE NOW() BETWEEN VALID_FROM AND VALID_TO
			                 AND SUBSTR(PRO_CODE, 3, 2) = SUBSTR(TRIM(LPAD(CAST(PCODE2 AS CHAR), 6, '0')), 1, 2)
			         )
			      THEN MERCHANT_TYPE_ORIG
			    WHEN SUBSTR(CAST(IFNULL(PCODE2,0) AS CHAR), 1, 2) NOT IN ('96','97','98','99')
			         AND SUBSTR(TRIM(LPAD(CAST(PCODE AS CHAR), 6, '0')), 1, 2) IN ('00','20')
			         AND MERCHANT_TYPE_ORIG IN (4111, 4131, 5172, 9211, 9222, 9223, 9311, 9399, 8398, 7523, 7524)
			      THEN MERCHANT_TYPE_ORIG
			    ELSE Merchant_type
			  END
			""";

	/**
	 * Chèn ZEN_TYPE='MERCHANT_TYPE' theo đúng logic gốc cho 1 ngày settlement.
	 * 25.stt_100100400225.sql
	 */
	@Transactional
	public int insertMerchantType(LocalDate settlementDate) {
		long t0 = System.currentTimeMillis();
		try {
			MapSqlParameterSource p = new MapSqlParameterSource().addValue("settlementDate",
					Date.valueOf(settlementDate));
			int rows = jdbc.update(SQL_INSERT_MERCHANT_TYPE, p);
			log.info("insertMerchantType(date={}): {} rows in {} ms", settlementDate, rows,
					System.currentTimeMillis() - t0);
			return rows;
		} catch (DataAccessException e) {
			log.error("insertMerchantType(date={}) failed: {}", settlementDate, e.getMessage(), e);
			throw e;
		}
	}

	// --- CURRENCY_CODE: 1 câu lệnh duy nhất (Oracle -> TiDB/MySQL) ---
	private static final String SQL_INSERT_CURRENCY_CODE = """
			INSERT INTO ZEN_FEE_VALUE_IBFT (ZEN_TYPE, ZEN_VALUE)
			SELECT
			  'CURRENCY_CODE' AS ZEN_TYPE,
			  CASE
			    WHEN ACQ_CURRENCY_CODE IS NULL THEN 704
			    WHEN ACQ_CURRENCY_CODE = 840  THEN 840
			    ELSE 704
			  END AS ZEN_VALUE
			FROM SHCLOG_SETT_IBFT
			WHERE DATE(SETTLEMENT_DATE) = :settlementDate
			  AND msgtype = 210
			  AND respcode = 0
			  AND isrev IS NULL
			  AND (
			       (
			         (
			           (SUBSTR(TRIM(LPAD(CAST(PCODE AS CHAR), 6, '0')), 1, 2)
			              IN ('00','01','30','35','40','41','42','43','48','20')
			            AND IFNULL(MSGTYPE_DETAIL, 'NA') NOT IN ('VPREC')
			           )
			           OR
			           (SUBSTR(TRIM(LPAD(CAST(PCODE AS CHAR), 6, '0')), 1, 2) = '94' AND MERCHANT_TYPE = 6011)
			         )
			         AND IFNULL(FROM_SYS, 'IST') LIKE '%IST%'
			       )
			       OR
			       (
			         FROM_SYS IS NOT NULL
			         AND SUBSTR(TRIM(LPAD(CAST(PCODE AS CHAR), 6, '0')), 1, 2) IN ('01','42','91')
			       )
			  )
			GROUP BY
			  CASE
			    WHEN ACQ_CURRENCY_CODE IS NULL THEN 704
			    WHEN ACQ_CURRENCY_CODE = 840  THEN 840
			    ELSE 704
			  END
			""";

	/**
	 * Chèn ZEN_TYPE='CURRENCY_CODE' theo logic gốc cho 1 ngày settlement
	 * 26.stt_100100400230.sql.
	 */
	@Transactional
	public int insertCurrencyCode(LocalDate settlementDate) {
		long t0 = System.currentTimeMillis();
		try {
			MapSqlParameterSource p = new MapSqlParameterSource().addValue("settlementDate",
					Date.valueOf(settlementDate));
			int rows = jdbc.update(SQL_INSERT_CURRENCY_CODE, p);
			log.info("insertCurrencyCode(date={}): {} rows in {} ms", settlementDate, rows,
					System.currentTimeMillis() - t0);
			return rows;
		} catch (DataAccessException e) {
			log.error("insertCurrencyCode(date={}) failed: {}", settlementDate, e.getMessage(), e);
			throw e;
		}
	}

	private static final String insertConfigFeeIbftSql = """
			INSERT INTO ZEN_CONFIG_FEE_IBFT (FEE_KEY, FEE_VALUE, FEE_ORDER, FEE_DATE)
			SELECT FEE_KEY, FEE_VALUE, FEE_ORDER, NOW()
			FROM (
			    SELECT
			        FEE_KEY,
			        FEE_VALUE,
			        RANK() OVER (PARTITION BY FEE_VALUE ORDER BY FEE_ORDER) AS FEE_ORDER
			    FROM (
			        SELECT
			            A.FEE_KEY,
			            CONCAT(B.ZEN_VALUE, C.ZEN_VALUE, D.ZEN_VALUE, E.ZEN_VALUE, F.ZEN_VALUE) AS FEE_VALUE,
			            CASE
			                WHEN A.ACQUIRER = B.ZEN_VALUE AND A.ISSUER = C.ZEN_VALUE THEN 1
			                WHEN A.ISSUER = C.ZEN_VALUE AND A.ACQUIRER = 0       THEN 3
			                WHEN A.ISSUER = 0        AND A.ACQUIRER = B.ZEN_VALUE THEN 3
			                WHEN A.ISSUER = C.ZEN_VALUE AND A.ACQUIRER <> B.ZEN_VALUE THEN 5 + A.ORDER_CONFIG
			                WHEN A.ISSUER <> C.ZEN_VALUE AND A.ACQUIRER = B.ZEN_VALUE THEN 5 + A.ORDER_CONFIG
			                WHEN A.ISSUER <> C.ZEN_VALUE AND A.ACQUIRER <> B.ZEN_VALUE THEN 10
			            END AS FEE_ORDER
			        FROM (
			            SELECT FEE_KEY, ISSUER, ACQUIRER, PRO_CODE, CURRENCY_CODE, MERCHANT_TYPE, ORDER_CONFIG
			            FROM Gr_Fee_Config_New
			            WHERE :p_date BETWEEN VALID_FROM AND VALID_TO
			        ) A
			        INNER JOIN (SELECT * FROM ZEN_FEE_VALUE_IBFT WHERE ZEN_TYPE = 'ACQUIRER') B
			            ON (IF(A.ACQUIRER = 0, B.ZEN_VALUE, A.ACQUIRER) = B.ZEN_VALUE)
			        INNER JOIN (SELECT * FROM ZEN_FEE_VALUE_IBFT WHERE ZEN_TYPE = 'ISSUER') C
			            ON (IF(A.ISSUER = 0, C.ZEN_VALUE, A.ISSUER) = C.ZEN_VALUE)
			        INNER JOIN (SELECT * FROM ZEN_FEE_VALUE_IBFT WHERE ZEN_TYPE = 'CURRENCY_CODE') D
			            ON (A.CURRENCY_CODE = D.ZEN_VALUE)
			        INNER JOIN (SELECT * FROM ZEN_FEE_VALUE_IBFT WHERE ZEN_TYPE = 'MERCHANT_TYPE') E
			            ON (IF(A.MERCHANT_TYPE = 0, E.ZEN_VALUE, A.MERCHANT_TYPE) = E.ZEN_VALUE)
			        INNER JOIN (SELECT * FROM ZEN_FEE_VALUE_IBFT WHERE ZEN_TYPE = 'PCODE') F
			            ON (A.PRO_CODE = F.ZEN_VALUE)
			    ) X
			) Y
			WHERE FEE_ORDER = :p_fee_order
			""";

	public int insertConfigFeeIbft(LocalDate processDate, int feeOrder) {
		MapSqlParameterSource params = new MapSqlParameterSource().addValue("p_date", processDate) // LocalDate sẽ tự
																									// map sang DATE
				.addValue("p_fee_order", feeOrder);

		return jdbc.update(insertConfigFeeIbftSql, params);
	}

}
