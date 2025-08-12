package com.enotessa.services;

import com.enotessa.dto.LoginRequest;
import com.enotessa.dto.RegisterRequest;
import com.enotessa.entities.User;
import com.enotessa.exceptions.RegisterException;
import com.enotessa.exceptions.ValidationException;
import com.enotessa.repositories.UserRepository;
import com.enotessa.security.CustomUserDetails;
import com.enotessa.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtService jwtService;

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
            throw new RegisterException("Login already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RegisterException("Email already exists");
        }
    }

    public String login(LoginRequest request) {
        String username = request.getLogin();
        User user = userRepository.findByLogin(username)
                .orElseThrow(() -> new ValidationException("User not found"));
        if (!request.getPassword().equals(user.getPassword())) {
            throw new ValidationException("Invalid password");
        }

        UserDetails userDetails = new CustomUserDetails(user);
        return jwtService.generateToken(userDetails);
    }
}
