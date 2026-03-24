package management.application.service.impl;

import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import management.application.dto.project.CreateProjectRequestDto;
import management.application.dto.project.ProjectDto;
import management.application.dto.project.UpdateProjectRequestDto;
import management.application.mapper.ProjectMapper;
import management.application.model.Project;
import management.application.model.User;
import management.application.repository.ProjectRepository;
import management.application.repository.UserRepository;
import management.application.service.ProjectService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectMapper projectMapper;

    @Override
    public Page<ProjectDto> getProjects(Pageable pageable) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.getUserByEmail(username);

        Page<Project> projects = projectRepository.getProjectsByAssigneeId(user.getId(), pageable);
        return projects.map(projectMapper::toDto);
    }

    @Override
    public ProjectDto getProjectById(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NoSuchElementException("Project not found"));

        return projectMapper.toDto(project);
    }

    @Override
    public ProjectDto createProject(CreateProjectRequestDto requestDto) {
        Project project = projectMapper.toEntity(requestDto);
        project.setStatus(Project.Status.INITIATED);

        return projectMapper.toDto(projectRepository.save(project));
    }

    @Override
    public ProjectDto updateProject(UpdateProjectRequestDto requestDto, Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NoSuchElementException("Project not found"));

        projectMapper.updateProject(requestDto, project);

        return projectMapper.toDto(projectRepository.save(project));
    }

    @Override
    public void deleteProject(Long projectId) {
        projectRepository.deleteById(projectId);
    }
}
