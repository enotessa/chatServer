package com.enotessa.rest;

import com.enotessa.dto.AuthResponse;
import com.enotessa.dto.LoginRequest;
import com.enotessa.dto.RegisterRequest;
import com.enotessa.services.AuthService;
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
        logger.debug("register()");
        AuthResponse authResponse = authService.register(request);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest request) {
        logger.debug("login()");
        AuthResponse authResponse = authService.login(request);
        return ResponseEntity.ok(authResponse);
    }
}
