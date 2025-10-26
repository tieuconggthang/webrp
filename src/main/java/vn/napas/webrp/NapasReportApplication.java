package vn.napas.webrp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class NapasReportApplication {
    public static void main(String[] args) {
        SpringApplication.run(NapasReportApplication.class, args);
    }
}
