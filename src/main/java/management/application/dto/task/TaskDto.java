package management.application.dto.task;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskDto {
    private Long id;
    private String name;
    private String description;
    private String priority;
    private LocalDate deadline;
    private String status;
    private Long projectId;
    private Long assigneeId;
    private Set<Long> labelsIds = new HashSet<>();
}
