package com.neehar.taskflow.auth.dto;

public record AuthResponse(
		String token,
        String email,
        String name) {
	
}
