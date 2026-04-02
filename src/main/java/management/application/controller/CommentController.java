package management.application.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import management.application.dto.comment.AddCommentRequestDto;
import management.application.dto.comment.CommentDto;
import management.application.service.CommentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "comment", description = "comments controller")
@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {
    private final CommentService commentService;

    @PreAuthorize("hasRole('USER')")
    @GetMapping
    @Operation(summary = "Get comments", description = "Get all comments for a task by task id")
    public Page<CommentDto> getComments(@RequestParam Long taskId, Pageable pageable) {
        return commentService.getComments(taskId, pageable);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    @Operation(summary = "add comments", description = "add comments to a task")
    public CommentDto addComment(@RequestBody @Valid AddCommentRequestDto comment) {
        return commentService.addComment(comment);
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "delete comments", description = "delete a comments by id")
    public void deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
    }
}
