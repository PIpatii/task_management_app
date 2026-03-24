package management.application.dto.user;

import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
    private String email;
    private String firstName;
    private String lastName;
    private Set<Long> roleIds = new HashSet<>();
}
