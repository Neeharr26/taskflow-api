package com.neehar.taskflow.comment;

import com.neehar.taskflow.comment.dto.CommentCreateRequest;
import com.neehar.taskflow.comment.dto.CommentResponse;
import com.neehar.taskflow.exception.ForbiddenException;
import com.neehar.taskflow.exception.NotFoundException;
import com.neehar.taskflow.task.Task;
import com.neehar.taskflow.task.TaskRepository;
import com.neehar.taskflow.user.User;
import com.neehar.taskflow.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Transactional
    public CommentResponse create(Long taskId, CommentCreateRequest request, String currentUserEmail) {
        Task task = loadTaskWithOwnershipCheck(taskId, currentUserEmail);
        User author = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + currentUserEmail));

        Comment comment = Comment.builder()
                .content(request.content())
                .task(task)
                .author(author)
                .build();

        return CommentResponse.fromEntity(commentRepository.save(comment));
    }

    public List<CommentResponse> listByTask(Long taskId, String currentUserEmail) {
        loadTaskWithOwnershipCheck(taskId, currentUserEmail);

        return commentRepository.findByTaskIdOrderByCreatedAtAsc(taskId).stream()
                .map(CommentResponse::fromEntity)
                .toList();
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