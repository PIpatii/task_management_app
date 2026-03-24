package management.application.service;

import management.application.dto.task.CreateTaskRequestDto;
import management.application.dto.task.TaskDto;
import management.application.dto.task.UpdateTaskRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TaskService {
    Page<TaskDto> getTasks(Long projectId, Pageable pageable);

    TaskDto getTaskById(Long id);

    TaskDto createTask(CreateTaskRequestDto requestDto);

    TaskDto updateTask(UpdateTaskRequestDto requestDto, Long projectId);

    void deleteTask(Long id);
}
