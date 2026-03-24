package management.application.mapper;

import management.application.config.MapperConfig;
import management.application.dto.comment.AddCommentRequestDto;
import management.application.dto.comment.CommentDto;
import management.application.model.Comment;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface CommentMapper {
    CommentDto toDto(Comment comment);

    Comment toEntity(AddCommentRequestDto requestDto);
}
