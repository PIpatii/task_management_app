package management.application.service;

import management.application.dto.project.CreateProjectRequestDto;
import management.application.dto.project.ProjectDto;
import management.application.dto.project.UpdateProjectRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProjectService {
    Page<ProjectDto> getProjects(Pageable pageable);

    ProjectDto getProjectById(Long projectId);

    ProjectDto createProject(CreateProjectRequestDto requestDto);

    ProjectDto updateProject(UpdateProjectRequestDto requestDto, Long projectId);

    void deleteProject(Long projectId);

}
