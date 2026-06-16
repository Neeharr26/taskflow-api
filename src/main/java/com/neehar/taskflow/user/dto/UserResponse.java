package com.neehar.taskflow.user.dto;
import java.time.LocalDateTime;
public record UserResponse(
		Long id,
		String email,
		String name,
		LocalDateTime createdAt
		) {
	  public static UserResponse fromEntity(com.neehar.taskflow.user.User user) {
	        return new UserResponse(
	                user.getId(),
	                user.getEmail(),
	                user.getName(),
	                user.getCreatedAt()
	        );
	    }
}
