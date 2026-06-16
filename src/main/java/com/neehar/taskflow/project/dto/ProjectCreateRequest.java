package com.neehar.taskflow.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProjectCreateRequest(
		@NotBlank
		@Size(max = 100, message = "Name must be at most 100 characters")
		String name,
		 @Size(max = 1000, message = "Description must be at most 1000 characters")
		String description) {
	
}
