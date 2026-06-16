package com.neehar.taskflow.task;

import com.neehar.taskflow.common.Status;
import com.neehar.taskflow.exception.ForbiddenException;
import com.neehar.taskflow.exception.NotFoundException;
import com.neehar.taskflow.project.Project;
import com.neehar.taskflow.project.ProjectRepository;
import com.neehar.taskflow.task.dto.TaskCreateRequest;
import com.neehar.taskflow.task.dto.TaskResponse;
import com.neehar.taskflow.task.dto.TaskStatusUpdateRequest;
import com.neehar.taskflow.user.User;
import com.neehar.taskflow.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    @Transactional
    public TaskResponse create(Long projectId, TaskCreateRequest request, String currentUserEmail) {
        Project project = loadProjectWithOwnershipCheck(projectId, currentUserEmail);

        User assignee = null;
        if (request.assigneeId() != null) {
            assignee = userRepository.findById(request.assigneeId())
                    .orElseThrow(() -> new NotFoundException("Assignee not found: " + request.assigneeId()));
        }

        Task task = Task.builder()
                .title(request.title())
                .description(request.description())
                .status(Status.TODO)               // always start in TODO
                .priority(request.priority())
                .dueDate(request.dueDate())
                .project(project)
                .assignee(assignee)
                .build();

        return TaskResponse.fromEntity(taskRepository.save(task));
    }

    public List<TaskResponse> listByProject(Long projectId, String currentUserEmail) {
        loadProjectWithOwnershipCheck(projectId, currentUserEmail);

        return taskRepository.findByProjectId(projectId).stream()
                .map(TaskResponse::fromEntity)
                .toList();
    }

    public TaskResponse getById(Long taskId, String currentUserEmail) {
        Task task = loadTaskWithOwnershipCheck(taskId, currentUserEmail);
        return TaskResponse.fromEntity(task);
    }

    @Transactional
    public TaskResponse updateStatus(Long taskId, TaskStatusUpdateRequest request, String currentUserEmail) {
        Task task = loadTaskWithOwnershipCheck(taskId, currentUserEmail);

        task.setStatus(request.status());
        // No need to call save() — Hibernate auto-detects changes in a transaction (dirty checking).
        return TaskResponse.fromEntity(task);
    }

    @Transactional
    public void delete(Long taskId, String currentUserEmail) {
        Task task = loadTaskWithOwnershipCheck(taskId, currentUserEmail);
        taskRepository.delete(task);
    }

    // --- helpers ---

    private Project loadProjectWithOwnershipCheck(Long projectId, String email) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException("Project not found: " + projectId));

        if (!project.getOwner().getEmail().equals(email)) {
            throw new ForbiddenException("You don't have access to this project");
        }
        return project;
    }

    private Task loadTaskWithOwnershipCheck(Long taskId, String email) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("Task not found: " + taskId));

        if (!task.getProject().getOwner().getEmail().equals(email)) {
            throw new ForbiddenException("You don't have access to this task");
        }
        return task;
    }
}