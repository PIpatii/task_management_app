package management.application.service.notification;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    @Value("${sendgrid.api-key}")
    private String apiKey;

    public void send(String to, String subject, String text) {
        Email from = new Email("my.task.managment@gmail.com");
        Email recipient = new Email(to);

        Content content = new Content("text/plain", text);
        Mail mail = new Mail(from, subject, recipient, content);

        SendGrid sg = new SendGrid(apiKey);
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            sg.api(request);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

