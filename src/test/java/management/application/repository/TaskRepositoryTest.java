package management.application.repository;

import java.time.LocalDate;
import java.util.List;
import management.application.model.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@RunWith(SpringRunner.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TaskRepositoryTest {
    @Autowired
    private TaskRepository taskRepository;

    @Test
    @Sql(scripts = "classpath:database/task/add-two-elements-to-tasks-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/task/remove-all-elements-from-tasks-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void getTasksByProjectId_correctData_success() {
        int expectedSize = 1;

        Page<Task> actual = taskRepository.getTasksByProjectId(1L,
                PageRequest.of(0, 10));

        assertEquals(expectedSize, actual.getContent().size());
    }

    @Test
    @Sql(scripts = "classpath:database/task/add-two-elements-to-tasks-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/task/remove-all-elements-from-tasks-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void findByDeadline_correctData_success() {
        int expectedSize = 2;

        List<Task> actual = taskRepository.findByDeadline(LocalDate.of(2026, 3, 21));

        assertEquals(expectedSize, actual.size());
    }
}
