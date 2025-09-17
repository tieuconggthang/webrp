package vn.napas.webrp.noti;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
//import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailNotifier {

//    private final JavaMailSender mailSender;

    @Value("${ibft.alert.emails:}")
    private String alertEmailsCsv;

    @Value("${spring.mail.username:no-reply@example.com}")
    private String from;

    public void notifyError(String subject, String body) {
//        if (alertEmailsCsv == null || alertEmailsCsv.isBlank()) return;
//        String[] to = alertEmailsCsv.split(",");
//        SimpleMailMessage msg = new SimpleMailMessage();
//        msg.setFrom(from);
//        msg.setTo(to);
//        msg.setSubject(subject);
//        msg.setText(body);
//        mailSender.send(msg);
    }
}
