package management.application.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddCommentRequestDto {
    @Positive
    private Long taskId;
    @NotBlank
    private String text;
}
