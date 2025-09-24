package com.enotessa.rest;

import com.enotessa.dto.MessageDto;
import com.enotessa.dto.ProfessionalPositionDto;
import com.enotessa.entities.User;
import com.enotessa.services.ChatService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/interview")
@AllArgsConstructor
public class ChatInterviewRest {
    private static final Logger logger = LoggerFactory.getLogger(ChatInterviewRest.class);
    private final ChatService chatService;

    @PostMapping("/message")
    public CompletableFuture<ResponseEntity<MessageDto>> sendInterviewMessage(@RequestBody MessageDto request, @AuthenticationPrincipal UserDetails user) {
        logger.info("sendInterviewMessage()");
        return chatService.sendMessage(request, user)
                .thenApply(text -> ResponseEntity.ok(new MessageDto("HR", text, LocalDateTime.now())));
    }

    @PostMapping("/interviewProfession")
    public ResponseEntity<Void> changeInterviewProfession(@RequestBody ProfessionalPositionDto request, @AuthenticationPrincipal UserDetails user) {
        logger.info("changeInterviewProfession()");
        chatService.changeInterviewProfession(request, user);
        return ResponseEntity.ok(null);
    }

    @DeleteMapping("/deleteMessages")
    public ResponseEntity<Void> deleteMessages(@AuthenticationPrincipal UserDetails user) {
        logger.info("deleteMessages()");
        chatService.deleteMessages(user);
        return ResponseEntity.ok(null);
    }
}
