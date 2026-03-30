package management.application.service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;
import management.application.dto.project.CreateProjectRequestDto;
import management.application.dto.project.ProjectDto;
import management.application.dto.project.UpdateProjectRequestDto;
import management.application.mapper.ProjectMapper;
import management.application.model.Project;
import management.application.model.User;
import management.application.repository.ProjectRepository;
import management.application.repository.UserRepository;
import management.application.service.impl.ProjectServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import static management.application.helper.TestDataHelper.createCreateProjectRequestDto;
import static management.application.helper.TestDataHelper.createProject;
import static management.application.helper.TestDataHelper.createProjectDto;
import static management.application.helper.TestDataHelper.createUpdateProjectRequestDto;
import static management.application.helper.TestDataHelper.createUser;
import static management.application.helper.TestSecurityUtils.mockAuth;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {
    @InjectMocks
    private ProjectServiceImpl projectService;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ProjectMapper projectMapper;

    @Test
    @DisplayName("get all user's projects")
    public void getProjects_success() {
        mockAuth("email");

        User user = createUser(1L, "email", "password",
                "firstName", "lastName");

        Project firstProject = createProject(1L, "First project", "description", LocalDate.now(),
                                LocalDate.now().plusMonths(1), 1L);

        Project secondProject = createProject(2L, "Second project", "description", LocalDate.now(),
                LocalDate.now().plusMonths(1), 1L);

        int expectedSize = 2;

        when(userRepository.getUserByEmail("email")).thenReturn(user);
        when(projectRepository.getProjectsByAssigneeId(1L, PageRequest.of(0,10)))
                .thenReturn(new PageImpl<>(Arrays.asList(firstProject, secondProject)));
        Page<ProjectDto> expected = projectService.getProjects(PageRequest.of(0,10));

        assertEquals(expectedSize, expected.getContent().size());
    }

    @Test
    @DisplayName("get a project by id")
    public void getProjectById_success() {
        Project project = createProject(1L, "First project", "description", LocalDate.now(),
                LocalDate.now().plusMonths(1), 1L);

        ProjectDto expected = createProjectDto(project.getId(), project.getName(), project.getDescription(),
                project.getStartDate(), project.getEndDate(), project.getStatus().name(), project.getAssigneeId());

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectMapper.toDto(project)).thenReturn(expected);

        ProjectDto actual = projectService.getProjectById(1L);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
    }

    @Test
    @DisplayName("create a project")
    public void createProject_success() {
        CreateProjectRequestDto requestDto = createCreateProjectRequestDto("project", "description",
                LocalDate.now(), LocalDate.now().plusMonths(1), 1L);

        Project project = createProject(1L, requestDto.getName(), requestDto.getDescription(),
                requestDto.getStartDate(), requestDto.getEndDate(), requestDto.getAssigneeId());

        ProjectDto expected = createProjectDto(project.getId(), project.getName(), project.getDescription(),
                project.getStartDate(), project.getEndDate(), project.getStatus().name(), project.getAssigneeId());

        when(projectMapper.toEntity(requestDto)).thenReturn(project);
        when(projectRepository.save(project)).thenReturn(project);
        when(projectMapper.toDto(project)).thenReturn(expected);

        ProjectDto actual = projectService.createProject(requestDto);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getDescription(), actual.getDescription());
    }

    @Test
    @DisplayName("update a project by id")
    public void updateProject_success() {
        UpdateProjectRequestDto requestDto = createUpdateProjectRequestDto("INITIATED");

        Project project = createProject(1L, "project", "description",
                LocalDate.now(), LocalDate.now().plusMonths(1), 1L);

        ProjectDto expected = createProjectDto(project.getId(), project.getName(), project.getDescription(),
                project.getStartDate(), project.getEndDate(), project.getStatus().name(), project.getAssigneeId());

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        doAnswer(invocation -> {
            UpdateProjectRequestDto dto = invocation.getArgument(0);
            Project proj =  invocation.getArgument(1);

            proj.setStatus(Project.Status.valueOf(dto.getStatus()));

            return null;
        }).when(projectMapper).updateProject(any(UpdateProjectRequestDto.class), any(Project.class));
        when(projectRepository.save(project)).thenReturn(project);
        when(projectMapper.toDto(project)).thenReturn(expected);
        ProjectDto actual = projectService.updateProject(requestDto, 1L);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getStatus(), actual.getStatus());
    }

    @Test
    @DisplayName("delete a project by id")
    public void deleteProject_success() {
        Long projectId = 1L;

        doNothing().when(projectRepository).deleteById(projectId);
        projectService.deleteProject(projectId);

        verify(projectRepository, times(1)).deleteById(projectId);
    }
}
