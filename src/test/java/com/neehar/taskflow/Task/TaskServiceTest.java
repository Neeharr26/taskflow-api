package com.neehar.taskflow.Task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import com.neehar.taskflow.common.Priority;
import com.neehar.taskflow.common.Status;
import com.neehar.taskflow.exception.ForbiddenException;
import com.neehar.taskflow.exception.NotFoundException;
import com.neehar.taskflow.project.Project;
import com.neehar.taskflow.project.ProjectRepository;
import com.neehar.taskflow.task.Task;
import com.neehar.taskflow.task.TaskRepository;
import com.neehar.taskflow.task.TaskService;
import com.neehar.taskflow.task.dto.TaskCreateRequest;
import com.neehar.taskflow.task.dto.TaskResponse;
import com.neehar.taskflow.task.dto.TaskStatusUpdateRequest;
import com.neehar.taskflow.user.User;
import com.neehar.taskflow.user.UserRepository;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

	 @Mock private TaskRepository taskRepository;
	    @Mock private ProjectRepository projectRepository;
	    @Mock private UserRepository userRepository;
	    
	    @InjectMocks private TaskService taskService;
	    
	    private static final String OWNER_EMAIL="neehar@example.com";
	    
	    // ------------getById----------
	    @Test
	    void getById_returnsTask_whenUserOwnParentProject() {
	        when(taskRepository.findById(1L)).thenReturn(Optional.of(taskInOwnedProject()));

	         TaskResponse response = taskService.getById(1L, OWNER_EMAIL);
	         
	         assertThat(response.id()).isEqualTo(1L);
	         assertThat(response.title()).isEqualTo("Write tests");

	    }
	    
	    @Test
	    void getById_throwsForbidden_whenUserDoesNotOwnParentProject() {
	        when(taskRepository.findById(1L)).thenReturn(Optional.of(taskInOwnedProject()));
	 
	        assertThatThrownBy(() -> taskService.getById(1L, "intruder@example.com"))
	                .isInstanceOf(ForbiddenException.class);
	    }
	 
	    @Test
	    void getById_throwsNotFound_whenTaskMissing() {
	        when(taskRepository.findById(99L)).thenReturn(Optional.empty());
	 
	        assertThatThrownBy(() -> taskService.getById(99L, OWNER_EMAIL))
	                .isInstanceOf(NotFoundException.class);
	    }
	 
	    // ---------- create ----------
	 
	    @Test
	    void create_savesTask_whenUserOwnsProjectAndNoAssignee() {
	        Project project = ownedProject();
	        when(projectRepository.findById(10L)).thenReturn(Optional.of(project));
	        Task saved = Task.builder().id(2L).title("New task").status(Status.TODO)
	                .priority(Priority.MEDIUM).project(project).build();
	        when(taskRepository.save(any(Task.class))).thenReturn(saved);
	 
	        TaskCreateRequest request =
	                new TaskCreateRequest("New task", "desc", Priority.MEDIUM, null, null); // assigneeId = null
	 
	        TaskResponse response = taskService.create(10L, request, OWNER_EMAIL);
	 
	        assertThat(response.id()).isEqualTo(2L);
	        assertThat(response.title()).isEqualTo("New task");
	        verify(taskRepository).save(any(Task.class));
	    }
	 
	    @Test
	    void create_throwsForbidden_whenUserDoesNotOwnProject() {
	        when(projectRepository.findById(10L)).thenReturn(Optional.of(ownedProject()));
	        TaskCreateRequest request =
	                new TaskCreateRequest("New task", "desc", Priority.MEDIUM, null, null);
	 
	        assertThatThrownBy(() -> taskService.create(10L, request, "intruder@example.com"))
	                .isInstanceOf(ForbiddenException.class);
	 
	        verify(taskRepository, never()).save(any());
	    }
	 
	    @Test
	    void create_throwsNotFound_whenAssigneeMissing() {
	        when(projectRepository.findById(10L)).thenReturn(Optional.of(ownedProject()));
	        when(userRepository.findById(77L)).thenReturn(Optional.empty()); // assignee lookup fails
	        TaskCreateRequest request =
	                new TaskCreateRequest("New task", "desc", Priority.MEDIUM, null, 77L); // assigneeId = 77
	 
	        assertThatThrownBy(() -> taskService.create(10L, request, OWNER_EMAIL))
	                .isInstanceOf(NotFoundException.class);
	    }
	 
	    // ---------- updateStatus ----------
	 
	    @Test
	    void updateStatus_changesStatus_whenUserOwnsTask() {
	        Task task = taskInOwnedProject(); // starts as TODO
	        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
	        TaskStatusUpdateRequest request = new TaskStatusUpdateRequest(Status.IN_PROGRESS);
	 
	        TaskResponse response = taskService.updateStatus(1L, request, OWNER_EMAIL);
	 
	        assertThat(response.status()).isEqualTo(Status.IN_PROGRESS);
	        assertThat(task.getStatus()).isEqualTo(Status.IN_PROGRESS); // the entity was mutated in place
	    }
	 
	    // ---------- delete ----------
	 
	    @Test
	    void delete_removesTask_whenUserOwnsIt() {
	        Task task = taskInOwnedProject();
	        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
	 
	        taskService.delete(1L, OWNER_EMAIL);
	 
	        verify(taskRepository).delete(task);
	    }
	 
	    @Test
	    void delete_throwsForbidden_andDeletesNothing_whenUserDoesNotOwnIt() {
	        when(taskRepository.findById(1L)).thenReturn(Optional.of(taskInOwnedProject()));
	 
	        assertThatThrownBy(() -> taskService.delete(1L, "intruder@example.com"))
	                .isInstanceOf(ForbiddenException.class);
	 
	        verify(taskRepository, never()).delete(any());
	    }
	 
	    // ---------- listByProject ----------
	 
	    @Test
	    void listByProject_returnsTasks_whenUserOwnsProject() {
	        Project project = ownedProject();
	        when(projectRepository.findById(10L)).thenReturn(Optional.of(project));
	        when(taskRepository.findByProjectId(10L)).thenReturn(List.of(
	                Task.builder().id(1L).title("A").status(Status.TODO).priority(Priority.LOW).project(project).build(),
	                Task.builder().id(2L).title("B").status(Status.TODO).priority(Priority.LOW).project(project).build()
	        ));
	 
	        List<TaskResponse> result = taskService.listByProject(10L, OWNER_EMAIL);
	 
	        assertThat(result).extracting(TaskResponse::title).containsExactly("A", "B");
	    }
	 
	    // ---------- fixtures: the owner -> project -> task graph ----------
	    
	    
	    private User owner() {
	        return User.builder().id(1L).email(OWNER_EMAIL).name("Neehar").build();
	    }
	    private Project ownedProject() {
	        return Project.builder().id(10L).name("TaskFlow").owner(owner()).build();
	    }
	    private Task taskInOwnedProject() {
	        return Task.builder().id(1L).title("Write tests").status(Status.TODO)
	                .priority(Priority.HIGH).project(ownedProject()).build();
	    }
	    
}
 