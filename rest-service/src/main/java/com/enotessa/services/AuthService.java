package com.enotessa.services;

import com.enotessa.dto.AuthResponse;
import com.enotessa.dto.RegisterRequest;
import com.enotessa.entities.User;
import com.enotessa.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    public AuthResponse register(RegisterRequest request) {
        checkUnique(request);
        User user = createUserFromRequest(request);
        User savedUser = userRepository.save(user);

        return new AuthResponse(
                        savedUser.getId(),
                        savedUser.getLogin(),
                        savedUser.getEmail()
        );
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
}
