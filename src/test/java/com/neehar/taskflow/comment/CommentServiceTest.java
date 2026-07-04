package com.neehar.taskflow.comment;

import com.neehar.taskflow.comment.dto.CommentCreateRequest;
import com.neehar.taskflow.comment.dto.CommentResponse;
import com.neehar.taskflow.common.Priority;
import com.neehar.taskflow.common.Status;
import com.neehar.taskflow.exception.ForbiddenException;
import com.neehar.taskflow.exception.NotFoundException;
import com.neehar.taskflow.project.Project;
import com.neehar.taskflow.task.Task;
import com.neehar.taskflow.task.TaskRepository;
import com.neehar.taskflow.user.User;
import com.neehar.taskflow.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock private CommentRepository commentRepository;
    @Mock private TaskRepository taskRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks private CommentService commentService;

    private static final String OWNER_EMAIL = "neehar@example.com";

    @Test
    void create_savesComment_whenUserOwnsTask() {
        Task task = taskOwnedByOwner();
        User author = owner();
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(userRepository.findByEmail(OWNER_EMAIL)).thenReturn(Optional.of(author));
        Comment saved = Comment.builder().id(100L).content("Looks good").task(task).author(author).build();
        when(commentRepository.save(any(Comment.class))).thenReturn(saved);

        CommentResponse response =
                commentService.create(1L, new CommentCreateRequest("Looks good"), OWNER_EMAIL);

        assertThat(response.id()).isEqualTo(100L);
        assertThat(response.content()).isEqualTo("Looks good");
        assertThat(response.taskId()).isEqualTo(1L);
        assertThat(response.authorName()).isEqualTo("Neehar");
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void create_throwsForbidden_whenUserDoesNotOwnTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(taskOwnedByOwner()));

        assertThatThrownBy(() ->
                commentService.create(1L, new CommentCreateRequest("hi"), "intruder@example.com"))
                .isInstanceOf(ForbiddenException.class);

        verify(commentRepository, never()).save(any());
    }

    @Test
    void create_throwsNotFound_whenTaskMissing() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                commentService.create(99L, new CommentCreateRequest("hi"), OWNER_EMAIL))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void listByTask_returnsComments_whenUserOwnsTask() {
        Task task = taskOwnedByOwner();
        User author = owner();
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(commentRepository.findByTaskIdOrderByCreatedAtAsc(1L)).thenReturn(List.of(
                Comment.builder().id(1L).content("first").task(task).author(author).build(),
                Comment.builder().id(2L).content("second").task(task).author(author).build()
        ));

        List<CommentResponse> result = commentService.listByTask(1L, OWNER_EMAIL);

        assertThat(result).extracting(CommentResponse::content).containsExactly("first", "second");
    }

    // ---------- fixtures: task -> project -> owner ----------

    private User owner() {
        return User.builder().id(1L).email(OWNER_EMAIL).name("Neehar").build();
    }

    private Task taskOwnedByOwner() {
        Project project = Project.builder().id(10L).name("TaskFlow").owner(owner()).build();
        return Task.builder().id(1L).title("Write tests").status(Status.TODO)
                .priority(Priority.HIGH).project(project).build();
    }
}