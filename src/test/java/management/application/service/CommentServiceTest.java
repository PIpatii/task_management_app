package management.application.service;

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
        Comment firstComment = new Comment();
        firstComment.setId(1L);
        firstComment.setTaskId(1L);

        Comment secondComment = new Comment();
        secondComment.setId(2L);
        secondComment.setTaskId(1L);

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

        User user = new User();
        user.setId(1L);
        user.setEmail("email");

        AddCommentRequestDto requestDto = new AddCommentRequestDto();
        requestDto.setTaskId(1L);
        requestDto.setText("text");

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setTaskId(requestDto.getTaskId());
        comment.setText(requestDto.getText());

        CommentDto expected = new CommentDto();
        expected.setId(comment.getId());
        expected.setTaskId(comment.getTaskId());
        expected.setText(comment.getText());

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

        Long commentId = 1L;

        User user = new User();
        user.setId(1L);
        user.setEmail("email");

        Comment comment = new Comment();
        comment.setId(commentId);
        comment.setUserId(user.getId());

        when(userRepository.getUserByEmail("email")).thenReturn(user);
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        doNothing().when(commentRepository).deleteById(commentId);
        commentService.deleteComment(commentId);

        verify(commentRepository, times(1)).deleteById(commentId);
    }
}
