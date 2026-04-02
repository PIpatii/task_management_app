package management.application.mapper;

import management.application.config.MapperConfig;
import management.application.dto.label.CreateLabelRequestDto;
import management.application.dto.label.LabelDto;
import management.application.dto.label.UpdateLabelRequestDto;
import management.application.model.Label;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface LabelMapper {
    LabelDto toDto(Label label);

    Label toEntity(CreateLabelRequestDto requestDto);

    void updateLabel(UpdateLabelRequestDto requestDto, @MappingTarget Label label);
}
