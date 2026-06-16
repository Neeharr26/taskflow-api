package com.neehar.taskflow.project;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.neehar.taskflow.project.dto.ProjectCreateRequest;
import com.neehar.taskflow.project.dto.ProjectResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

	 private final ProjectService projectService;
	 
	 @PostMapping
	    public ResponseEntity<ProjectResponse> create(
	            @Valid @RequestBody ProjectCreateRequest request,
	            @AuthenticationPrincipal UserDetails currentUser
	    ) {
	        ProjectResponse response = projectService.create(request, currentUser.getUsername());
	        return ResponseEntity.status(201).body(response);
	    }
	 
	 @GetMapping
	    public ResponseEntity<List<ProjectResponse>> listMyProjects(
	            @AuthenticationPrincipal UserDetails currentUser
	    ) {
	        return ResponseEntity.ok(projectService.listMyProjects(currentUser.getUsername()));
	    }
	 
	 @GetMapping("/{id}")
	    public ResponseEntity<ProjectResponse> getById(
	            @PathVariable Long id,
	            @AuthenticationPrincipal UserDetails currentUser
	    ) {
	        return ResponseEntity.ok(projectService.getById(id, currentUser.getUsername()));
	    }
	 
	   @DeleteMapping("/{id}")
	    public ResponseEntity<Void> delete(
	            @PathVariable Long id,
	            @AuthenticationPrincipal UserDetails currentUser
	    ) {
	        projectService.delete(id, currentUser.getUsername());
	        return ResponseEntity.noContent().build();
	    }
}
