package com.enotessa.rest;

import com.enotessa.dto.LoginRequest;
import com.enotessa.dto.RegisterRequest;
import com.enotessa.dto.TokenResponce;
import com.enotessa.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthRest {
    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<TokenResponce> register(@RequestBody @Valid RegisterRequest request) {
        String token = authService.register(request);
        return ResponseEntity.ok(new TokenResponce(token));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponce> login(@RequestBody @Valid LoginRequest request) {
        String token = authService.login(request);
        return ResponseEntity.ok(new TokenResponce(token));
    }
}
