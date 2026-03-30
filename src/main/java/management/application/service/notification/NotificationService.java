package management.application.service.notification;

import lombok.RequiredArgsConstructor;
import management.application.model.User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final EmailService emailService;

    public void notifyUser(User user, String message) {
        if (user.getEmail() != null) {
            emailService.send(
                    user.getEmail(),
                    "a reminder about deadline",
                    message
            );
        }
    }

}
