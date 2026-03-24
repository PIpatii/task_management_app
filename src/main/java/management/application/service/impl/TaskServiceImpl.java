package management.application.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import management.application.dto.task.CreateTaskRequestDto;
import management.application.dto.task.TaskDto;
import management.application.dto.task.UpdateTaskRequestDto;
import management.application.mapper.TaskMapper;
import management.application.model.Task;
import management.application.repository.TaskRepository;
import management.application.service.TaskService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    @Override
    public Page<TaskDto> getTasks(Long projectId, Pageable pageable) {
        Page<Task> tasks = taskRepository.getTasksByProjectId(projectId, pageable);

        return tasks.map(taskMapper::toDto);
    }

    @Override
    public TaskDto getTaskById(Long id) {
        return taskMapper.toDto(taskRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Task with id " + id + " not found!")));
    }

    @Override
    public TaskDto createTask(CreateTaskRequestDto requestDto) {
        Task task = taskMapper.toEntity(requestDto);
        task.setStatus(Task.Status.NOT_STARTED);

        return taskMapper.toDto(taskRepository.save(task));
    }

    @Override
    public TaskDto updateTask(UpdateTaskRequestDto requestDto, Long projectId) {
        Task task = taskRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));
        taskMapper.updateTask(requestDto, task);

        return taskMapper.toDto(taskRepository.save(task));
    }

    @Override
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }
}
