package com.neehar.taskflow.task;


import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.validation.Valid;
import com.neehar.taskflow.task.dto.TaskCreateRequest;
import com.neehar.taskflow.task.dto.TaskResponse;
import com.neehar.taskflow.task.dto.TaskStatusUpdateRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class TaskController {

	private final TaskService taskService;
	
	@PostMapping("/api/projects/{projectId}/tasks")
	public ResponseEntity<TaskResponse> create(@PathVariable Long projectId,@Valid @RequestBody TaskCreateRequest request,@AuthenticationPrincipal UserDetails currentUser){
		TaskResponse response=taskService.create(projectId, request, currentUser.getUsername());
		return ResponseEntity.status(201).body(response);
	}
	
	@GetMapping("api/projects/{projectId}/tasks")
	public ResponseEntity<List<TaskResponse>> listByProject( @PathVariable Long projectId,
            @AuthenticationPrincipal UserDetails currentUser){
		return ResponseEntity.ok(taskService.listByProject(projectId, currentUser.getUsername()));
	}
	
	 @GetMapping("/api/tasks/{id}")
	    public ResponseEntity<TaskResponse> getById(
	            @PathVariable Long id,
	            @AuthenticationPrincipal UserDetails currentUser
	    ) {
	        return ResponseEntity.ok(taskService.getById(id, currentUser.getUsername()));
	    }
	 
	  @PatchMapping("/api/tasks/{id}/status")
	    public ResponseEntity<TaskResponse> updateStatus(
	            @PathVariable Long id,
	            @Valid @RequestBody TaskStatusUpdateRequest request,
	            @AuthenticationPrincipal UserDetails currentUser
	    ) {
	        return ResponseEntity.ok(taskService.updateStatus(id, request, currentUser.getUsername()));
	    }
	  @DeleteMapping("/api/tasks/{id}")
	    public ResponseEntity<Void> delete(
	            @PathVariable Long id,
	            @AuthenticationPrincipal UserDetails currentUser
	    ) {
	        taskService.delete(id, currentUser.getUsername());
	        return ResponseEntity.noContent().build();
	  }
}
