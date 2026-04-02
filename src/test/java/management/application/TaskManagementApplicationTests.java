package management.application;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = "sendgrid.api-key=dummy")
class TaskManagementApplicationTests {

    @Test
    void contextLoads() {
    }

}
