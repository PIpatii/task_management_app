package management.application.service.notification;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import management.application.model.Task;
import management.application.repository.TaskRepository;
import management.application.repository.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeadlineNotificationScheduler {
    private final TaskRepository taskRepository;
    private final NotificationService notificationService;
    private final UserRepository userRepository;

    @Scheduled(cron = "0 0 9 * * *")
    public void sendDeadlineNotifications() {
        LocalDate tomorrow = LocalDate.now();

        List<Task> tasks = taskRepository.findByDeadline(tomorrow);
        System.out.println("tasks: " + tasks);
        System.out.println("Method has been called");
        for (Task task : tasks) {
            notificationService.notifyUser(
                    userRepository.getUserById(task.getAssigneeId()),
                    "Нагадування: завтра дедлайн задачі \"" + task.getName() + "\""
            );
        }
    }
}
