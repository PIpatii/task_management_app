package management.application.dto.label;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateLabelRequestDto {
    @NotBlank
    private String name;
    @NotBlank
    private String color;
}
