package management.application.repository;

import java.util.Optional;
import management.application.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@RunWith(SpringRunner.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    @Sql(scripts = "classpath:database/user/add-element-to-user-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/user/remove-all-elements-from-user-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void findByEmail_correctData_success() {
        User expected = new User();
        expected.setId(2L);
        expected.setEmail("email");
        expected.setPassword("password");
        expected.setFirstName("firstName");
        expected.setLastName("lastName");

        Optional<User> actual = userRepository.findByEmail("email");

        assertEquals(expected.getId(), actual.map(User::getId).orElse(null));
        assertEquals(expected.getEmail(), actual.map(User::getEmail).orElse(null));
        assertEquals(expected.getPassword(), actual.map(User::getPassword).orElse(null));
        assertEquals(expected.getFirstName(), actual.map(User::getFirstName).orElse(null));
        assertEquals(expected.getLastName(), actual.map(User::getLastName).orElse(null));
    }

    @Test
    @Sql(scripts = "classpath:database/user/add-element-to-user-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/user/remove-all-elements-from-user-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void getUserByEmail_correctData_success() {
        User expected = new User();
        expected.setId(2L);
        expected.setEmail("email");
        expected.setPassword("password");
        expected.setFirstName("firstName");
        expected.setLastName("lastName");

        User actual = userRepository.getUserByEmail("email");

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getEmail(), actual.getEmail());
        assertEquals(expected.getPassword(), actual.getPassword());
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
    }

    @Test
    @Sql(scripts = "classpath:database/user/add-element-to-user-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/user/remove-all-elements-from-user-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void getUserById_correctData_success() {
        User expected = new User();
        expected.setId(2L);
        expected.setEmail("email");
        expected.setPassword("password");
        expected.setFirstName("firstName");
        expected.setLastName("lastName");

        User actual = userRepository.getUserById(2L);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getEmail(), actual.getEmail());
        assertEquals(expected.getPassword(), actual.getPassword());
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
    }
}

