package com.enotessa.rest;

import com.enotessa.dto.LoginRequest;
import com.enotessa.dto.RegisterRequest;
import com.enotessa.dto.TokenResponce;
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
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthRest {
    private final AuthService authService;
    private static final Logger logger = LoggerFactory.getLogger(AuthRest.class);

    @PostMapping("/register")
    public ResponseEntity<TokenResponce> register(@RequestBody @Valid RegisterRequest request) {
        logger.debug("register()");
        String token = authService.register(request);
        return ResponseEntity.ok(new TokenResponce(token));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponce> login(@RequestBody @Valid LoginRequest request) {
        logger.debug("login()");
        String token = authService.login(request);
        return ResponseEntity.ok(new TokenResponce(token));
    }
}
