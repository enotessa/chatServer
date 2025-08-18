package com.enotessa.rest;

import com.enotessa.dto.MessageDto;
import com.enotessa.dto.ProfessionalPositionDto;
import com.enotessa.services.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/chats")
public class ChatRest {
    @Autowired
    ChatService chatService;

    @PostMapping("/interview")
    public ResponseEntity<MessageDto> sendInterviewMessage(@RequestBody MessageDto request, @AuthenticationPrincipal UserDetails user) {
        String message = chatService.sendMessage(request, user);
        return ResponseEntity.ok(new MessageDto("HR", message, LocalDateTime.now()));
    }

    @PostMapping("/interviewProfession")
    public ResponseEntity<Void> changeInterviewProfession(@RequestBody ProfessionalPositionDto request, @AuthenticationPrincipal UserDetails user) {
        chatService.changeInterviewProfession(request, user);
        return ResponseEntity.ok(null);
    }
}
