package com.neehar.taskflow.project.dto;

import java.time.LocalDateTime;

import com.neehar.taskflow.project.Project;

public record ProjectResponse(
		Long id,
        String name,
        String description,
        Long ownerId,
        String ownerName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
    
	public static ProjectResponse fromEntity(Project project) {
		return new ProjectResponse(
				 project.getId(),
	                project.getName(),
	                project.getDescription(),
	                project.getOwner().getId(),
	                project.getOwner().getName(),
	                project.getCreatedAt(),
	                project.getUpdatedAt());
	}
}
