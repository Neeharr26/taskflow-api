package com.neehar.taskflow.task.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.neehar.taskflow.common.Priority;
import com.neehar.taskflow.common.Status;
import com.neehar.taskflow.task.Task;

public record TaskResponse(
		Long id,
		String title,
		String description,
		Status status,
		Priority priority,
		LocalDate dueDate,
		Long projectId,
		Long assigneeId,
		String assigneeName,
		LocalDateTime createdAt,
		LocalDateTime updatedAt
		) {
	
	public static TaskResponse fromEntity(Task task) {
		return new TaskResponse(
				task.getId(),
				task.getTitle(),
				task.getDescription(),
				task.getStatus(),
				task.getPriority(),
                task.getDueDate(),
                task.getProject().getId(),
                task.getAssignee() != null ? task.getAssignee().getId() : null,
                task.getAssignee() != null ? task.getAssignee().getName() : null,
                task.getCreatedAt(),
                task.getUpdatedAt());
	}
}
