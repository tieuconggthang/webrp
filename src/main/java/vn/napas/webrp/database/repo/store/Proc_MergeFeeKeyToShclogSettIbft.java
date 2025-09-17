package vn.napas.webrp.database.repo.store;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.napas.webrp.database.repo.sql.DbLoggerRepository;
import vn.napas.webrp.noti.SmsNotifier;

import java.sql.SQLException;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class Proc_MergeFeeKeyToShclogSettIbft {

    private final NamedParameterJdbcTemplate jdbc;
    private final DbLoggerRepository dbLog;
    private final SmsNotifier smsNotifier;
    private final String module = "MERGE_FEE_KEY_TO_SHCLOG_SETT_IBFT";

    private static final String MODULE = "MERGE_FEE_KEY_TO_SHCLOG_SETT_IBFT";



    // ==== Câu lệnh UPDATE…JOIN đã chuyển đổi (rút gọn biến chuỗi) ====
    private static final String SQL_UPDATE = /* dán NGUYÊN văn câu lệnh UPDATE ở mục (1) vào đây */ """
        UPDATE SHCLOG_SETT_IBFT S
        JOIN ZEN_CONFIG_FEE_IBFT B
          ON (
                CONCAT(
                  CONCAT(IFNULL(S.ACQUIRER_FE, S.ACQUIRER), ''), 
                  CONCAT(IFNULL(S.ISSUER_FE,   S.ISSUER),   ''), 
                  CAST(CASE WHEN S.ACQ_CURRENCY_CODE = 840 THEN 840 ELSE 704 END AS CHAR)
                ),
                CAST((
                  CASE
                    WHEN S.TRAN_CASE = '72|C3' THEN 6011
                    WHEN IFNULL(S.PCODE2,0) IN (890000,880000,720000,730000) THEN 0
                    WHEN IFNULL(S.PCODE2,0) IN (840000,968400,978400,988400,998400) THEN S.MERCHANT_TYPE
                    WHEN SUBSTR(LPAD(CAST(IFNULL(S.PCODE2,0) AS CHAR),6,'0'),1,2) IN ('98','99')
                         AND SUBSTR(LPAD(CAST(S.PCODE AS CHAR),6,'0'),1,2) = '30'
                         AND S.MERCHANT_TYPE = 6012 THEN S.MERCHANT_TYPE
                    WHEN IFNULL(S.PCODE2,0) IN (
                            960000,970000,980000,990000,968500,978500,988500,998500,
                            967500,977500,987500,997500, 967600,977600,987600,997600,
                            967700,977700,987700,997700, 967800,977800,987800,997800,
                            967900,977900,987900,997900, 966100,976100,986100,996100,
                            966200,976200,986200,996200
                         )
                         AND SUBSTR(LPAD(CAST(S.PCODE AS CHAR),6,'0'),1,2) IN ('00','20')
                         AND S.MERCHANT_TYPE_ORIG IN (
                            SELECT MERCHANT_TYPE
                            FROM GR_FEE_CONFIG_NEW
                            WHERE STR_TO_DATE(:settle, '%d/%m/%Y') BETWEEN VALID_FROM AND VALID_TO
                              AND SUBSTR(PRO_CODE,3,2) = SUBSTR(LPAD(CAST(IFNULL(S.PCODE2,0) AS CHAR),6,'0'),1,2)
                         )
                      THEN S.MERCHANT_TYPE_ORIG
                    WHEN SUBSTR(LPAD(CAST(IFNULL(S.PCODE2,0) AS CHAR),6,'0'),1,2) NOT IN ('96','97','98','99')
                         AND SUBSTR(LPAD(CAST(S.PCODE AS CHAR),6,'0'),1,2) IN ('00','20')
                         AND S.MERCHANT_TYPE_ORIG IN (4111,4131,5172,9211,9222,9223,9311,9399,8398,7523,7524,5541,5542)
                      THEN S.MERCHANT_TYPE_ORIG
                    ELSE S.MERCHANT_TYPE
                  END
                  ) AS CHAR
                ),
                (
                  CASE
                    WHEN IFNULL(S.PCODE2,0) IN (760000,967600,977600,987600,997600) THEN '76'
                    WHEN IFNULL(S.ACQUIRER_RP,0) IN (605609,220699,605608,600005)
                         AND SUBSTR(LPAD(CAST(IFNULL(S.PCODE2,0) AS CHAR),6,'0'),1,2) IN ('96','97','98','99')
                      THEN SUBSTR(LPAD(CAST(S.PCODE AS CHAR),6,'0'),1,2)
                    WHEN SUBSTR(LPAD(CAST(IFNULL(S.PCODE2,0) AS CHAR),6,'0'),1,2) IN ('98','99') AND
                        (
                          (SUBSTR(LPAD(CAST(S.PCODE AS CHAR),6,'0'),1,2)='30' AND S.MERCHANT_TYPE=6012) OR
                          (SUBSTR(LPAD(CAST(S.PCODE AS CHAR),6,'0'),1,2) IN ('01','30','35','40','94') AND S.MERCHANT_TYPE=6011) OR
                          (SUBSTR(LPAD(CAST(S.PCODE AS CHAR),6,'0'),1,2) IN ('00','20')
                              AND S.MERCHANT_TYPE_ORIG NOT IN (4111,4131,5172,9211,9222,9223,9311,9399,8398,7523,7524,5541,5542))
                        )
                      THEN SUBSTR(LPAD(CAST(S.PCODE AS CHAR),6,'0'),1,2)
                    WHEN IFNULL(S.PCODE2,0) NOT IN (
                            960000,970000,980000,990000,968500,978500,988500,998500,
                            967700,977700,987700,997700,967500,977500,987500,997500,
                            967800,977800,987800,997800,967900,977900,987900,997900,
                            966100,976100,986100,996100,966200,976200,986200,996200
                         )
                      THEN
                        CASE
                          WHEN S.ISSUER_RP = 602907
                            THEN IFNULL(S.PCODE_ORIG, SUBSTR(LPAD(CAST(S.PCODE AS CHAR),6,'0'),1,2))
                          WHEN S.PCODE2 IS NULL OR S.PCODE2 IN (950000,850000,750000,760000,780000,790000)
                            THEN SUBSTR(LPAD(CAST(S.PCODE AS CHAR),6,'0'),1,2)
                          WHEN S.PCODE2 IN (810000,820000,830000,860000,870000,880000)
                            THEN SUBSTR(LPAD(CAST(S.PCODE2 AS CHAR),6,'0'),1,2)
                          WHEN S.PCODE2 = 910000 AND (S.FROM_SYS = 'IST' OR S.TRAN_CASE = 'C3|72')
                            THEN SUBSTR(LPAD(CAST(S.PCODE AS CHAR),6,'0'),1,2)
                          WHEN S.PCODE2 = 930000
                            THEN SUBSTR(LPAD(CAST(S.PCODE AS CHAR),6,'0'),1,2)
                          WHEN IFNULL(S.PCODE2,0) IN (968400,978400,988400,998400)
                            THEN '84'
                          ELSE SUBSTR(LPAD(CAST(S.PCODE2 AS CHAR),6,'0'),1,2)
                        END
                    ELSE
                      CONCAT(
                        SUBSTR(LPAD(CAST(S.PCODE AS CHAR),6,'0'),1,2),
                        LPAD(CAST(
                          CASE CAST(S.PCODE2 AS CHAR)
                            WHEN '968500' THEN 960000
                            WHEN '978500' THEN 970000
                            WHEN '967500' THEN 960000
                            WHEN '977500' THEN 970000
                            WHEN '987500' THEN 980000
                            WHEN '997500' THEN 990000
                            WHEN '967700' THEN 960000
                            WHEN '977700' THEN 970000
                            WHEN '987700' THEN 980000
                            WHEN '997700' THEN 990000
                            WHEN '967800' THEN 960000
                            WHEN '977800' THEN 970000
                            WHEN '987800' THEN 980000
                            WHEN '997800' THEN 990000
                            WHEN '967900' THEN 960000
                            WHEN '977900' THEN 970000
                            WHEN '987900' THEN 980000
                            WHEN '997900' THEN 990000
                            WHEN '966100' THEN 960000
                            WHEN '976100' THEN 970000
                            WHEN '986100' THEN 980000
                            WHEN '996100' THEN 990000
                            WHEN '966200' THEN 960000
                            WHEN '976200' THEN 970000
                            WHEN '986200' THEN 980000
                            WHEN '996200' THEN 990000
                            ELSE IFNULL(S.PCODE2,0)
                          END AS CHAR), 6,'0'
                        )
                      )
                  END
                )
            ) = B.FEE_VALUE
        )
        SET S.FEE_KEY = B.FEE_KEY
        WHERE S.SETTLEMENT_DATE = STR_TO_DATE(:settle, '%d/%m/%Y')
          AND (
                (S.MSGTYPE = 210 AND S.RESPCODE = 0 AND S.ISREV IS NULL)
                OR (S.MSGTYPE = 430 AND S.RESPCODE = 114)
              )
          AND S.ISSUER_RP NOT IN ('602907')
          AND ( S.FEE_NOTE IS NULL OR S.FEE_NOTE NOT LIKE 'HACH TOAN DIEU CHINH%' )
          AND (
                (
                  (
                    SUBSTR(LPAD(CAST(S.PCODE AS CHAR),6,'0'),1,2) IN ('00','01','30','35','40','41','42','43','48','20')
                    AND IFNULL(S.MSGTYPE_DETAIL,'NA') NOT IN ('VPREC')
                  )
                  OR
                  (SUBSTR(LPAD(CAST(S.PCODE AS CHAR),6,'0'),1,2) = '94' AND S.MERCHANT_TYPE = 6011)
                )
                AND IFNULL(S.FROM_SYS,'IST') LIKE '%IST%'
              OR
                (
                  S.FROM_SYS IS NOT NULL
                  AND SUBSTR(LPAD(CAST(S.PCODE AS CHAR),6,'0'),1,2) IN ('01','42','91')
                )
              );
        """;

    /**
     * Thực thi job merge FEE_KEY tương đương store Oracle.
     * @param settlementDateDdMMyyyy dạng dd/MM/yyyy (vd: "07/09/2025")
     * @return số bản ghi bị ảnh hưởng (ước lượng ~ rows matched)
     */
    public int mergeFeeKeyToShclogSettIbft(String settlementDateDdMMyyyy) {
        MapSqlParameterSource p = new MapSqlParameterSource().addValue("module", MODULE)
                                                             .addValue("settle", settlementDateDdMMyyyy);
        try {
//            jdbc.update(SQL_LOG_BEGIN, p);
        	dbLog.begin(module, "Begin Merge Fee key to SHCLOG_SETT_IBFT ");
            int updated = jdbc.update(SQL_UPDATE, p);
           dbLog.end(module, "End Merge Fee key to SHCLOG_SETT_IBFT");
            log.info("[{}] Updated rows: {}", MODULE, updated);
            return updated;
        } catch (Exception ex) {
            String detail = "MERGE_FEE_KEY_TO_SHCLOG_SETT_IBFT error: " + ex.getMessage();
            int ecode = -1;
          if (ex instanceof SQLException) {
        	  ecode = ((SQLException) ex).getErrorCode();
          }
            
            dbLog.error(ecode + "", module, detail, 2);
            // TODO: nếu bạn có SmsService thì gọi ở đây giống SEND_SMS trong Oracle
            smsNotifier.notifyError(module, detail);
            return -1;
        }
    }
}
