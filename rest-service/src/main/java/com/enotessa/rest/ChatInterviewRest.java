package com.enotessa.rest;

import com.enotessa.dto.MessageDto;
import com.enotessa.dto.ProfessionalPositionDto;
import com.enotessa.services.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/interview")
public class ChatInterviewRest {
    @Autowired
    ChatService chatService;

    @PostMapping("/message")
    public ResponseEntity<MessageDto> sendInterviewMessage(@RequestBody MessageDto request, @AuthenticationPrincipal UserDetails user) {
        String message = chatService.sendMessage(request, user);
        return ResponseEntity.ok(new MessageDto("HR", message, LocalDateTime.now()));
    }

    @PostMapping("/interviewProfession")
    public ResponseEntity<Void> changeInterviewProfession(@RequestBody ProfessionalPositionDto request, @AuthenticationPrincipal UserDetails user) {
        chatService.changeInterviewProfession(request, user);
        return ResponseEntity.ok(null);
    }

    @DeleteMapping("/deleteMessages")
    public ResponseEntity<Void> deleteMessages(@AuthenticationPrincipal UserDetails user) {
        chatService.deleteMessages(user);
        return ResponseEntity.ok(null);
    }
}
