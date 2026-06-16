package com.neehar.taskflow.task.dto;

import java.time.LocalDate;

import com.neehar.taskflow.common.Priority;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TaskCreateRequest(
		@NotBlank
		@Size(max=200)
		String title,
		@Size(max=2000)
		String description,
		@NotNull
		Priority priority,
		LocalDate dueDate,
		Long assigneeId) {
}
