package management.application.service.impl;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import management.application.dto.comment.AddCommentRequestDto;
import management.application.dto.comment.CommentDto;
import management.application.mapper.CommentMapper;
import management.application.model.Comment;
import management.application.model.User;
import management.application.repository.CommentRepository;
import management.application.repository.UserRepository;
import management.application.service.CommentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final UserRepository userRepository;

    @Override
    public Page<CommentDto> getComments(Long taskId, Pageable pageable) {
        Page<Comment> comments = commentRepository.getCommentsByTaskId(taskId, pageable);
        return comments.map(commentMapper::toDto);
    }

    @Override
    public CommentDto addComment(AddCommentRequestDto addCommentDto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.getUserByEmail(username);

        Comment comment = commentMapper.toEntity(addCommentDto);
        comment.setTimestamp(LocalDateTime.now());
        comment.setUserId(user.getId());
        return commentMapper.toDto(commentRepository.save(comment));
    }

    @Override
    public void deleteComment(Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.getUserByEmail(username);

        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Comment not found"));
        if (comment.getUserId().equals(user.getId())) {
            commentRepository.deleteById(id);
        } else {
            throw new RuntimeException("User cannot delete someone else's comment");
        }
    }
}
