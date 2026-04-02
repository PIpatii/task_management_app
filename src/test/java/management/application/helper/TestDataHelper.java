package management.application.helper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import management.application.dto.attachment.AttachmentDto;
import management.application.dto.comment.AddCommentRequestDto;
import management.application.dto.comment.CommentDto;
import management.application.dto.label.CreateLabelRequestDto;
import management.application.dto.label.LabelDto;
import management.application.dto.label.UpdateLabelRequestDto;
import management.application.dto.project.CreateProjectRequestDto;
import management.application.dto.project.ProjectDto;
import management.application.dto.project.UpdateProjectRequestDto;
import management.application.dto.task.CreateTaskRequestDto;
import management.application.dto.task.TaskDto;
import management.application.dto.task.UpdateTaskRequestDto;
import management.application.dto.user.UpdateProfileRequestDto;
import management.application.dto.user.UpdateRoleRequestDto;
import management.application.dto.user.UserDto;
import management.application.dto.user.UserLoginRequestDto;
import management.application.dto.user.UserRegistrationRequestDto;
import management.application.dto.user.UserRegistrationResponseDto;
import management.application.model.Attachment;
import management.application.model.Comment;
import management.application.model.Label;
import management.application.model.Project;
import management.application.model.Role;
import management.application.model.Task;
import management.application.model.User;

public final class TestDataHelper {

    public TestDataHelper() {

    }

    public static Attachment createAttachment(Long id, Long taskId, String fileName, String dropboxFileId, LocalDateTime time, String contentType) {
        Attachment attachment = new Attachment();
        attachment.setId(id);
        attachment.setTaskId(taskId);
        attachment.setFileName(fileName);
        attachment.setDropboxFileId(dropboxFileId);
        attachment.setUploadTime(time);
        attachment.setContentType(contentType);

        return attachment;
    }

    public static AttachmentDto createAttachmentDto(Long id, Long taskId, String fileName, String dropboxFileId, LocalDateTime time, String contentType) {
        AttachmentDto attachmentDto = new AttachmentDto();
        attachmentDto.setId(id);
        attachmentDto.setTaskId(taskId);
        attachmentDto.setFileName(fileName);
        attachmentDto.setDropboxFileId(dropboxFileId);
        attachmentDto.setUploadTime(time);
        attachmentDto.setContentType(contentType);

        return attachmentDto;
    }

    public static Comment createComment(Long id, Long taskId, Long userId, String text, LocalDateTime time) {
        Comment comment = new Comment();
        comment.setId(id);
        comment.setTaskId(taskId);
        comment.setUserId(userId);
        comment.setText(text);
        comment.setTimestamp(time);

        return comment;
    }

    public static AddCommentRequestDto createAddCommentRequestDto(Long taskId, String text) {
        AddCommentRequestDto addCommentRequestDto = new AddCommentRequestDto();
        addCommentRequestDto.setTaskId(taskId);
        addCommentRequestDto.setText(text);

        return addCommentRequestDto;
    }

    public static CommentDto createCommentDto(Long id, Long taskId, Long userId, String text, LocalDateTime time) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(id);
        commentDto.setTaskId(taskId);
        commentDto.setUserId(userId);
        commentDto.setText(text);
        commentDto.setTimestamp(time);

        return commentDto;
    }

    public static Label createLabel(Long id, String name, String color) {
        Label label = new Label();
        label.setId(id);
        label.setName(name);
        label.setColor(color);

        return label;
    }

    public static CreateLabelRequestDto createCreateLabelRequestDto(String name, String color) {
        CreateLabelRequestDto createLabelRequestDto = new CreateLabelRequestDto();
        createLabelRequestDto.setName(name);
        createLabelRequestDto.setColor(color);

        return createLabelRequestDto;
    }

    public static LabelDto createLabelDto(Long id, String name, String color) {
        LabelDto labelDto = new LabelDto();
        labelDto.setId(id);
        labelDto.setName(name);
        labelDto.setColor(color);

        return labelDto;
    }

    public static UpdateLabelRequestDto createUpdateLabelRequestDto(String name, String color) {
        UpdateLabelRequestDto updateLabelRequestDto = new UpdateLabelRequestDto();
        updateLabelRequestDto.setName(name);
        updateLabelRequestDto.setColor(color);

        return updateLabelRequestDto;
    }

    public static Project createProject(Long id, String name, String description, LocalDate startDate, LocalDate endDate, Long assigneeId) {
        Project project = new Project();
        project.setId(id);
        project.setName(name);
        project.setDescription(description);
        project.setStartDate(startDate);
        project.setEndDate(endDate);
        project.setStatus(Project.Status.INITIATED);
        project.setAssigneeId(assigneeId);

        return project;
    }

    public static CreateProjectRequestDto  createCreateProjectRequestDto(String name, String description, LocalDate startDate, LocalDate endDate, Long assigneeId) {
        CreateProjectRequestDto createProjectRequestDto = new CreateProjectRequestDto();
        createProjectRequestDto.setName(name);
        createProjectRequestDto.setDescription(description);
        createProjectRequestDto.setStartDate(startDate);
        createProjectRequestDto.setEndDate(endDate);
        createProjectRequestDto.setAssigneeId(assigneeId);

        return createProjectRequestDto;
    }

    public static ProjectDto createProjectDto(Long id, String name, String description, LocalDate startDate, LocalDate endDate, String status, Long assigneeId) {
        ProjectDto projectDto = new ProjectDto();
        projectDto.setId(id);
        projectDto.setName(name);
        projectDto.setDescription(description);
        projectDto.setStartDate(startDate);
        projectDto.setEndDate(endDate);
        projectDto.setStatus(status);
        projectDto.setAssigneeId(assigneeId);

        return projectDto;
    }

    public static UpdateProjectRequestDto createUpdateProjectRequestDto(String status) {
        UpdateProjectRequestDto updateProjectRequestDto = new UpdateProjectRequestDto();
        updateProjectRequestDto.setStatus(status);

        return updateProjectRequestDto;
    }

    public static Task createTask(Long id, String name, String description, LocalDate deadline, Long projectId, Long assigneeId) {
        Task task = new Task();
        task.setId(id);
        task.setName(name);
        task.setDescription(description);
        task.setDeadline(deadline);
        task.setProjectId(projectId);
        task.setAssigneeId(assigneeId);
        task.setStatus(Task.Status.IN_PROGRESS);
        task.setPriority(Task.Priority.LOW);

        return task;
    }

    public static CreateTaskRequestDto  createCreateTaskRequestDto(String name, String description, LocalDate deadline, String priority, Long projectId, Long assigneeId, Set<Long> labels) {
        CreateTaskRequestDto createTaskRequestDto = new CreateTaskRequestDto();
        createTaskRequestDto.setName(name);
        createTaskRequestDto.setDescription(description);
        createTaskRequestDto.setDeadline(deadline);
        createTaskRequestDto.setPriority(priority);
        createTaskRequestDto.setProjectId(projectId);
        createTaskRequestDto.setAssigneeId(assigneeId);
        createTaskRequestDto.setLabelsIds(labels);

        return createTaskRequestDto;
    }

    public static TaskDto createTaskDto(Long id, String name, String description, String status, LocalDate deadline, String priority, Long projectId, Long assigneeId) {
        TaskDto taskDto = new TaskDto();
        taskDto.setId(id);
        taskDto.setName(name);
        taskDto.setDescription(description);
        taskDto.setDeadline(deadline);
        taskDto.setProjectId(projectId);
        taskDto.setAssigneeId(assigneeId);
        taskDto.setStatus(status);
        taskDto.setPriority(priority);

        return taskDto;
    }

    public static UpdateTaskRequestDto createUpdateTaskRequestDto(String status, String priority) {
        UpdateTaskRequestDto updateTaskRequestDto = new UpdateTaskRequestDto();
        updateTaskRequestDto.setStatus(status);
        updateTaskRequestDto.setPriority(priority);

        return updateTaskRequestDto;
    }

    public static UpdateProfileRequestDto  createUpdateProfileRequestDto(String email, String lastName, String firstName) {
        UpdateProfileRequestDto updateProfileRequestDto = new UpdateProfileRequestDto();
        updateProfileRequestDto.setEmail(email);
        updateProfileRequestDto.setLastName(lastName);
        updateProfileRequestDto.setFirstName(firstName);

        return updateProfileRequestDto;
    }

    public static UpdateRoleRequestDto  createUpdateRoleRequestDto(Set<Long> roleIds) {
        UpdateRoleRequestDto updateRoleRequestDto = new UpdateRoleRequestDto();
        updateRoleRequestDto.setRoleIds(roleIds);

        return updateRoleRequestDto;
    }

    public static UserLoginRequestDto  createUserLoginRequestDto(String email, String password) {
        UserLoginRequestDto userLoginRequestDto = new UserLoginRequestDto();
        userLoginRequestDto.setEmail(email);
        userLoginRequestDto.setPassword(password);

        return userLoginRequestDto;
    }

    public static UserRegistrationRequestDto createUserRegistrationRequestDto(String email, String password, String repeatPassword, String lastName, String firstName) {
        UserRegistrationRequestDto userRegistrationRequestDto = new UserRegistrationRequestDto();
        userRegistrationRequestDto.setEmail(email);
        userRegistrationRequestDto.setPassword(password);
        userRegistrationRequestDto.setRepeatPassword(repeatPassword);
        userRegistrationRequestDto.setLastName(lastName);
        userRegistrationRequestDto.setFirstName(firstName);

        return userRegistrationRequestDto;
    }

    public static UserRegistrationResponseDto createUserRegistrationResponseDto(Long id, String email, String firstName, String lastName) {
        UserRegistrationResponseDto userRegistrationResponseDto = new UserRegistrationResponseDto();
        userRegistrationResponseDto.setId(id);
        userRegistrationResponseDto.setEmail(email);
        userRegistrationResponseDto.setFirstName(firstName);
        userRegistrationResponseDto.setLastName(lastName);

        return userRegistrationResponseDto;
    }

    public static UserDto createUserDto(String email, String firstName, String lastName) {
        UserDto userDto = new UserDto();
        userDto.setEmail(email);
        userDto.setFirstName(firstName);
        userDto.setLastName(lastName);

        return userDto;
    }
    public static User createUser(Long id, String email, String password, String firstName, String lastName) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setPassword(password);
        user.setFirstName(firstName);
        user.setLastName(lastName);

        return user;
    }

    public static Role createRole(Long id, Role.RoleName  roleName) {
        Role role = new Role();
        role.setId(id);
        role.setName(roleName);

        return role;
    }
}
