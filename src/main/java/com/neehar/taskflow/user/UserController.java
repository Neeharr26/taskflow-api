package com.neehar.taskflow.user;
import com.neehar.taskflow.user.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import com.neehar.taskflow.user.dto.RegisterRequest;
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
private final UserService userService;

@GetMapping("/{id}")
public ResponseEntity<UserResponse> getUserById(@PathVariable Long id){
	UserResponse response = userService.getUserById(id);
	 return ResponseEntity.ok(response);
}
@PostMapping("/register")
public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
    UserResponse created = userService.register(request);
    return ResponseEntity.status(201).body(created);
}
}
