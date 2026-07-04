package com.neehar.taskflow.project;

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
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.neehar.taskflow.exception.ForbiddenException;
import com.neehar.taskflow.exception.NotFoundException;
import com.neehar.taskflow.project.dto.ProjectCreateRequest;
import com.neehar.taskflow.project.dto.ProjectResponse;
import com.neehar.taskflow.user.User;
import com.neehar.taskflow.user.UserRepository;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

	@Mock
    private ProjectRepository projectRepository;   // fake database for projects
	
	 @Mock
	    private UserRepository userRepository;         // fake database for users
	 
	 @InjectMocks
	    private ProjectService projectService;         // real service, both fakes injected
	 
	 
// -------------------------------getByID-------------------
	 @Test
	 void getById_returnsProject_whenUserOwnsIt() {
		  User owner = User.builder().id(1L).email("neehar@example.com").name("Neehar").build();
		  Project project= Project.builder().id(10L).name("TaskFlow").description("d").owner(owner).build();
		  when(projectRepository.findById(10L)).thenReturn(Optional.of(project));
		  
		  ProjectResponse response = projectService.getById(10L, "neehar@example.com");
		  
		  assertThat(response.id()).isEqualTo(10L);
	        assertThat(response.name()).isEqualTo("TaskFlow");
	        assertThat(response.ownerName()).isEqualTo("Neehar");
	 }
	 
	 @Test
	 void getById_throwsForbidden_whenUserDoesNotOwnIt() {
		 User owner = User.builder().id(1L).email("owner@example.com").name("Owner").build();
		 Project project = Project.builder().id(10L).name("TaskFlow").owner(owner).build();
	        when(projectRepository.findById(10L)).thenReturn(Optional.of(project));
	        assertThatThrownBy(() -> projectService.getById(10L, "intruder@example.com"))
            .isInstanceOf(ForbiddenException.class);
	 }
	 
	 @Test
	    void getById_throwsNotFound_whenProjectMissing() {
	        when(projectRepository.findById(99L)).thenReturn(Optional.empty());
	 
	        assertThatThrownBy(() -> projectService.getById(99L, "neehar@example.com"))
	                .isInstanceOf(NotFoundException.class);
}
	 
// --------------------------------delete-------------------------
	 @Test
	    void delete_removesProject_whenUserOwnsIt() {
	        User owner = User.builder().id(1L).email("neehar@example.com").name("Neehar").build();
	        Project project = Project.builder().id(10L).name("TaskFlow").owner(owner).build();
	        when(projectRepository.findById(10L)).thenReturn(Optional.of(project));
	 
	        projectService.delete(10L, "neehar@example.com");
	 
	        verify(projectRepository).delete(project);   // prove it actually deleted
	    }
	 
	 
	 @Test
	    void delete_throwsForbidden_andDeletesNothing_whenUserDoesNotOwnIt() {
	        User owner = User.builder().id(1L).email("owner@example.com").name("Owner").build();
	        Project project = Project.builder().id(10L).name("TaskFlow").owner(owner).build();
	        when(projectRepository.findById(10L)).thenReturn(Optional.of(project));
	 
	        assertThatThrownBy(() -> projectService.delete(10L, "intruder@example.com"))
	                .isInstanceOf(ForbiddenException.class);
	 
	        verify(projectRepository, never()).delete(any());   // the intruder deleted nothing
	    }
	 
	 @Test
	    void delete_throwsNotFound_whenProjectMissing() {
	        when(projectRepository.findById(99L)).thenReturn(Optional.empty());
	 
	        assertThatThrownBy(() -> projectService.delete(99L, "neehar@example.com"))
	        .isInstanceOf(NotFoundException.class);
	 }

// ---------------------Create-------------------------------------------
	 @Test
	 void create_savesAndReturnsProject() {
		   ProjectCreateRequest request = new ProjectCreateRequest("New Project", "Description");
		   User owner = User.builder().id(1L).email("neehar@example.com").name("Neehar").build();
		   Project saved = Project.builder().id(5L).name("New Project").description("Description").owner(owner).build();
		   when(userRepository.findByEmail("neehar@example.com")).thenReturn(Optional.of(owner));
	        when(projectRepository.save(any(Project.class))).thenReturn(saved);
	        ProjectResponse response = projectService.create(request, "neehar@example.com");
	        
	        assertThat(response.id()).isEqualTo(5L);
	        assertThat(response.name()).isEqualTo("New Project");
	        assertThat(response.ownerId()).isEqualTo(1L);
	        verify(projectRepository).save(any(Project.class));
	 }
	 
	 @Test
	    void create_throwsUsernameNotFound_whenUserMissing() {
	        ProjectCreateRequest request = new ProjectCreateRequest("New Project", "Description");
	        when(userRepository.findByEmail("ghost@example.com")).thenReturn(Optional.empty());
	 
	        assertThatThrownBy(() -> projectService.create(request, "ghost@example.com"))
	                .isInstanceOf(UsernameNotFoundException.class);
	 }
	 
	  // ---------- listMyProjects ----------
	    @Test
	 void listMyProjects_returnsOnlyTheUsersProjects() {
	        User owner = User.builder().id(1L).email("neehar@example.com").name("Neehar").build();
	        Project a = Project.builder().id(1L).name("Project A").owner(owner).build();
	        Project b = Project.builder().id(2L).name("Project B").owner(owner).build();
	 
	        when(userRepository.findByEmail("neehar@example.com")).thenReturn(Optional.of(owner));
	        when(projectRepository.findByOwner(owner)).thenReturn(List.of(a, b));
	 
	        List<ProjectResponse> result = projectService.listMyProjects("neehar@example.com");
	 
	        assertThat(result).hasSize(2);
	        assertThat(result).extracting(ProjectResponse::name)
	                .containsExactly("Project A", "Project B");
	    }
	 
}