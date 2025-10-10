package vn.napas.webrp.database.repo.store;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.napas.webrp.report.util.SqlLogUtils;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Chuyển MERGE Oracle -> 3 bước cho TiDB/MySQL:
 *   step0: chuẩn bị (UNIQUE KEY + VIEW nguồn)
 *   step1: UPDATE các bản ghi đã tồn tại (match)
 *   step2: INSERT các bản ghi chưa tồn tại (NOT EXISTS) + dedup
 *   step3: UPDATE STT ổn định
 *
 * Ghi log vào ERR_EX trước/sau mỗi bước.
 */
/**
 * MERGE_SHC_SETT_IBFT_200
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class Proc_MERGE_SHC_SETT_IBFT_200 {

	private static final String MODULE = "MERGE_SHC_SETT_IBFT_200";
	private final NamedParameterJdbcTemplate jdbc;
	private final JdbcTemplate jdbcTemplate;

	/*
	 * ========================= 1) CÁC CÂU LỆNH SQL DẠNG HẰNG
	 * =========================
	 */

	// step0.1 – UNIQUE KEY (bắt lỗi trùng tên thì bỏ qua)
	private static final String SQL_STEP0_UNIQUE_KEY = """
			ALTER TABLE SHCLOG_SETT_IBFT
			ADD UNIQUE KEY uk_ibft_merge (PAN, ORIGTRACE, TERMID, LOCAL_DATE, LOCAL_TIME, ACQUIRER)
			""";

	// step0.2 – VIEW nguồn chung (lọc & chuẩn hoá)
	private static final String SQL_STEP0_SOURCE_VIEW = """
			//        CREATE OR REPLACE VIEW V_IBFT_SRC_0200 AS
			//        SELECT
			//          TRIM(B.CARD_NO)                  AS card_no_trim,
			//          CAST(B.TRACE_NO AS UNSIGNED)     AS trace_no_num,
			//          B.TERM_ID                        AS term_id,
			//          LPAD(B.LOCAL_DATE,4,'0')         AS local_mmdd,
			//          CAST(B.LOCAL_TIME AS UNSIGNED)   AS local_time_num,
			//          CAST(B.ACQ_ID   AS UNSIGNED)     AS acq_id_num,
			//
			//          B.MTI, B.BEN_ID, B.SERVICE_CODE, B.ISS_ID, B.PROC_CODE, B.IBFT_INFO,
			//          B.ACQ_COUNTRY, B.POS_ENTRY_CODE, B.POS_CONDITION_CODE, B.ADDRESPONSE, B.MVV,
			//          B.F4, B.F5, B.F6, B.F49,
			//          B.SETTLEMENT_CODE, B.SETTLEMENT_RATE, B.ISS_CONV_RATE, B.TCC,
			//          B.REF_NO, B.TNX_STAMP, B.CARD_ACCEPT_NAME_LOCATION, B.CARD_ACCEPT_ID_CODE,
			//          B.SETTLE_DATE, B.ACCOUNT_NO, B.DEST_ACCOUNT, B.MCC, B.APPROVAL_CODE, B.ADD_INFO,
			//
			//          CAST(B.AMOUNT AS DECIMAL(20,0))                                   AS amount_raw_num,
			//          CAST(SUBSTRING(B.AMOUNT,1,GREATEST(CHAR_LENGTH(B.AMOUNT)-2,0)) AS UNSIGNED) AS amount_ins_num,
			//          CAST(SUBSTRING(B.F4,1,GREATEST(CHAR_LENGTH(B.F4)-2,0)) AS UNSIGNED) AS f4_num,
			//          CAST(SUBSTRING(B.F5,1,GREATEST(CHAR_LENGTH(B.F5)-2,0)) AS UNSIGNED) AS f5_num,
			//          CAST(SUBSTRING(B.F6,1,GREATEST(CHAR_LENGTH(B.F6)-2,0)) AS UNSIGNED) AS f6_num,
			//          CAST(CONCAT('2', B.TRACE_NO) AS UNSIGNED)                         AS trace2_num
			//        FROM ISOMESSAGE_TMP_TURN B
			//        WHERE B.CARD_NO IS NOT NULL
			//          AND B.MTI = '0200'
			//          AND B.BEN_ID   REGEXP '^[0-9]+$'
			//          AND B.TRACE_NO REGEXP '^[0-9]+$'
			//        """;

	// step 3 merge thay the bang insert on dupplicate
	private final String SQL_STEP_3 = """
			INSERT INTO SHCLOG_SETT_IBFT (
			    DATA_ID, PPCODE, MSGTYPE, PAN, PCODE, Amount, ACQ_CURRENCY_CODE, TRACE, LOCAL_TIME, LOCAL_DATE, SETTLEMENT_DATE,
			    ACQUIRER, ISSUER, RESPCODE, MERCHANT_TYPE, MERCHANT_TYPE_ORIG, AUTHNUM, SETT_CURRENCY_CODE, TERMID, ADD_INFO, ACCTNUM,
			    ISS_CURRENCY_CODE, ORIGTRACE, ORIGISS, ORIGRESPCODE, CH_CURRENCY_CODE, ACQUIRER_FE, ACQUIRER_RP, ISSUER_FE,
			    ISSUER_RP, PCODE2, FROM_SYS, BB_BIN, BB_BIN_ORIG, CONTENT_FUND, TXNSRC, ACQ_COUNTRY, POS_ENTRY_CODE, POS_CONDITION_CODE,
			    ADDRESPONSE, MVV, F4, F5, F6, F49, SETTLEMENT_CODE, SETTLEMENT_RATE, ISS_CONV_RATE, TCC,
			    refnum, trandate, trantime, ACCEPTORNAME, TERMLOC, F15, pcode_orig, ACCOUNT_NO, DEST_ACCOUNT
			)
			SELECT
			    1 AS DATA_ID,
			    CASE WHEN B.PROC_CODE REGEXP '^[0-9]+$' THEN CAST(B.PROC_CODE AS SIGNED) ELSE NULL END AS PPCODE,
			    '210' AS MSGTYPE,
			    B.CARD_NO AS PAN,
			    CASE WHEN B.PROC_CODE REGEXP '^[0-9]+$' THEN CAST(B.PROC_CODE AS SIGNED) ELSE NULL END AS PCODE,
			    CASE WHEN B.AMOUNT REGEXP '^[0-9]+$' THEN CAST(COALESCE(B.AMOUNT, '0') AS SIGNED) ELSE NULL END AS Amount,
			    704 AS ACQ_CURRENCY_CODE,
			    CASE WHEN B.trace2_num REGEXP '^[0-9]+$' THEN CAST(B.trace2_num AS SIGNED) ELSE NULL END AS TRACE,
			    CASE WHEN B.LOCAL_TIME REGEXP '^[0-9]+$' THEN CAST(B.LOCAL_TIME AS SIGNED) ELSE NULL END AS LOCAL_TIME,
			    CASE
			        WHEN (
			            CASE
			                WHEN B.LOCAL_DATE = '0229' THEN
			                    IFNULL(
			                        STR_TO_DATE(
			                            CONCAT(
			                                (ROUND(FLOOR(YEAR(CURDATE()) / 4)) * 4),
			                                B.LOCAL_DATE
			                            ),
			                            '%Y%m%d'
			                        ),
			                        STR_TO_DATE(
			                            CONCAT(
			                                (ROUND(FLOOR((YEAR(CURDATE()) - 1) / 4)) * 4),
			                                B.LOCAL_DATE
			                            ),
			                            '%Y%m%d'
			                        )
			                    )
			                ELSE
			                    IFNULL(
			                        STR_TO_DATE(
			                            CONCAT(YEAR(CURDATE()), B.LOCAL_DATE),
			                            '%Y%m%d'
			                        ),
			                        STR_TO_DATE(
			                            CONCAT(YEAR(DATE_SUB(CURDATE(), INTERVAL 1 YEAR)), B.LOCAL_DATE),
			                            '%Y%m%d'
			                        )
			                    )
			            END
			        ) > CURDATE() THEN
			            CASE
			                WHEN B.LOCAL_DATE = '0229' THEN
			                    IFNULL(
			                        STR_TO_DATE(
			                            CONCAT(
			                                (ROUND(FLOOR((YEAR(CURDATE()) - 1) / 4)) * 4),
			                                B.LOCAL_DATE
			                            ),
			                            '%Y%m%d'
			                        ),
			                        NULL
			                    )
			                ELSE
			                    IFNULL(
			                        STR_TO_DATE(
			                            CONCAT(YEAR(DATE_SUB(CURDATE(), INTERVAL 1 YEAR)), B.LOCAL_DATE),
			                            '%Y%m%d'
			                        ),
			                        NULL
			                    )
			            END
			        ELSE
			            CASE
			                WHEN B.LOCAL_DATE = '0229' THEN
			                    IFNULL(
			                        STR_TO_DATE(
			                            CONCAT(
			                                (ROUND(FLOOR(YEAR(CURDATE()) / 4)) * 4),
			                                B.LOCAL_DATE
			                            ),
			                            '%Y%m%d'
			                        ),
			                        STR_TO_DATE(
			                            CONCAT(
			                                (ROUND(FLOOR((YEAR(CURDATE()) - 1) / 4)) * 4),
			                                B.LOCAL_DATE
			                            ),
			                            '%Y%m%d'
			                        )
			                    )
			                ELSE
			                    IFNULL(
			                        STR_TO_DATE(
			                            CONCAT(YEAR(CURDATE()), B.LOCAL_DATE),
			                            '%Y%m%d'
			                        ),
			                        STR_TO_DATE(
			                            CONCAT(YEAR(DATE_SUB(CURDATE(), INTERVAL 1 YEAR)), B.LOCAL_DATE),
			                            '%Y%m%d'
			                        )
			                    )
			            END
			    END AS LOCAL_DATE,
			    CASE
			        WHEN (
			            CASE
			                WHEN B.SETTLE_DATE = '0229' THEN
			                    IFNULL(
			                        STR_TO_DATE(
			                            CONCAT(
			                                (ROUND(FLOOR(YEAR(CURDATE()) / 4)) * 4),
			                                B.SETTLE_DATE
			                            ),
			                            '%Y%m%d'
			                        ),
			                        STR_TO_DATE(
			                            CONCAT(
			                                (ROUND(FLOOR((YEAR(CURDATE()) - 1) / 4)) * 4),
			                                B.SETTLE_DATE
			                            ),
			                            '%Y%m%d'
			                        )
			                    )
			                ELSE
			                    IFNULL(
			                        STR_TO_DATE(
			                            CONCAT(YEAR(CURDATE()), B.SETTLE_DATE),
			                            '%Y%m%d'
			                        ),
			                        STR_TO_DATE(
			                            CONCAT(YEAR(DATE_SUB(CURDATE(), INTERVAL 1 YEAR)), B.SETTLE_DATE),
			                            '%Y%m%d'
			                        )
			                    )
			            END
			        ) > CURDATE() THEN
			            CASE
			                WHEN B.SETTLE_DATE = '0229' THEN
			                    IFNULL(
			                        STR_TO_DATE(
			                            CONCAT(
			                                (ROUND(FLOOR((YEAR(CURDATE()) - 1) / 4)) * 4),
			                                B.SETTLE_DATE
			                            ),
			                            '%Y%m%d'
			                        ),
			                        NULL
			                    )
			                ELSE
			                    IFNULL(
			                        STR_TO_DATE(
			                            CONCAT(YEAR(DATE_SUB(CURDATE(), INTERVAL 1 YEAR)), B.SETTLE_DATE),
			                            '%Y%m%d'
			                        ),
			                        NULL
			                    )
			            END
			        ELSE
			            CASE
			                WHEN B.SETTLE_DATE = '0229' THEN
			                    IFNULL(
			                        STR_TO_DATE(
			                            CONCAT(
			                                (ROUND(FLOOR(YEAR(CURDATE()) / 4)) * 4),
			                                B.SETTLE_DATE
			                            ),
			                            '%Y%m%d'
			                        ),
			                        STR_TO_DATE(
			                            CONCAT(
			                                (ROUND(FLOOR((YEAR(CURDATE()) - 1) / 4)) * 4),
			                                B.SETTLE_DATE
			                            ),
			                            '%Y%m%d'
			                        )
			                    )
			                ELSE
			                    IFNULL(
			                        STR_TO_DATE(
			                            CONCAT(YEAR(CURDATE()), B.SETTLE_DATE),
			                            '%Y%m%d'
			                        ),
			                        STR_TO_DATE(
			                            CONCAT(YEAR(DATE_SUB(CURDATE(), INTERVAL 1 YEAR)), B.SETTLE_DATE),
			                            '%Y%m%d'
			                        )
			                    )
			            END
			    END AS SETTLEMENT_DATE,
			    CASE WHEN B.ACQ_ID REGEXP '^[0-9]+$' THEN CAST(B.ACQ_ID AS SIGNED) ELSE NULL END AS ACQUIRER,
			    CASE WHEN B.ACQ_ID REGEXP '^[0-9]+$' THEN CAST(B.ACQ_ID AS SIGNED) ELSE NULL END AS ISSUER,
			    CASE
			        WHEN B.BEN_ID = '971133' AND B.DEST_ACCOUNT LIKE 'NPDC%' THEN 68
			        WHEN B.SERVICE_CODE = 'QR_PUSH' THEN 68
			        WHEN B.BEN_ID = '971100' AND B.TCC = '99' THEN 68
			        WHEN TRIM(B.ISS_ID) IN ('980471', '980472') THEN 68
			        ELSE 0
			    END AS RESPCODE,
			    6011 AS MERCHANT_TYPE,
			    CASE WHEN B.MCC REGEXP '^[0-9]+$' THEN CAST(B.MCC AS SIGNED) ELSE NULL END AS MERCHANT_TYPE_ORIG,
			    B.APPROVAL_CODE AS AUTHNUM,
			    704 AS SETT_CURRENCY_CODE,
			    B.TERM_ID AS TERMID,
			    B.ADD_INFO AS ADD_INFO,
			    CONCAT(COALESCE(B.ACCOUNT_NO, ' '), '|', COALESCE(B.DEST_ACCOUNT, '')) AS ACCTNUM,
			    704 AS ISS_CURRENCY_CODE,
			    CASE WHEN B.TRACE_NO REGEXP '^[0-9]+$' THEN CAST(B.TRACE_NO AS SIGNED) ELSE NULL END AS ORIGTRACE,
			    CASE WHEN B.ACQ_ID REGEXP '^[0-9]+$' THEN CAST(B.ACQ_ID AS SIGNED) ELSE NULL END AS ORIGISS,
			    '97' AS ORIGRESPCODE,
			    704 AS CH_CURRENCY_CODE,
			    IFNULL(
			        CASE
			            WHEN CAST(B.ACQ_ID AS SIGNED) = 970429 THEN 157979
			            WHEN CAST(B.ACQ_ID AS SIGNED) = 970400 THEN 161087
			            WHEN CAST(B.ACQ_ID AS SIGNED) = 970427 THEN 166888
			            WHEN CAST(B.ACQ_ID AS SIGNED) = 970441 THEN 180906
			            WHEN CAST(B.ACQ_ID AS SIGNED) = 970425 THEN 970459
			            WHEN CAST(B.ACQ_ID AS SIGNED) = 970431 THEN 452999
			            WHEN CAST(B.ACQ_ID AS SIGNED) = 970436 THEN 686868
			            WHEN CAST(B.ACQ_ID AS SIGNED) = 970419 THEN 818188
			            WHEN CAST(B.ACQ_ID AS SIGNED) = 970407 THEN 888899
			            WHEN CAST(B.ACQ_ID AS SIGNED) = 970434 THEN 888999
			            WHEN CAST(B.ACQ_ID AS SIGNED) = 970440 THEN 970468
			            WHEN CAST(B.ACQ_ID AS SIGNED) = 970418 THEN 970488
			            WHEN CAST(B.ACQ_ID AS SIGNED) = 970432 THEN 981957
			            WHEN CAST(B.ACQ_ID AS SIGNED) = 970405 THEN 970499
			            ELSE CAST(B.ACQ_ID AS SIGNED)
			        END,
			        NULL
			    ) AS ACQUIRER_FE,
			    IFNULL(
			        CASE
			            WHEN TRIM(B.ISS_ID) = '980471' THEN 980471
			            WHEN TRIM(B.ISS_ID) = '980475' THEN 980478
			            WHEN CAST(B.ACQ_ID AS SIGNED) = 191919 THEN 970459
			            WHEN CAST(B.ACQ_ID AS SIGNED) = 970415 THEN 970489
			            ELSE
			                IFNULL(
			                    CASE
			                        WHEN CAST(B.ACQ_ID AS SIGNED) = 970429 THEN 157979
			                        WHEN CAST(B.ACQ_ID AS SIGNED) = 970400 THEN 161087
			                        WHEN CAST(B.ACQ_ID AS SIGNED) = 970427 THEN 166888
			                        WHEN CAST(B.ACQ_ID AS SIGNED) = 970441 THEN 180906
			                        WHEN CAST(B.ACQ_ID AS SIGNED) = 970425 THEN 970459
			                        WHEN CAST(B.ACQ_ID AS SIGNED) = 970431 THEN 452999
			                        WHEN CAST(B.ACQ_ID AS SIGNED) = 970436 THEN 686868
			                        WHEN CAST(B.ACQ_ID AS SIGNED) = 970419 THEN 818188
			                        WHEN CAST(B.ACQ_ID AS SIGNED) = 970407 THEN 888899
			                        WHEN CAST(B.ACQ_ID AS SIGNED) = 970434 THEN 888999
			                        WHEN CAST(B.ACQ_ID AS SIGNED) = 970440 THEN 970468
			                        WHEN CAST(B.ACQ_ID AS SIGNED) = 970418 THEN 970488
			                        WHEN CAST(B.ACQ_ID AS SIGNED) = 970432 THEN 981957
			                        WHEN CAST(B.ACQ_ID AS SIGNED) = 970405 THEN 970499
			                        ELSE CAST(B.ACQ_ID AS SIGNED)
			                    END,
			                    NULL
			                )
			        END,
			        NULL
			    ) AS ACQUIRER_RP,
			    IFNULL(
			        CASE
			            WHEN CAST(B.ACQ_ID AS SIGNED) = 191919 THEN 970459
			            WHEN CAST(B.ACQ_ID AS SIGNED) = 970415 THEN 970489
			            ELSE
			                IFNULL(
			                    CASE
			                        WHEN CAST(B.ACQ_ID AS SIGNED) = 970429 THEN 157979
			                        WHEN CAST(B.ACQ_ID AS SIGNED) = 970400 THEN 161087
			                        WHEN CAST(B.ACQ_ID AS SIGNED) = 970427 THEN 166888
			                        WHEN CAST(B.ACQ_ID AS SIGNED) = 970441 THEN 180906
			                        WHEN CAST(B.ACQ_ID AS SIGNED) = 970425 THEN 970459
			                        WHEN CAST(B.ACQ_ID AS SIGNED) = 970431 THEN 452999
			                        WHEN CAST(B.ACQ_ID AS SIGNED) = 970436 THEN 686868
			                        WHEN CAST(B.ACQ_ID AS SIGNED) = 970419 THEN 818188
			                        WHEN CAST(B.ACQ_ID AS SIGNED) = 970407 THEN 888899
			                        WHEN CAST(B.ACQ_ID AS SIGNED) = 970434 THEN 888999
			                        WHEN CAST(B.ACQ_ID AS SIGNED) = 970440 THEN 970468
			                        WHEN CAST(B.ACQ_ID AS SIGNED) = 970418 THEN 970488
			                        WHEN CAST(B.ACQ_ID AS SIGNED) = 970432 THEN 981957
			                        WHEN CAST(B.ACQ_ID AS SIGNED) = 970405 THEN 970499
			                        ELSE CAST(B.ACQ_ID AS SIGNED)
			                    END,
			                    NULL
			                )
			        END,
			        NULL
			    ) AS ISSUER_FE,
			    IFNULL(
			        CASE
			            WHEN TRIM(B.ISS_ID) = '980471' THEN 980471
			            WHEN TRIM(B.ISS_ID) = '980475' THEN 980478
			            WHEN CAST(B.ACQ_ID AS SIGNED) = 191919 THEN 970459
			            WHEN CAST(B.ACQ_ID AS SIGNED) = 970415 THEN 970489
			            ELSE
			                IFNULL(
			                    CASE
			                        WHEN CAST(B.ACQ_ID AS SIGNED) = 970429 THEN 157979
			                        WHEN CAST(B.ACQ_ID AS SIGNED) = 970400 THEN 161087
			                        WHEN CAST(B.ACQ_ID AS SIGNED) = 970427 THEN 166888
			                        WHEN CAST(B.ACQ_ID AS SIGNED) = 970441 THEN 180906
			                        WHEN CAST(B.ACQ_ID AS SIGNED) = 970425 THEN 970459
			                        WHEN CAST(B.ACQ_ID AS SIGNED) = 970431 THEN 452999
			                        WHEN CAST(B.ACQ_ID AS SIGNED) = 970436 THEN 686868
			                        WHEN CAST(B.ACQ_ID AS SIGNED) = 970419 THEN 818188
			                        WHEN CAST(B.ACQ_ID AS SIGNED) = 970407 THEN 888899
			                        WHEN CAST(B.ACQ_ID AS SIGNED) = 970434 THEN 888999
			                        WHEN CAST(B.ACQ_ID AS SIGNED) = 970440 THEN 970468
			                        WHEN CAST(B.ACQ_ID AS SIGNED) = 970418 THEN 970488
			                        WHEN CAST(B.ACQ_ID AS SIGNED) = 970432 THEN 981957
			                        WHEN CAST(B.ACQ_ID AS SIGNED) = 970405 THEN 970499
			                        ELSE CAST(B.ACQ_ID AS SIGNED)
			                    END,
			                    NULL
			                )
			        END,
			        NULL
			    ) AS ISSUER_RP,
			    CASE
			        WHEN B.TCC = '99' THEN 930000
			        WHEN B.TCC = '95' THEN 950000
			        WHEN B.SERVICE_CODE = 'QR_PUSH' THEN 890000
			        WHEN B.TCC = '97' THEN 720000
			        WHEN B.TCC = '98' THEN 730000
			        ELSE 910000
			    END AS PCODE2,
			    'IBT' AS FROM_SYS,
			    CASE
			        WHEN TRIM(B.ISS_ID) = '980472' THEN 980471
			        WHEN TRIM(B.ISS_ID) = '980474' THEN 980478
			        WHEN B.BEN_ID IS NOT NULL AND B.PROC_CODE IN ('912020', '910020')
			            THEN IFNULL(
			                CASE
			                    WHEN (SELECT COUNT(*) FROM IBFT_BANK_BINS WHERE BIN = B.BEN_ID) = 1 THEN
			                        CASE
			                            WHEN (SELECT CAST(MEMBER_ID AS SIGNED) FROM IBFT_BANK_BINS WHERE BIN = B.BEN_ID LIMIT 1) = 191919 THEN 970459
			                            WHEN (SELECT CAST(MEMBER_ID AS SIGNED) FROM IBFT_BANK_BINS WHERE BIN = B.BEN_ID LIMIT 1) = 970415 THEN 970489
			                            ELSE (SELECT CAST(MEMBER_ID AS SIGNED) FROM IBFT_BANK_BINS WHERE BIN = B.BEN_ID LIMIT 1)
			                        END
			                    ELSE NULL
			                END,
			                NULL
			            )
			        ELSE
			            IFNULL(
			                CASE
			                    WHEN (SELECT COUNT(*) FROM IBFT_BANK_BINS WHERE BIN = LEFT(B.DEST_ACCOUNT, 6)) = 1 THEN
			                        CASE
			                            WHEN (SELECT CAST(MEMBER_ID AS SIGNED) FROM IBFT_BANK_BINS WHERE BIN = LEFT(B.DEST_ACCOUNT, 6) LIMIT 1) = 191919 THEN 970459
			                            WHEN (SELECT CAST(MEMBER_ID AS SIGNED) FROM IBFT_BANK_BINS WHERE BIN = LEFT(B.DEST_ACCOUNT, 6) LIMIT 1) = 970415 THEN 970489
			                            ELSE (SELECT CAST(MEMBER_ID AS SIGNED) FROM IBFT_BANK_BINS WHERE BIN = LEFT(B.DEST_ACCOUNT, 6) LIMIT 1)
			                        END
			                    ELSE NULL
			                END,
			                NULL
			            )
			    END AS BB_BIN,
			    CASE
			        WHEN B.BEN_ID IN (SELECT TGTT_ID FROM TGTT_20) THEN IFNULL(CASE WHEN B.BEN_ID REGEXP '^[0-9]+$' THEN CAST(B.BEN_ID AS SIGNED) ELSE NULL END, NULL)
			        WHEN TRIM(B.ISS_ID) IN ('980472', '980474', '980475')
			            THEN CASE
			                WHEN B.BEN_ID IS NOT NULL AND B.PROC_CODE IN ('912020', '910020')
			                    THEN IFNULL(
			                        CASE
			                            WHEN (SELECT COUNT(*) FROM IBFT_BANK_BINS WHERE BIN = B.BEN_ID) = 1 THEN
			                                CASE
			                                    WHEN (SELECT CAST(MEMBER_ID AS SIGNED) FROM IBFT_BANK_BINS WHERE BIN = B.BEN_ID LIMIT 1) = 191919 THEN 970459
			                                    WHEN (SELECT CAST(MEMBER_ID AS SIGNED) FROM IBFT_BANK_BINS WHERE BIN = B.BEN_ID LIMIT 1) = 970415 THEN 970489
			                                    ELSE (SELECT CAST(MEMBER_ID AS SIGNED) FROM IBFT_BANK_BINS WHERE BIN = B.BEN_ID LIMIT 1)
			                                END
			                            ELSE NULL
			                        END,
			                        NULL
			                    )
			                ELSE
			                    IFNULL(
			                        CASE
			                            WHEN (SELECT COUNT(*) FROM IBFT_BANK_BINS WHERE BIN = LEFT(B.DEST_ACCOUNT, 6)) = 1 THEN
			                                CASE
			                                    WHEN (SELECT CAST(MEMBER_ID AS SIGNED) FROM IBFT_BANK_BINS WHERE BIN = LEFT(B.DEST_ACCOUNT, 6) LIMIT 1) = 191919 THEN 970459
			                                    WHEN (SELECT CAST(MEMBER_ID AS SIGNED) FROM IBFT_BANK_BINS WHERE BIN = LEFT(B.DEST_ACCOUNT, 6) LIMIT 1) = 970415 THEN 970489
			                                    ELSE (SELECT CAST(MEMBER_ID AS SIGNED) FROM IBFT_BANK_BINS WHERE BIN = LEFT(B.DEST_ACCOUNT, 6) LIMIT 1)
			                                END
			                            ELSE NULL
			                        END,
			                        NULL
			                    )
			            END
			        ELSE IFNULL(CASE WHEN B.BEN_ID REGEXP '^[0-9]+$' THEN CAST(B.BEN_ID AS SIGNED) ELSE NULL END, NULL)
			    END AS BB_BIN_ORIG,
			    NULL AS CONTENT_FUND,
			    'MTI=200' AS TXNSRC,
			    B.ACQ_COUNTRY,
			    B.POS_ENTRY_CODE,
			    B.POS_CONDITION_CODE,
			    B.ADDRESPONSE,
			    B.MVV,
			    CASE WHEN B.F4 REGEXP '^[0-9]+$' THEN CAST(COALESCE(B.F4, '0') AS SIGNED) ELSE NULL END AS F4,
			    CASE WHEN B.F4 REGEXP '^[0-9]+$' THEN CAST(COALESCE(B.F5, '0') AS SIGNED) ELSE NULL END AS F5,
			    CASE WHEN B.F6 REGEXP '^[0-9]+$' THEN CAST(COALESCE(B.F6, '0') AS SIGNED) ELSE NULL END AS F6,
			    B.F49,
			    B.SETTLEMENT_CODE,
			    B.SETTLEMENT_RATE,
			    B.ISS_CONV_RATE,
			    B.TCC,
			    B.REF_NO,
			    TRUNCATE(B.TNX_STAMP, 0) AS trandate,
			    DATE_FORMAT(B.TNX_STAMP, '%H%i%s') AS trantime,
			    B.CARD_ACCEPT_NAME_LOCATION AS ACCEPTORNAME,
			    B.CARD_ACCEPT_ID_CODE AS TERMLOC,
			    CASE
			        WHEN (
			            CASE
			                WHEN B.SETTLE_DATE = '0229' THEN
			                    IFNULL(
			                        STR_TO_DATE(
			                            CONCAT(
			                                (ROUND(FLOOR(YEAR(CURDATE()) / 4)) * 4),
			                                B.SETTLE_DATE
			                            ),
			                            '%Y%m%d'
			                        ),
			                        STR_TO_DATE(
			                            CONCAT(
			                                (ROUND(FLOOR((YEAR(CURDATE()) - 1) / 4)) * 4),
			                                B.SETTLE_DATE
			                            ),
			                            '%Y%m%d'
			                        )
			                    )
			                ELSE
			                    IFNULL(
			                        STR_TO_DATE(
			                            CONCAT(YEAR(CURDATE()), B.SETTLE_DATE),
			                            '%Y%m%d'
			                        ),
			                        STR_TO_DATE(
			                            CONCAT(YEAR(DATE_SUB(CURDATE(), INTERVAL 1 YEAR)), B.SETTLE_DATE),
			                            '%Y%m%d'
			                        )
			                    )
			            END
			        ) > CURDATE() THEN
			            CASE
			                WHEN B.SETTLE_DATE = '0229' THEN
			                    IFNULL(
			                        STR_TO_DATE(
			                            CONCAT(
			                                (ROUND(FLOOR((YEAR(CURDATE()) - 1) / 4)) * 4),
			                                B.SETTLE_DATE
			                            ),
			                            '%Y%m%d'
			                        ),
			                        NULL
			                    )
			                ELSE
			                    IFNULL(
			                        STR_TO_DATE(
			                            CONCAT(YEAR(DATE_SUB(CURDATE(), INTERVAL 1 YEAR)), B.SETTLE_DATE),
			                            '%Y%m%d'
			                        ),
			                        NULL
			                    )
			            END
			        ELSE
			            CASE
			                WHEN B.SETTLE_DATE = '0229' THEN
			                    IFNULL(
			                        STR_TO_DATE(
			                            CONCAT(
			                                (ROUND(FLOOR(YEAR(CURDATE()) / 4)) * 4),
			                                B.SETTLE_DATE
			                            ),
			                            '%Y%m%d'
			                        ),
			                        STR_TO_DATE(
			                            CONCAT(
			                                (ROUND(FLOOR((YEAR(CURDATE()) - 1) / 4)) * 4),
			                                B.SETTLE_DATE
			                            ),
			                            '%Y%m%d'
			                        )
			                    )
			                ELSE
			                    IFNULL(
			                        STR_TO_DATE(
			                            CONCAT(YEAR(CURDATE()), B.SETTLE_DATE),
			                            '%Y%m%d'
			                        ),
			                        STR_TO_DATE(
			                            CONCAT(YEAR(DATE_SUB(CURDATE(), INTERVAL 1 YEAR)), B.SETTLE_DATE),
			                            '%Y%m%d'
			                        )
			                    )
			            END
			    END AS F15,
			    CASE WHEN B.PROC_CODE REGEXP '^[0-9]+$' THEN CAST(B.PROC_CODE AS SIGNED) ELSE NULL END AS pcode_orig,
			    B.ACCOUNT_NO,
			    B.DEST_ACCOUNT
			FROM V_ISOMESSAGE_TMP_TURN B
			WHERE B.CARD_NO IS NOT NULL
			  AND B.MTI = '0200'
			  AND (COALESCE(CASE WHEN B.BEN_ID REGEXP '^[0-9]+$' THEN CAST(B.BEN_ID AS SIGNED) ELSE 0 END, 0) <> 0
			      AND COALESCE(CASE WHEN B.TRACE_NO REGEXP '^[0-9]+$' THEN CAST(B.TRACE_NO AS SIGNED) ELSE 0 END, 0) <> 0)
			ON DUPLICATE KEY UPDATE
			    RESPCODE = CASE
			        WHEN SHCLOG_SETT_IBFT.AMOUNT <> B.AMOUNT AND SHCLOG_SETT_IBFT.RESPCODE = 0 THEN 116
			        ELSE SHCLOG_SETT_IBFT.RESPCODE
			    END,
			    TXNSRC = CASE
			        WHEN SHCLOG_SETT_IBFT.AMOUNT <> B.AMOUNT THEN 'RC=99'
			        ELSE CAST(SHCLOG_SETT_IBFT.TXNSRC AS CHAR)
			    END, CONTENT_FUND = B.IBFT_INFO;
												""";
	// step 3.1 – UPDATE (match theo đúng điều kiện MERGE gốc)
	private static final String SQL_STEP3_1_UPDATE = """
			UPDATE SHCLOG_SETT_IBFT A
			JOIN V_ISOMESSAGE_TMP_TURN_0200 B
			  ON  A.PAN        = B.card_no_trim
			  AND A.ORIGTRACE  = B.trace_no_num
			  AND A.TERMID     = B.term_id
			  AND DATE_FORMAT(A.LOCAL_DATE, '%m%d') = B.local_mmdd
			  AND A.LOCAL_TIME = B.local_time_num
			  AND A.ACQUIRER   = B.acq_id_num
			SET
			  A.RESPCODE = CASE
			                 WHEN A.AMOUNT <> ROUND(B.amount_raw_num/100, 0) AND A.RESPCODE = 0
			                 THEN 116 ELSE A.RESPCODE
			               END,
			  A.TXNSRC   = CASE
			                 WHEN A.AMOUNT <> ROUND(B.amount_raw_num/100, 0)
			                 THEN 'RC=99' ELSE A.TXNSRC
			               END,
			  A.CONTENT_FUND = B.IBFT_INFO
			""";

	// step 3.2 – INSERT (NOT EXISTS) + dedup nguồn (1 dòng/khóa, ưu tiên TNX_STAMP
	// mới nhất)
	private static final String SQL_STEP3_2INSERT_NOT_EXISTS = """
			INSERT INTO SHCLOG_SETT_IBFT (
			  PAN, ORIGTRACE, TERMID, LOCAL_DATE, LOCAL_TIME, ACQUIRER,
			  DATA_ID, PPCODE, MSGTYPE, PCODE, AMOUNT, ACQ_CURRENCY_CODE, TRACE,
			  SETTLEMENT_DATE, ISSUER, RESPCODE, MERCHANT_TYPE, MERCHANT_TYPE_ORIG, AUTHNUM,
			  SETT_CURRENCY_CODE, ADD_INFO, ACCTNUM, ISS_CURRENCY_CODE, ORIGISS,
			  ORIGRESPCODE, CH_CURRENCY_CODE, ACQUIRER_FE, ACQUIRER_RP, ISSUER_FE, ISSUER_RP,
			  PCODE2, FROM_SYS, BB_BIN, BB_BIN_ORIG, CONTENT_FUND, TXNSRC, ACQ_COUNTRY,
			  POS_ENTRY_CODE, POS_CONDITION_CODE, ADDRESPONSE, MVV, F4, F5, F6, F49,
			  SETTLEMENT_CODE, SETTLEMENT_RATE, ISS_CONV_RATE, TCC,
			  refnum, trandate, trantime, ACCEPTORNAME, TERMLOC, F15, pcode_orig,
			  ACCOUNT_NO, DEST_ACCOUNT
			)
			WITH base AS (
			  /* dedup nguồn: 1 dòng/khóa, ưu tiên TNX_STAMP mới nhất */
			  SELECT *
			  FROM (
			    SELECT
			      card_no_trim, trace_no_num, term_id, local_mmdd, local_time_num, acq_id_num,
			      MTI, BEN_ID, SERVICE_CODE, ISS_ID, PROC_CODE, IBFT_INFO,
			      ACQ_COUNTRY, POS_ENTRY_CODE, POS_CONDITION_CODE, ADDRESPONSE, MVV,
			      F4, F5, F6, F49,
			      SETTLEMENT_CODE, SETTLEMENT_RATE, ISS_CONV_RATE, TCC,
			      REF_NO, TNX_STAMP, CARD_ACCEPT_NAME_LOCATION, CARD_ACCEPT_ID_CODE,
			      SETTLE_DATE, ACCOUNT_NO, DEST_ACCOUNT, MCC, APPROVAL_CODE, ADD_INFO,
			      amount_ins_num, f4_num, f5_num, f6_num, trace2_num,
			      ROW_NUMBER() OVER (
			        PARTITION BY card_no_trim, trace_no_num, term_id, local_mmdd, local_time_num, acq_id_num
			        ORDER BY TNX_STAMP DESC
			      ) rn
			    FROM V_ISOMESSAGE_TMP_TURN_0200
			  ) t
			  WHERE t.rn = 1
			),
			src AS (
			  SELECT b.*,
			         /* Thay MAP_IBFT_ACQ_ID(acq_id_num) bằng CASE cố định */
			         CASE CAST(b.acq_id_num AS UNSIGNED)
			           WHEN 970429 THEN 157979
			           WHEN 970400 THEN 161087
			           WHEN 970427 THEN 166888
			           WHEN 970441 THEN 180906
			           WHEN 970425 THEN 970459
			           WHEN 970431 THEN 452999
			           WHEN 970436 THEN 686868
			           WHEN 970419 THEN 818188
			           WHEN 970407 THEN 888899
			           WHEN 970434 THEN 888999
			           WHEN 970440 THEN 970468
			           WHEN 970418 THEN 970488
			           WHEN 970432 THEN 981957
			           WHEN 970405 THEN 970499
			           ELSE CAST(b.acq_id_num AS UNSIGNED)
			         END AS mapped_acq_id
			  FROM base b
			)
			SELECT
			  s.card_no_trim,
			  s.trace_no_num,
			  s.term_id,
			  STR_TO_DATE(CONCAT(YEAR(CURDATE()), s.local_mmdd), '%Y%m%d'),
			  s.local_time_num,
			  s.acq_id_num,

			  1,
			  CAST(s.PROC_CODE AS UNSIGNED),
			  '210',
			  CAST(s.PROC_CODE AS UNSIGNED),
			  s.amount_ins_num,
			  704,
			  s.trace2_num,

			  STR_TO_DATE(CONCAT(YEAR(CURDATE()), LPAD(s.SETTLE_DATE,4,'0')), '%Y%m%d'),
			  s.acq_id_num,
			  CASE
			    WHEN s.BEN_ID = '971133' AND s.DEST_ACCOUNT LIKE 'NPDC%' THEN 68
			    WHEN s.SERVICE_CODE = 'QR_PUSH' THEN 68
			    WHEN s.BEN_ID = '971100' AND s.TCC = '99' THEN 68
			    WHEN TRIM(s.ISS_ID) IN ('980471','980472') THEN 68
			    ELSE 0
			  END,
			  6011,
			  CAST(s.MCC AS UNSIGNED),
			  s.APPROVAL_CODE,
			  704,
			  s.ADD_INFO,
			  CONCAT(IFNULL(s.ACCOUNT_NO,' '), '|', IFNULL(s.DEST_ACCOUNT,'')),
			  704,

			  /* ORIGISS: ưu tiên các ngoại lệ, sau đó dùng mapped_acq_id (thay MAP_IBFT_ACQ_ID) */
			  CASE
			    WHEN TRIM(s.ISS_ID) = '980471' THEN 980471
			    WHEN TRIM(s.ISS_ID) = '980475' THEN 980478
			    WHEN s.acq_id_num = 191919 THEN 970459
			    WHEN s.acq_id_num = 970415 THEN 970489
			    ELSE s.mapped_acq_id
			  END,

			  '97',
			  704,
			  NULL, NULL, NULL, NULL,

			  /* BB_BIN_ORIG: ngoại lệ rồi đến mapped_acq_id */
			  CASE
			    WHEN s.acq_id_num = 191919 THEN 970459
			    WHEN s.acq_id_num = 970415 THEN 970489
			    ELSE s.mapped_acq_id
			  END,

			  'IBT',

			  /* === Vị trí #1: GET_IBT_BIN(...) → JOIN + CASE theo BEN_ID/DEST_ACCOUNT === */
			  CASE
			    WHEN TRIM(s.ISS_ID) = '980472' THEN 980471
			    WHEN TRIM(s.ISS_ID) = '980474' THEN 980478
			    WHEN s.BEN_ID IS NOT NULL AND s.PROC_CODE IN ('912020','910020')
			      THEN CASE
			             WHEN CAST(i_ben.MEMBER_ID  AS UNSIGNED) = 191919 THEN 970459
			             WHEN CAST(i_ben.MEMBER_ID  AS UNSIGNED) = 970415 THEN 970489
			             ELSE CAST(i_ben.MEMBER_ID  AS UNSIGNED)
			           END
			    ELSE   CASE
			             WHEN CAST(i_dest.MEMBER_ID AS UNSIGNED) = 191919 THEN 970459
			             WHEN CAST(i_dest.MEMBER_ID AS UNSIGNED) = 970415 THEN 970489
			             ELSE CAST(i_dest.MEMBER_ID AS UNSIGNED)
			           END
			  END,

			  /* === Vị trí #2: TO_NUMBER_BNV(BEN_ID) (theo yêu cầu: dùng CAST TiDB) + fallback GET_IBT_BIN === */
			  CASE
			    WHEN s.BEN_ID IN (SELECT TGTT_ID FROM TGTT_20) THEN
			      CAST(s.BEN_ID AS UNSIGNED)  -- thay TO_NUMBER_BNV(s.BEN_ID)
			    WHEN TRIM(s.ISS_ID) IN ('980472','980474','980475') THEN
			      CASE
			        WHEN s.BEN_ID IS NOT NULL AND s.PROC_CODE IN ('912020','910020')
			          THEN CASE
			                 WHEN CAST(i_ben.MEMBER_ID  AS UNSIGNED) = 191919 THEN 970459
			                 WHEN CAST(i_ben.MEMBER_ID  AS UNSIGNED) = 970415 THEN 970489
			                 ELSE CAST(i_ben.MEMBER_ID  AS UNSIGNED)
			               END
			        ELSE   CASE
			                 WHEN CAST(i_dest.MEMBER_ID AS UNSIGNED) = 191919 THEN 970459
			                 WHEN CAST(i_dest.MEMBER_ID AS UNSIGNED) = 970415 THEN 970489
			                 ELSE CAST(i_dest.MEMBER_ID AS UNSIGNED)
			               END
			      END
			    ELSE
			      CAST(s.BEN_ID AS UNSIGNED)  -- thay TO_NUMBER_BNV(s.BEN_ID)
			  END,

			  s.IBFT_INFO,
			  'MTI=200',
			  s.ACQ_COUNTRY, s.POS_ENTRY_CODE, s.POS_CONDITION_CODE, s.ADDRESPONSE, s.MVV,
			  s.f4_num, s.f5_num, s.f6_num, s.F49,
			  s.SETTLEMENT_CODE, s.SETTLEMENT_RATE, s.ISS_CONV_RATE, s.TCC,
			  s.REF_NO, DATE(s.TNX_STAMP), DATE_FORMAT(s.TNX_STAMP, '%H%i%s'),
			  s.CARD_ACCEPT_NAME_LOCATION, s.CARD_ACCEPT_ID_CODE,
			  NULL, CAST(s.PROC_CODE AS UNSIGNED),
			  s.ACCOUNT_NO, s.DEST_ACCOUNT
			FROM src s
			LEFT JOIN IBFT_BANK_BINS i_ben
			       ON i_ben.BIN  = s.BEN_ID
			LEFT JOIN IBFT_BANK_BINS i_dest
			       ON i_dest.BIN = SUBSTRING(s.DEST_ACCOUNT,1,6)
			WHERE NOT EXISTS (
			  SELECT 1
			  FROM SHCLOG_SETT_IBFT A
			  WHERE A.PAN        = s.card_no_trim
			    AND A.ORIGTRACE  = s.trace_no_num
			    AND A.TERMID     = s.term_id
			    AND DATE_FORMAT(A.LOCAL_DATE,'%m%d') = s.local_mmdd
			    AND A.LOCAL_TIME = s.local_time_num
			    AND A.ACQUIRER   = s.acq_id_num
			);
									""";

	private static final String SQL_STEP_4_FINÍH_MERGE = """
			INSERT INTO ERR_EX(ERR_TIME, ERR_CODE, ERR_DETAIL, ERR_MODULE)
						VALUES (NOW(), 0, 'Finish Merge SHCLOG_SETT_IBFT and ISOMESSAGE_TMP_TURN', :module);
						""";
	private static final String SQL_STEP_5_UPDATE_SHCLOG_SETT_IBFT_STT = """
			UPDATE SHCLOG_SETT_IBFT AS a
			JOIN (
			    SELECT
			        tidb_id,
			        ROW_NUMBER() OVER (ORDER BY TRANDATE, TRANTIME, TRACE, tidb_id) + :iSTT AS new_stt
			    FROM SHCLOG_SETT_IBFT
			    WHERE STT IS NULL
			      AND ORIGRESPCODE = 97
			) AS b
			ON a.tidb_id = b.tidb_id
			SET a.STT = b.new_stt;
									""";

	private static final String SQL_STEP_6_FINISH_UPDATE = """
			INSERT INTO ERR_EX(ERR_TIME, ERR_CODE, ERR_DETAIL, ERR_MODULE)
			VALUES (
			    NOW(),
			    0,
			    CONCAT('Finish Update STT for ', :rowupdate, ' transactions ORIGRESPCODE = 97 in SHCLOG_SETT_IBFT'),
			    :module
			);
						""";

	private static final String SQL_STEP_7_END_MERGE = """
			INSERT INTO ERR_EX(ERR_TIME, ERR_CODE, ERR_DETAIL, ERR_MODULE)
			VALUES (NOW(), 0, 'End Merge SHCLOG_SETT_IBFT and ISOMESSAGE_TMP_TURN', :module);
						""";

	/*
	 * ========================= 2) HÀM THỰC THI CHUNG =========================
	 */

	private int executeUpdate(String sql) {
		return jdbcTemplate.update(sql);
	}

	private int executeUpdate(String sql, Object... args) {
		return jdbcTemplate.update(sql, args);
	}

	private void safeAlterUniqueKey() {
		try {
			executeUpdate(SQL_STEP0_UNIQUE_KEY);
			log("Created unique key uk_ibft_merge");
		} catch (Exception ignore) {
			log("Unique key existed (skip)");
		}
	}

	private void log(String msg) {
		executeUpdate("""
				    INSERT INTO ERR_EX(ERR_TIME, ERR_CODE, ERR_DETAIL, ERR_MODULE)
				    VALUES (NOW(), 0, ?, ?)
				""", msg, MODULE);
	}

	/*
	 * ========================= 3) CÁC STEP CỤ THỂ =========================
	 */

//	@Transactional
//	public void step0Prepare() {
//		safeAlterUniqueKey();
//		executeUpdate(SQL_STEP0_SOURCE_VIEW);
//		log("Created/Refreshed view V_IBFT_SRC_0200");
//	}

	@Transactional
	public void step31Update() {
		MapSqlParameterSource p = new MapSqlParameterSource();
//        int rows = executeUpdate(SQL_STEP3_1_UPDATE);
		exec(MODULE, SQL_STEP3_1_UPDATE, p);
//        log("Step1 UPDATE affected: " + rows);
//        return rows;
	}

	@Transactional
	public void step32Insert() {
		MapSqlParameterSource p = new MapSqlParameterSource();
		exec(MODULE, SQL_STEP3_2INSERT_NOT_EXISTS, p);
//        int rows = executeUpdate(SQL_STEP3_2INSERT_NOT_EXISTS);
//        log("Step2 INSERT affected: " + rows);
//        return rows;
	}

	@Transactional
	public int step2getMaxSTT() {
		Integer iSTT = jdbcTemplate.queryForObject("SELECT COALESCE(MAX(STT), 0) FROM SHCLOG_SETT_IBFT", Integer.class);
//        jdbc.update("DROP TEMPORARY TABLE IF EXISTS _stt_tmp");
//        executeUpdate(SQL_STEP3_BUILD_TMP, iSTT);
//        int rows = executeUpdate(SQL_STEP3_APPLY);
//        jdbc.update("DROP TEMPORARY TABLE IF EXISTS _stt_tmp");
//        log("Step3 STT updated: " + rows);
		return iSTT;
	}

	/** Chạy toàn bộ pipeline theo thứ tự các bước */
	@Transactional
	public void runAll() {
		log("Begin pipeline");
		MapSqlParameterSource p = new MapSqlParameterSource().addValue("module", MODULE);
		// step 2 get max stt from SHCLOG_SETT_IBFT
		int iSTT = step2getMaxSTT();
//        step0Prepare();
		p.addValue("iSTT", iSTT);
//		step31Update();
//		step32Insert();
//        step3UpdateStt();
		exec(MODULE, SQL_STEP_3, p);
		// step 4
		exec(MODULE, SQL_STEP_4_FINÍH_MERGE, p);
		// step 5 Update SHCLOG_SETT_IBFT
		int rows = exec(MODULE, SQL_STEP_5_UPDATE_SHCLOG_SETT_IBFT_STT, p);
		p.addValue("rowupdate", rows);
		exec(MODULE, SQL_STEP_6_FINISH_UPDATE, p);
		exec(MODULE, SQL_STEP_7_END_MERGE, p);
		log("End pipeline");
	}

	private int exec(String tag, String sql, MapSqlParameterSource p) {
		String trimmed = sql == null ? "" : sql.trim();
		if (trimmed.isEmpty()) {
			log.info("{}: SKIPPED (chưa thay SQL TiDB)", tag);
			return -1;
		}
		log.info("sql tag: {}", SqlLogUtils.renderSql(sql, p.getValues()));
		int rows = jdbc.update(sql, p);
		log.info("{}: {} row(s)", tag, rows);
		return rows;
	}
}
