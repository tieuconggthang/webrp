package vn.napas.webrp.report.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class PingController {
    private final JdbcTemplate jdbc;
    @GetMapping("/db/ping")
    public Map<String,String> ping() {
        String msg = jdbc.queryForObject("SELECT 'TiDB OK' ", String.class);
        return Map.of("msg", msg);
    }
}
