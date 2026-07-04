package com.neehar.taskflow.user;

import com.neehar.taskflow.exception.NotFoundException;
import com.neehar.taskflow.user.dto.RegisterRequest;
import com.neehar.taskflow.user.dto.UserResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;   // a dependency, but NOT a repository — still mocked

    @InjectMocks private UserService userService;

    // ---------- getUserById ----------

    @Test
    void getUserById_returnsUser_whenFound() {
        User user = User.builder().id(1L).email("neehar@example.com").name("Neehar").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserResponse response = userService.getUserById(1L);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("Neehar");
    }

    @Test
    void getUserById_throwsNotFound_whenMissing() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(99L))
                .isInstanceOf(NotFoundException.class);
    }

    // ---------- register ----------

    @Test
    void register_savesUser_whenEmailIsNew() {
        RegisterRequest request = new RegisterRequest("neehar@example.com", "password123", "Neehar");
        when(userRepository.existsByEmail("neehar@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashed-password"); // dictate the "hash"
        User saved = User.builder().id(1L).email("neehar@example.com").name("Neehar").build();
        when(userRepository.save(any(User.class))).thenReturn(saved);

        UserResponse response = userService.register(request);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.email()).isEqualTo("neehar@example.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_throwsIllegalArgument_whenEmailAlreadyExists() {
        RegisterRequest request = new RegisterRequest("taken@example.com", "password123", "Neehar");
        when(userRepository.existsByEmail("taken@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.register(request))
                .isInstanceOf(IllegalArgumentException.class);

        verify(userRepository, never()).save(any());   // nothing was saved
    }
}