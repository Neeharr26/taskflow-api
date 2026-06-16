package com.neehar.taskflow.task.dto;

import com.neehar.taskflow.common.Status;

import jakarta.validation.constraints.NotNull;

public record TaskStatusUpdateRequest(
		@NotNull
		Status status) {

}
