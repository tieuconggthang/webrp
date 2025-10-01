
package vn.napas.webrp.report.service.ibft;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import vn.napas.webrp.constant.TableConstant;
import vn.napas.webrp.database.repo.TableMaintenanceRepository;
import vn.napas.webrp.database.repo.store.NapasMasterViewDomesticRepoInline19;
import vn.napas.webrp.report.util.SqlLogUtils;

import java.time.LocalDate;
import java.util.Map;

/**
 * NAPAS_MASTER_VIEW_DOMESTIC_IBFT
 */
@Service
@Slf4j
public class NapasMasterViewDomesticServiceInline19 {

    private final NapasMasterViewDomesticRepoInline19 repo;
    private final NamedParameterJdbcTemplate jdbc;
    @Autowired TableMaintenanceRepository tableMaintenanceRepository;

    public NapasMasterViewDomesticServiceInline19(NapasMasterViewDomesticRepoInline19 repo,
                                                  NamedParameterJdbcTemplate jdbc) {
        this.repo = repo;
        this.jdbc = jdbc;
    }

    @Transactional
    public void run(LocalDate fromDate, LocalDate toDate, String user) {
        String listSms = getListSmsOrDefault("0983411005");
        //step 1 insert log action
        logErr("0", "Start", "NAPAS_MASTER_VIEW_DOMESTIC_IBFT");

        // Truncate working table if present in flow
//        try { jdbc.getJdbcTemplate().execute("TRUNCATE TABLE TCKT_NAPAS_IBFT"); } catch (Exception ignore) {}
//        tableMaintenanceRepository.truncateTable(TableConstant.TCKT_NAPAS_IBFT);
        // Hook: if step 2 requires calling an earlier job, do it here
        // insertTcktSessionDomesticIbft(fromDate, toDate, user);

        repo.executeAll(fromDate, toDate, listSms);

        logErr("0", "End", "NAPAS_MASTER_VIEW_DOMESTIC_IBFT");
    }

    private String getListSmsOrDefault(String def) {
        try {
            String sql = "SELECT PARA_VALUE FROM NAPAS_PARA WHERE PARA_NAME='LIST_SMS'";
            return jdbc.query(sql, rs -> rs.next() ? rs.getString(1) : def);
        } catch (Exception e) {
            return def;
        }
    }

    private void logErr(String code, String detail, String module) {
        String sql = "INSERT INTO ERR_EX(ERR_TIME, ERR_CODE, ERR_DETAIL, ERR_MODULE) VALUES (NOW(), :c, :d, :m)";
//        MapSqlParameterSource p = (MapSqlParameterSource) Map.of("c", code, "d", detail, "m", module);
        MapSqlParameterSource p = new MapSqlParameterSource()
                .addValue("c", code)
                .addValue("d", detail)
                .addValue("m", module);
//                .addValue("LIST_SMS", user);
        log.info(SqlLogUtils.renderSql(sql, p.getValues()));
        jdbc.update(sql, p);
    }
}
