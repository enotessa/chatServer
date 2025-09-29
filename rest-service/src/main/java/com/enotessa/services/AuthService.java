package com.enotessa.services;

import com.enotessa.AuthEventProducer;
import com.enotessa.dto.AuthResponse;
import com.enotessa.dto.LoginRequest;
import com.enotessa.dto.RefreshRequest;
import com.enotessa.dto.RegisterRequest;
import com.enotessa.entities.User;
import com.enotessa.exceptions.RegisterException;
import com.enotessa.exceptions.ValidationException;
import com.enotessa.repositories.UserRepository;
import com.enotessa.security.CustomUserDetails;
import com.enotessa.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthEventProducer authEventProducer;
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private static final String LOGIN_ALREADY_EXISTS = "Login already exists";
    private static final String EMAIL_ALREADY_EXISTS = "Email already exists";
    private static final String USER_NOT_FOUND = "User not found";
    private static final String INVALID_PASSWORD = "Invalid password";
    private static final String INVALID_OR_EXPIRED_REFRESH_TOKEN = "Invalid or expired refresh token";

    public AuthResponse register(RegisterRequest request) {
        checkUnique(request);
        User user = createUserFromRequest(request);
        userRepository.save(user);

        UserDetails userDetails = new CustomUserDetails(user);
        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        authEventProducer.sendUserRegistered(String.valueOf(user.getId()));
        return new AuthResponse(accessToken, refreshToken);
    }

    private User createUserFromRequest(RegisterRequest request) {
        User user = new User();
        user.setLogin(request.getLogin());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        return user;
    }

    private void checkUnique(RegisterRequest request) {
        if (userRepository.existsByLogin(request.getLogin())) {
            logger.error(LOGIN_ALREADY_EXISTS);
            throw new RegisterException(LOGIN_ALREADY_EXISTS);
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            logger.error(EMAIL_ALREADY_EXISTS);
            throw new RegisterException(EMAIL_ALREADY_EXISTS);
        }
    }

    public AuthResponse login(LoginRequest request) {
        String username = request.getLogin();
        User user = userRepository.findByLogin(username)
                .orElseThrow(() -> new ValidationException(USER_NOT_FOUND));
        if (!request.getPassword().equals(user.getPassword())) {
            logger.error(INVALID_PASSWORD);
            throw new ValidationException(INVALID_PASSWORD);
        }

        UserDetails userDetails = new CustomUserDetails(user);
        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        authEventProducer.sendUserLoggedIn(String.valueOf(user.getId()));
        return new AuthResponse(accessToken, refreshToken);
    }

    public AuthResponse refresh(RefreshRequest request) {
        String refreshToken = request.getRefreshToken();
        String username = jwtService.extractUsername(refreshToken);

        User user = userRepository.findByLogin(username)
                .orElseThrow(() -> new ValidationException("User not found"));

        if (jwtService.isTokenValid(refreshToken, new CustomUserDetails(user))) {
            String newAccessToken = jwtService.generateAccessToken(new CustomUserDetails(user));
            return new AuthResponse(newAccessToken, refreshToken);
        } else {
            throw new ValidationException(INVALID_OR_EXPIRED_REFRESH_TOKEN);
        }
    }
}
