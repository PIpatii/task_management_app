package management.application.repository;

import java.time.LocalDate;
import java.util.List;
import management.application.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
    Page<Task> getTasksByProjectId(Long projectId, Pageable pageable);

    List<Task> findByDeadline(LocalDate deadline);
}
