package com.neehar.taskflow.user;
import com.neehar.taskflow.exception.NotFoundException;
import com.neehar.taskflow.user.dto.RegisterRequest;
import com.neehar.taskflow.user.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
		private final UserRepository userRepository;
		private final PasswordEncoder passwordEncoder;

	public UserResponse getUserById(Long id) {
		User user =userRepository.findById(id).orElseThrow(()-> new NotFoundException("User not found with id: " + id));
		
	return UserResponse.fromEntity(user);
	}
	 @Transactional
	    public UserResponse register(RegisterRequest request) {
	        if (userRepository.existsByEmail(request.email())) {
	            throw new IllegalArgumentException("Email already registered: " + request.email());
	        }

	        User user = User.builder()
	                .email(request.email())
	                .password(passwordEncoder.encode(request.password()))
	                .name(request.name())
	                .build();

	        User saved = userRepository.save(user);
	        return UserResponse.fromEntity(saved);
	}
}
