package management.application.dto.user;

import jakarta.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateRoleRequestDto {
    @NotNull
    private Set<Long> roleIds = new HashSet<>();
}
