package com.enotessa.rest;

import com.enotessa.dto.AuthResponse;
import com.enotessa.dto.LoginRequest;
import com.enotessa.dto.RefreshRequest;
import com.enotessa.dto.RegisterRequest;
import com.enotessa.entities.User;
import com.enotessa.exceptions.ValidationException;
import com.enotessa.security.CustomUserDetails;
import com.enotessa.services.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthRest {
    private final AuthService authService;
    private static final Logger logger = LoggerFactory.getLogger(AuthRest.class);

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody @Valid RegisterRequest request) {
        logger.info("register()");
        AuthResponse authResponse = authService.register(request);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest request) {
        logger.info("login()");
        AuthResponse authResponse = authService.login(request);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody @Valid RefreshRequest request) {
        try {
            logger.info("refresh()");
            AuthResponse authResponse = authService.refresh(request);
            return ResponseEntity.ok(authResponse);
        } catch (ValidationException e) {
            return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).build();
        }
    }
}
