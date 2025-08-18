package com.enotessa.services;

import com.enotessa.entities.Message;
import com.enotessa.entities.User;
import com.enotessa.repositories.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class MessageService {
    @Autowired
    private MessageRepository messageRepository;

    public void addMessage(String text, String role, User user, LocalDateTime timestamp) {
        Message message = new Message();
        message.setRole(role);
        message.setMessage(text);
        message.setUser(user);
        message.setTimestamp(timestamp);
        messageRepository.save(message);
    }
}
