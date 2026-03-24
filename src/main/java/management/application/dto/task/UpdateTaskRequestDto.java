package management.application.dto.task;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateTaskRequestDto {
    @NotBlank
    private String priority;
    @NotBlank
    private String status;
}
