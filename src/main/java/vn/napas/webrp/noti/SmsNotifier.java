package vn.napas.webrp.noti;



import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class SmsNotifier {

    // cấu hình: "0366155501,0983411005,0988766330"
    @Value("${ibft.alert.phones:}")
    private String alertPhonesCsv;

    /** Giữ đúng format của store: MERGE_SHC_SETT_IBFT_200#<phones separated by ;>#<detail> */
    public void notifyError(String module, String detail) {
        List<String> phones = Arrays.stream(alertPhonesCsv.split(","))
                                    .map(String::trim).filter(s -> !s.isEmpty()).toList();
        String phoneToken = String.join(";", phones);
        String payload = module + "#" + phoneToken + "#" + detail;

        // TODO: tích hợp gateway SMS thực (Twilio, Kannel, SMPP...)
        log.warn("SMS ALERT [stub] -> {} | {}", phoneToken, payload);
    }
}
