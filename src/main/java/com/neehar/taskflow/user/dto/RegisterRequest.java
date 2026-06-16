package com.neehar.taskflow.user.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
public record RegisterRequest(@NotBlank(message="Email is required")
@Email(message="Must be a valid email address")
String email,

@NotBlank(message="Password is required")
@Size(min=8, max=100, message="Password must be 8-100")
String password,

@NotBlank(message = "Name is required")
@Size(max = 100, message = "Name must be at most 100 characters")
String name
) {}
