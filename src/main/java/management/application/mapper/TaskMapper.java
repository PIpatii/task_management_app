package management.application.mapper;

import java.util.Set;
import java.util.stream.Collectors;
import management.application.config.MapperConfig;
import management.application.dto.task.CreateTaskRequestDto;
import management.application.dto.task.TaskDto;
import management.application.dto.task.UpdateTaskRequestDto;
import management.application.model.Label;
import management.application.model.Task;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface TaskMapper {

    @Mapping(target = "labelsIds", ignore = true)
    TaskDto toDto(Task task);

    @Mapping(target = "labels", ignore = true)
    Task toEntity(CreateTaskRequestDto requestDto);

    void updateTask(UpdateTaskRequestDto requestDto, @MappingTarget Task task);

    @AfterMapping
    default void setLabelIds(@MappingTarget TaskDto taskDto, Task task) {
        Set<Long> labelIds = task.getLabels().stream()
                .map(Label::getId)
                .collect(Collectors.toSet());

        taskDto.setLabelsIds(labelIds);
    }

    @AfterMapping
    default void setCategory(@MappingTarget Task task, CreateTaskRequestDto requestDto) {
        Set<Label> labels = requestDto.getLabelsIds().stream()
                .map(Label::new)
                .collect(Collectors.toSet());

        task.setLabels(labels);
    }
}
