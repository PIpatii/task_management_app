package management.application.service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import management.application.dto.task.CreateTaskRequestDto;
import management.application.dto.task.TaskDto;
import management.application.dto.task.UpdateTaskRequestDto;
import management.application.mapper.TaskMapper;
import management.application.model.Task;
import management.application.repository.TaskRepository;
import management.application.service.impl.TaskServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import static management.application.helper.TestDataHelper.createCreateTaskRequestDto;
import static management.application.helper.TestDataHelper.createTask;
import static management.application.helper.TestDataHelper.createTaskDto;
import static management.application.helper.TestDataHelper.createUpdateTaskRequestDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {
    @InjectMocks
    private TaskServiceImpl taskService;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private TaskMapper taskMapper;

    @Test
    @DisplayName("get all project's tasks")
    public void getTasks_success() {
        Task firstTask = createTask(1L, "task", "description",
                LocalDate.now().plusMonths(1), 1L, 1L);

        Task secondTask = createTask(2L, "task", "description",
                LocalDate.now().plusMonths(1), 1L, 1L);
        int expectedSize = 2;

        when(taskRepository.getTasksByProjectId(1L, PageRequest.of(0, 10)))
                .thenReturn(new PageImpl<>(Arrays.asList(firstTask, secondTask)));
        Page<TaskDto> tasks = taskService.getTasks(1L, PageRequest.of(0, 10));

        assertEquals(expectedSize, tasks.getContent().size());
    }

    @Test
    @DisplayName("get a task by id")
    public void getTaskById_success() {
        Task task = createTask(1L, "task", "description",
                LocalDate.now().plusMonths(1), 1L, 1L);

        TaskDto expected = createTaskDto(task.getId(), task.getName(), task.getDescription(),
                task.getStatus().name(), task.getDeadline(), task.getPriority().name(),
                task.getProjectId(), task.getAssigneeId());

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskMapper.toDto(task)).thenReturn(expected);

        TaskDto actual = taskService.getTaskById(1L);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getProjectId(), actual.getProjectId());
        assertEquals(expected.getName(), actual.getName());
    }

    @Test
    @DisplayName("create a task")
    public void createTask_success() {
        CreateTaskRequestDto requestDto = createCreateTaskRequestDto("task", "description",
                LocalDate.now().plusMonths(1), "LOW",1L, 1L, Set.of(1L));

        Task task = createTask(1L, requestDto.getName(), requestDto.getDescription(),
                requestDto.getDeadline(), requestDto.getProjectId(), requestDto.getAssigneeId());

        TaskDto expected = createTaskDto(task.getId(), task.getName(), task.getDescription(),
                task.getStatus().name(), task.getDeadline(), task.getPriority().name(),
                task.getProjectId(), task.getAssigneeId());

        when(taskMapper.toEntity(requestDto)).thenReturn(task);
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.toDto(task)).thenReturn(expected);

        TaskDto actual = taskService.createTask(requestDto);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getProjectId(), actual.getProjectId());
    }

    @Test
    @DisplayName("update a task by id")
    public void updateTask_success() {
        UpdateTaskRequestDto requestDto = createUpdateTaskRequestDto("IN_PROGRESS", "LOW");

        Task task = createTask(1L,"task", "description",
                LocalDate.now().plusMonths(1),1L, 1L);

        TaskDto expected = createTaskDto(task.getId(), task.getName(), task.getDescription(),
                task.getStatus().name(), task.getDeadline(), task.getPriority().name(),
                task.getProjectId(), task.getAssigneeId());

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        doAnswer(invocation -> {
            UpdateTaskRequestDto dto = invocation.getArgument(0);
            Task task1 = invocation.getArgument(1);

            task1.setPriority(Task.Priority.valueOf(dto.getPriority()));
            task1.setStatus(Task.Status.valueOf(dto.getStatus()));

            return null;
        }).when(taskMapper).updateTask(any(UpdateTaskRequestDto.class), any(Task.class));
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.toDto(task)).thenReturn(expected);
        TaskDto actual = taskService.updateTask(requestDto, 1L);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getStatus(), actual.getStatus());
        assertEquals(expected.getProjectId(), actual.getProjectId());
    }

    @Test
    @DisplayName("delete a task by id")
    public void deleteTask_success() {
        Long taskId = 1L;

        doNothing().when(taskRepository).deleteById(taskId);
        taskService.deleteTask(taskId);

        verify(taskRepository, times(1)).deleteById(taskId);
    }
}
