package management.application.service;

import management.application.dto.comment.AddCommentRequestDto;
import management.application.dto.comment.CommentDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentService {
    Page<CommentDto> getComments(Long taskId, Pageable pageable);

    CommentDto addComment(AddCommentRequestDto addCommentDto);

    void deleteComment(Long id);
}
