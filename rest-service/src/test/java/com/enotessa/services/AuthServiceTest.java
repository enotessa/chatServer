package com.enotessa.services;

import com.enotessa.dto.AuthResponse;
import com.enotessa.dto.LoginRequest;
import com.enotessa.dto.RegisterRequest;
import com.enotessa.entities.User;
import com.enotessa.exceptions.RegisterException;
import com.enotessa.exceptions.ValidationException;
import com.enotessa.repositories.UserRepository;
import com.enotessa.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_success() {
        RegisterRequest request = new RegisterRequest();
        request.setLogin("testuser");
        request.setEmail("test@example.com");
        request.setPassword("password");

        when(userRepository.existsByLogin("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(jwtService.generateAccessToken(any(UserDetails.class))).thenReturn("accessToken123");
        when(jwtService.generateRefreshToken(any(UserDetails.class))).thenReturn("refreshToken123");

        AuthResponse authResponse = authService.register(request);
        AuthResponse authResponseTrue = new AuthResponse("accessToken123", "refreshToken123");

        assertEquals(authResponse, authResponseTrue);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertEquals("testuser", savedUser.getLogin());
        assertEquals("test@example.com", savedUser.getEmail());
        assertEquals("password", savedUser.getPassword());
    }

    @Test
    void register_duplicateLogin_throwsRegisterException() {
        RegisterRequest request = new RegisterRequest();
        request.setLogin("testuser");
        request.setEmail("test@example.com");

        when(userRepository.existsByLogin("testuser")).thenReturn(true);

        RegisterException exception = assertThrows(RegisterException.class, () -> authService.register(request));
        assertEquals("Login already exists", exception.getMessage());
    }

    @Test
    void register_duplicateEmail_throwsRegisterException() {
        RegisterRequest request = new RegisterRequest();
        request.setLogin("testuser");
        request.setEmail("test@example.com");

        when(userRepository.existsByLogin("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        RegisterException exception = assertThrows(RegisterException.class, () -> authService.register(request));
        assertEquals("Email already exists", exception.getMessage());
    }

    @Test
    void login_success() {
        LoginRequest request = new LoginRequest();
        request.setLogin("testuser");
        request.setPassword("password");

        User user = new User();
        user.setLogin("testuser");
        user.setPassword("password");

        when(userRepository.findByLogin("testuser")).thenReturn(Optional.of(user));
        when(jwtService.generateAccessToken(any(UserDetails.class))).thenReturn("accessToken123");
        when(jwtService.generateRefreshToken(any(UserDetails.class))).thenReturn("refreshToken123");

        AuthResponse authResponse = authService.login(request);
        AuthResponse authResponseTrue = new AuthResponse("accessToken123", "refreshToken123");

        assertEquals(authResponse, authResponseTrue);
    }

    @Test
    void login_userNotFound_throwsValidationException() {
        LoginRequest request = new LoginRequest();
        request.setLogin("unknown");
        request.setPassword("password");

        when(userRepository.findByLogin("unknown")).thenReturn(Optional.empty());

        ValidationException exception = assertThrows(ValidationException.class, () -> authService.login(request));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void login_invalidPassword_throwsValidationException() {
        LoginRequest request = new LoginRequest();
        request.setLogin("testuser");
        request.setPassword("wrongpassword");

        User user = new User();
        user.setLogin("testuser");
        user.setPassword("password");

        when(userRepository.findByLogin("testuser")).thenReturn(Optional.of(user));

        ValidationException exception = assertThrows(ValidationException.class, () -> authService.login(request));
        assertEquals("Invalid password", exception.getMessage());
    }
}
