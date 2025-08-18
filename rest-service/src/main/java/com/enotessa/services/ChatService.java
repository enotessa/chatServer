package com.enotessa.services;

import com.enotessa.dto.MessageDto;
import com.enotessa.dto.ProfessionalPositionDto;
import com.enotessa.entities.Message;
import com.enotessa.entities.User;
import com.enotessa.gpt.GptMessage;
import com.enotessa.gpt.GptService;
import com.enotessa.repositories.MessageRepository;
import com.enotessa.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class ChatService {
    private final GptService gptService;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final MessageService messageService;

    private final String ROLE_PROMPT = "ты - интервьюер и проводишь мне собеседование";
    private final String PROMPT_TEMPLATE = """
            ты проводишь собеседование на позицию %s.\s
            Задавай мне по одному вопросу.\s
            Пропусти вопрос об опыте.\s
            Начинай сразу с технических вопросов.\s
            После того, как я отвечаю на вопрос, ты говоришь, что я написал правильно,\s
            а что направильно и задаешь следующий вопрос""";

    private final String SYSTEM_ROLE = "system";
    private final String USER_ROLE = "user";
    private final String ASSISTANT_ROLE = "assistant";

    public String sendMessage(MessageDto request, UserDetails userDetails) {
        validateMessage(request);
        User user = getUserByLogin(userDetails.getUsername());
        saveUserMessage(request, user);

        List<GptMessage> conversationHistory = loadConversationHistory(user);

        String gptResponse = gptService.sendChatRequest(request.getMessage(), conversationHistory);
        messageService.addMessage(gptResponse, ASSISTANT_ROLE, user, LocalDateTime.now());

        return gptResponse;
    }


    public void changeInterviewProfession(ProfessionalPositionDto request, UserDetails userDetails) {
        User user = getUserByLogin(userDetails.getUsername());
        gptService.changeInterviewProfession(request.getProfessionalPosition());

        clearUserMessages(user);
        initializeInterviewMessages(user, request.getProfessionalPosition());
    }

    //---------------------------
    private void validateMessage(MessageDto request) {
        if (request.getMessage() == null || request.getMessage().isEmpty()) {
            throw new IllegalArgumentException("Message cannot be empty");
        }
    }

    private User getUserByLogin(String login) {
        return userRepository.findByLogin(login)
                .orElseThrow(() -> new RuntimeException("User not found"));
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
        List<Message> oldMessages = messageRepository.findAllByUserOrderByIdAsc(user);
        messageRepository.deleteAll(oldMessages);
    }

    private void initializeInterviewMessages(User user, String profession) {
        messageService.addMessage(ROLE_PROMPT, SYSTEM_ROLE, user, LocalDateTime.now());
        messageService.addMessage(String.format(PROMPT_TEMPLATE, profession), USER_ROLE, user, LocalDateTime.now());
    }
}
