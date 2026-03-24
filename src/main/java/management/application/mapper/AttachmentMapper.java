package management.application.mapper;

import management.application.config.MapperConfig;
import management.application.dto.attachment.AttachmentDto;
import management.application.model.Attachment;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface AttachmentMapper {
    AttachmentDto toDto(Attachment attachment);
}
