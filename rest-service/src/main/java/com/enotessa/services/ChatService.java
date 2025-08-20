package com.enotessa.services;

import com.enotessa.dto.MessageDto;
import com.enotessa.dto.ProfessionalPositionDto;
import com.enotessa.entities.Message;
import com.enotessa.entities.User;
import com.enotessa.exceptions.GptServiceException;
import com.enotessa.exceptions.UserNotFoundException;
import com.enotessa.gpt.GptMessage;
import com.enotessa.gpt.GptService;
import com.enotessa.repositories.MessageRepository;
import com.enotessa.repositories.UserRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.enotessa.constants.ChatConstants.*;

@Component
@AllArgsConstructor
public class ChatService {
    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);
    private final GptService gptService;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final MessageService messageService;

    @Transactional
    public String sendMessage(@NonNull MessageDto request, @NonNull UserDetails userDetails) {
        logger.debug("Processing message from user: " + userDetails.getUsername());
        validateMessage(request);
        User user = getUserByLogin(userDetails.getUsername(), userDetails);
        saveUserMessage(request, user);
        return processGptResponse(user, request.getMessage());
    }

    private String processGptResponse(User user, String message) {
        List<GptMessage> conversationHistory = loadConversationHistory(user);
        try {
            String gptResponse = gptService.sendChatRequest(message, conversationHistory);
            messageService.addMessage(gptResponse, ASSISTANT_ROLE, user, LocalDateTime.now());
            logger.debug("Received GPT response: " + gptResponse);
            return gptResponse;
        } catch (Exception e) {
            logger.error("Failed to process GPT request for user " + user.getLogin() + ": " + e.getMessage());
            throw new GptServiceException(FAILED_GET_RESPONSE, e);
        }
    }

    @Transactional
    public void changeInterviewProfession(ProfessionalPositionDto request, UserDetails userDetails) {
        if (request.getProfessionalPosition() == null || request.getProfessionalPosition().isBlank()) {
            logger.error(PROFESSION_IS_EMPTY);
            throw new IllegalArgumentException(PROFESSION_IS_EMPTY);
        }
        User user = getUserByLogin(userDetails.getUsername(), userDetails);
        gptService.changeInterviewProfession(request.getProfessionalPosition());

        clearUserMessages(user);
        initializeInterviewMessages(user, request.getProfessionalPosition());
    }

    public void deleteMessages(UserDetails userDetails) {
        User user = getUserByLogin(userDetails.getUsername(), userDetails);
        clearUserMessages(user);
        initializeInterviewMessages(user, gptService.getProfession().getDisplayName());
    }


    //---------------------------
    private void validateMessage(MessageDto request) {
        if (request.getMessage().isBlank()) {
            logger.error(EMPTY_MESSAGE_ERROR);
            throw new IllegalArgumentException(EMPTY_MESSAGE_ERROR);
        }
    }

    private User getUserByLogin(@NonNull String login, @NonNull UserDetails userDetails) {
        return userRepository.findByLogin(login)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_ERROR));
    }

    private void saveUserMessage(MessageDto request, User user) {
        messageService.addMessage(request.getMessage(), USER_ROLE, user, request.getTimestamp());
    }

    private List<GptMessage> loadConversationHistory(User user) {
        List<Message> messagesFromDb = messageRepository.findAllByUserOrderByIdAsc(user);
        return messagesFromDb.stream()
                .map(message -> new GptMessage(message.getRole(), message.getMessage()))
                .collect(Collectors.toList());
    }

    private void clearUserMessages(User user) {
        messageRepository.deleteAllByUser(user);
    }

    private void initializeInterviewMessages(User user, String profession) {
        messageService.addMessage(ROLE_PROMPT, SYSTEM_ROLE, user, LocalDateTime.now());
        messageService.addMessage(String.format(PROMPT_TEMPLATE, profession), USER_ROLE, user, LocalDateTime.now());
    }
}
