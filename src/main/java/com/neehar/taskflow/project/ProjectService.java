package com.neehar.taskflow.project;

import java.util.List;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neehar.taskflow.exception.ForbiddenException;
import com.neehar.taskflow.exception.NotFoundException;
import com.neehar.taskflow.project.dto.ProjectCreateRequest;
import com.neehar.taskflow.project.dto.ProjectResponse;
import com.neehar.taskflow.user.User;
import com.neehar.taskflow.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectService {

	 private final ProjectRepository projectRepository;
	    private final UserRepository userRepository;
	    
	    @Transactional
	    public ProjectResponse create(ProjectCreateRequest request, String currentUserEmail) {
	        User owner = loadCurrentUser(currentUserEmail);

	        Project project = Project.builder()
	                .name(request.name())
	                .description(request.description())
	                .owner(owner)
	                .build();

	        return ProjectResponse.fromEntity(projectRepository.save(project));
	    }
	    
	    public List<ProjectResponse> listMyProjects(String currentUserEmail) {
	        User currentUser = loadCurrentUser(currentUserEmail);
	        return projectRepository.findByOwner(currentUser).stream()
	                .map(ProjectResponse::fromEntity)
	                .toList();
	    }
	    
	    public ProjectResponse getById(Long projectId, String currentUserEmail) {
	        Project project = projectRepository.findById(projectId)
	                .orElseThrow(() -> new NotFoundException("Project not found: " + projectId));

	        checkOwnership(project, currentUserEmail);

	        return ProjectResponse.fromEntity(project);
	    }
	    
	    @Transactional
	    public void delete(Long projectId, String currentUserEmail) {
	        Project project = projectRepository.findById(projectId)
	                .orElseThrow(() -> new NotFoundException("Project not found: " + projectId));

	        checkOwnership(project, currentUserEmail);

	        projectRepository.delete(project);
	    }
	    
	    // helpers
	    private User loadCurrentUser(String email) {
	        return userRepository.findByEmail(email)
	                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
	    }
	    private void checkOwnership(Project project, String currentUserEmail) {
	        if (!project.getOwner().getEmail().equals(currentUserEmail)) {
	            throw new ForbiddenException("You don't have access to this project");
	        }
	    }
}
