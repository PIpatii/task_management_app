package management.application.mapper;

import management.application.config.MapperConfig;
import management.application.dto.project.CreateProjectRequestDto;
import management.application.dto.project.ProjectDto;
import management.application.dto.project.UpdateProjectRequestDto;
import management.application.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface ProjectMapper {
    Project toEntity(CreateProjectRequestDto requestDto);

    ProjectDto toDto(Project project);

    void updateProject(UpdateProjectRequestDto requestDto, @MappingTarget Project project);
}
