package management.application.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import management.application.dto.project.CreateProjectRequestDto;
import management.application.dto.project.ProjectDto;
import management.application.dto.project.UpdateProjectRequestDto;
import management.application.service.ProjectService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "project", description = "projects controller")
@RestController
@RequiredArgsConstructor
@RequestMapping("/projects")
public class ProjectController {
    private final ProjectService projectService;

    @PreAuthorize("hasRole('USER')")
    @GetMapping
    @Operation(summary = "get projects", description = "get user's projects")
    public Page<ProjectDto> getProjects(Pageable pageable) {
        return projectService.getProjects(pageable);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{id}")
    @Operation(summary = "get project by id", description = "get a project by id")
    public ProjectDto getProjectById(@PathVariable Long id) {
        return projectService.getProjectById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @Operation(summary = "create project", description = "create a project")
    public ProjectDto createProject(@RequestBody @Valid CreateProjectRequestDto requestDto) {
        return projectService.createProject(requestDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    @Operation(summary = "update project", description = "update a project by id")
    public ProjectDto updateProject(@RequestBody @Valid UpdateProjectRequestDto requestDto,
                                    @PathVariable Long id) {
        return projectService.updateProject(requestDto, id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "delete project", description = "delete a project by id")
    public void deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
    }
}
