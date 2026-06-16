package com.neehar.taskflow.exception;

import java.time.LocalDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
		 int status,
	        String error,
	        String message,
	        String path,
	        LocalDateTime timestamp,
	        Map<String, String> validationErrors)
{
	 public static ErrorResponse of(int status, String error, String message, String path) {
	        return new ErrorResponse(status, error, message, path, LocalDateTime.now(), null);
	    }
	 public static ErrorResponse withValidation(int status, String error, String message, String path, Map<String, String> errors) {
	        return new ErrorResponse(status, error, message, path, LocalDateTime.now(), errors);
	    }

}
