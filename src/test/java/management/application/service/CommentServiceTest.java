package management.application.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import management.application.dto.comment.AddCommentRequestDto;
import management.application.dto.comment.CommentDto;
import management.application.mapper.CommentMapper;
import management.application.model.Comment;
import management.application.model.User;
import management.application.repository.CommentRepository;
import management.application.repository.UserRepository;
import management.application.service.impl.CommentServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import static management.application.helper.TestDataHelper.createAddCommentRequestDto;
import static management.application.helper.TestDataHelper.createComment;
import static management.application.helper.TestDataHelper.createCommentDto;
import static management.application.helper.TestDataHelper.createUser;
import static management.application.helper.TestSecurityUtils.mockAuth;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {
    @InjectMocks
    private CommentServiceImpl commentService;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CommentMapper commentMapper;

    @Test
    @DisplayName("get task's comments by task id")
    public void getCommentsByTaskId_success() {
        Comment firstComment = createComment(1L, 1L, 1L,
                "text", LocalDateTime.now());

        Comment secondComment = createComment(2L, 1L, 1L,
                "text", LocalDateTime.now());

        int expectedSize = 2;

        when(commentRepository.getCommentsByTaskId(1L, PageRequest.of(0, 10)))
                .thenReturn(new PageImpl<>(Arrays.asList(firstComment, secondComment)));
        Page<CommentDto> actual = commentService.getComments(1L, PageRequest.of(0, 10));

        assertEquals(expectedSize, actual.getContent().size());
    }

    @Test
    @DisplayName("add comment to a task")
    public void addComment_success() {
        mockAuth("email");

        User user = createUser(1L, "email", "password",
                "firstName", "lastName");

        AddCommentRequestDto requestDto = createAddCommentRequestDto(1L, "text");

        Comment comment = createComment(1L, requestDto.getTaskId(), 1L,
                requestDto.getText(), LocalDateTime.now());

        CommentDto expected = createCommentDto(comment.getId(), comment.getTaskId(),
                comment.getUserId(), comment.getText(), comment.getTimestamp());

        when(userRepository.getUserByEmail("email")).thenReturn(user);
        when(commentMapper.toEntity(requestDto)).thenReturn(comment);
        when(commentRepository.save(comment)).thenReturn(comment);
        when(commentMapper.toDto(comment)).thenReturn(expected);

        CommentDto actual = commentService.addComment(requestDto);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getText(), actual.getText());
        assertEquals(expected.getTaskId(), actual.getTaskId());
    }

    @Test
    @DisplayName("delete comment from a task")
    public void deleteComment_success() {
        mockAuth("email");

        User user = createUser(1L, "email", "password",
                "firstName", "lastName");

        Comment comment = createComment(1L, 1L, 1L,
                "text", LocalDateTime.now());

        when(userRepository.getUserByEmail("email")).thenReturn(user);
        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));

        doNothing().when(commentRepository).deleteById(comment.getId());
        commentService.deleteComment(comment.getId());

        verify(commentRepository, times(1)).deleteById(comment.getId());
    }
}
