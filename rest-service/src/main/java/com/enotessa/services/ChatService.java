package com.enotessa.services;

import com.enotessa.dto.MessageDto;
import com.enotessa.gpt.GptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ChatService {
    @Autowired
    private GptService gptService;

    public String sendMessage(MessageDto request) {
        String requestMessage = request.getMessage();
        if (requestMessage == null || requestMessage.isEmpty()) {
            throw new IllegalArgumentException("Message cannot be empty");
        }
        return gptService.sendChatRequest(requestMessage);
    }
}
