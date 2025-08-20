package com.enotessa.services;

import com.enotessa.dto.LoginRequest;
import com.enotessa.dto.RegisterRequest;
import com.enotessa.entities.User;
import com.enotessa.exceptions.RegisterException;
import com.enotessa.exceptions.ValidationException;
import com.enotessa.repositories.UserRepository;
import com.enotessa.security.CustomUserDetails;
import com.enotessa.security.JwtService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private static final String LOGIN_ALREADY_EXISTS = "Login already exists";
    private static final String EMAIL_ALREADY_EXISTS = "Email already exists";
    private static final String USER_NOT_FOUND = "User not found";
    private static final String INVALID_PASSWORD = "Invalid password";

    public String register(RegisterRequest request) {
        checkUnique(request);
        User user = createUserFromRequest(request);
        userRepository.save(user);

        UserDetails userDetails = new CustomUserDetails(user);
        return jwtService.generateToken(userDetails);
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

    public String login(LoginRequest request) {
        String username = request.getLogin();
        User user = userRepository.findByLogin(username)
                .orElseThrow(() -> new ValidationException(USER_NOT_FOUND));
        if (!request.getPassword().equals(user.getPassword())) {
            logger.error(INVALID_PASSWORD);
            throw new ValidationException(INVALID_PASSWORD);
        }

        UserDetails userDetails = new CustomUserDetails(user);
        return jwtService.generateToken(userDetails);
    }
}
