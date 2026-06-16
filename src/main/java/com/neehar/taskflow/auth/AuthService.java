package com.neehar.taskflow.auth;
import com.neehar.taskflow.auth.dto.AuthResponse;
import com.neehar.taskflow.auth.dto.LoginRequest;
import com.neehar.taskflow.user.User;
import com.neehar.taskflow.user.UserRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {	
	 private final UserRepository userRepository;
	 private final PasswordEncoder passwordEncoder;
	 private final JwtService jwtService;
	 
	 public AuthResponse login(LoginRequest request) {
		 User user= userRepository.findByEmail(request.email())
	                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));
		 
		 if (!passwordEncoder.matches(request.password(), user.getPassword())) {
	            throw new BadCredentialsException("Invalid credentials");
	        }
		 
		 String token = jwtService.generateToken(user.getEmail());
	        return new AuthResponse(token, user.getEmail(), user.getName());
	 }
}
