package com.enotessa.services;

import com.enotessa.dto.LoginRequest;
import com.enotessa.dto.RegisterRequest;
import com.enotessa.entities.User;
import com.enotessa.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    public void register(RegisterRequest request) {
        checkUnique(request);
        User user = createUserFromRequest(request);
        userRepository.save(user);
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
            throw new RuntimeException("Login already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
    }

    public void login(LoginRequest request) {
        User user = userRepository.findByLogin(request.getLogin())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if ( !request.getPassword().equals(user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }
    }
}
