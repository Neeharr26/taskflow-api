package com.neehar.taskflow.auth;

import com.neehar.taskflow.auth.dto.AuthResponse;
import com.neehar.taskflow.auth.dto.LoginRequest;
import com.neehar.taskflow.user.User;
import com.neehar.taskflow.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;             // faked so no real token crypto runs

    @InjectMocks private AuthService authService;

    @Test
    void login_returnsToken_whenCredentialsAreValid() {
        User user = User.builder().id(1L).email("neehar@example.com")
                .password("hashed-password").name("Neehar").build();
        LoginRequest request = new LoginRequest("neehar@example.com", "rawPassword");

        when(userRepository.findByEmail("neehar@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("rawPassword", "hashed-password")).thenReturn(true);
        when(jwtService.generateToken("neehar@example.com")).thenReturn("jwt-token");

        AuthResponse response = authService.login(request);

        assertThat(response.token()).isEqualTo("jwt-token");
        assertThat(response.email()).isEqualTo("neehar@example.com");
        assertThat(response.name()).isEqualTo("Neehar");
    }

    @Test
    void login_throwsBadCredentials_whenUserNotFound() {
        LoginRequest request = new LoginRequest("ghost@example.com", "rawPassword");
        when(userRepository.findByEmail("ghost@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void login_throwsBadCredentials_whenPasswordDoesNotMatch() {
        User user = User.builder().id(1L).email("neehar@example.com")
                .password("hashed-password").name("Neehar").build();
        LoginRequest request = new LoginRequest("neehar@example.com", "wrongPassword");

        when(userRepository.findByEmail("neehar@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", "hashed-password")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class);
    }
}