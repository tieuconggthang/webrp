package vn.napas.webrp.database.repo;
import jakarta.annotation.PostConstruct;
import vn.napas.webrp.database.dto.FeeConfigRow;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.regex.Pattern;

@Repository
public class FeeCacheLoader {
    private final NamedParameterJdbcTemplate jdbc;

    private volatile Map<String, Set<String>> pcode72Index = new HashMap<>();
    private volatile List<FeeConfigRow> feeConfigs = new ArrayList<>();

    public FeeCacheLoader(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @PostConstruct
    public void init() {
        reload(); // load ngay khi start app
    }

    /** Hàm gọi từ @Scheduled để nạp lại cache */
    @Scheduled(fixedDelay = 300_000) // 5 phút 1 lần, tùy chỉnh
    public synchronized void reload() {
        loadPcode72();
        loadFeeConfigs();
    }

    private void loadPcode72() {
        String sql = "SELECT TRIM(BANKID) BANKID, TRIM(PRANGE) PRANGE FROM PCODE72";
        Map<String, Set<String>> map = new HashMap<>();
        jdbc.query(sql, rs -> {
            String bankId = rs.getString("BANKID");
            String prange = rs.getString("PRANGE");
            Set<String> prefixes = map.computeIfAbsent(bankId, k -> new HashSet<>());
            if (prange != null) {
                for (String token : prange.split("[,;\\s]+")) {
                    if (token.matches("\\d{6}")) prefixes.add(token);
                }
            }
        });
        this.pcode72Index = map;
    }

    private void loadFeeConfigs() {
//        String sql = """
//            SELECT FEE_KEY, ISSUER, ACQUIRER, PRO_CODE, MERCHANT_TYPE, CURRENCY_CODE,
//                   IFNULL(ORDER_CONFIG,0) ORDER_CONFIG, ACTIVE,
//                   DATE(VALID_FROM) VALID_FROM, DATE(VALID_TO) VALID_TO,
//                   FEE_NOTE
//            FROM GR_FEE_CONFIG_NEW
//            """;
    	
    	
      String sql = """
      SELECT *
      FROM GR_FEE_CONFIG_NEW
      """;

        this.feeConfigs = jdbc.query(sql, (rs, rn) -> FeeConfigRow.from(rs));
    }

    // === API cho repo ===
    public Map<String, Set<String>> getPcode72Index() { return pcode72Index; }
    public List<FeeConfigRow> getFeeConfigs() { return feeConfigs; }
}
