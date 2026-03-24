package management.application.service;

import java.util.Arrays;
import java.util.Optional;
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
        Task firstTask = new Task();
        firstTask.setId(1L);
        firstTask.setProjectId(1L);

        Task secondTask = new Task();
        secondTask.setId(2L);
        secondTask.setProjectId(1L);

        int expectedSize = 2;

        when(taskRepository.getTasksByProjectId(1L, PageRequest.of(0, 10)))
                .thenReturn(new PageImpl<>(Arrays.asList(firstTask, secondTask)));
        Page<TaskDto> tasks = taskService.getTasks(1L, PageRequest.of(0, 10));

        assertEquals(expectedSize, tasks.getContent().size());
    }

    @Test
    @DisplayName("get a task by id")
    public void getTaskById_success() {
        Task task = new Task();
        task.setId(1L);
        task.setName("task");
        task.setProjectId(1L);

        TaskDto expected = new TaskDto();
        expected.setId(expected.getId());
        expected.setName("task");
        expected.setProjectId(expected.getProjectId());

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
        CreateTaskRequestDto requestDto = new CreateTaskRequestDto();
        requestDto.setProjectId(1L);
        requestDto.setName("task");

        Task task = new Task();
        task.setId(1L);
        task.setName(requestDto.getName());
        task.setProjectId(requestDto.getProjectId());

        TaskDto expected = new TaskDto();
        expected.setId(task.getId());
        expected.setName(task.getName());
        expected.setProjectId(task.getProjectId());
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
        UpdateTaskRequestDto requestDto = new UpdateTaskRequestDto();
        requestDto.setStatus("IN_PROGRESS");
        requestDto.setPriority("HIGH");

        Task task = new Task();
        task.setId(1L);
        task.setStatus(Task.Status.valueOf(requestDto.getStatus()));
        task.setPriority(Task.Priority.valueOf(requestDto.getPriority()));

        TaskDto expected = new TaskDto();
        expected.setId(task.getId());
        expected.setStatus(task.getStatus().name());
        expected.setPriority(task.getPriority().name());

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
