package com.enotessa.services;

import com.enotessa.dto.MessageDto;
import com.enotessa.dto.ProfessionalPositionDto;
import com.enotessa.entities.Message;
import com.enotessa.entities.User;
import com.enotessa.gpt.GptService;
import com.enotessa.repositories.MessageRepository;
import com.enotessa.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ChatServiceTest {

    @Mock
    private GptService gptService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private MessageService messageService;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private ChatService chatService;

    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testUser = new User();
        testUser.setId(1L);
        testUser.setLogin("testUser");
    }

    @Test
    void sendMessage_ShouldReturnGptResponse() {
        // подготовка данных
        MessageDto request = new MessageDto("user", "Привет", LocalDateTime.now());

        when(userDetails.getUsername()).thenReturn("testUser");
        when(userRepository.findByLogin("testUser")).thenReturn(Optional.of(testUser));
        when(messageRepository.findAllByUserOrderByIdAsc(testUser)).thenReturn(List.of());
        when(gptService.sendChatRequest(eq("Привет"), anyList())).thenReturn("Ответ GPT");

        // вызов метода
        String response = chatService.sendMessage(request, userDetails);

        // проверка
        assertEquals("Ответ GPT", response);
        verify(messageService).addMessage("Привет", "user", testUser, request.getTimestamp());
        verify(messageService).addMessage(eq("Ответ GPT"), eq("assistant"), eq(testUser), any(LocalDateTime.class));
    }

    @Test
    void sendMessage_EmptyMessage_ShouldThrowException() {
        MessageDto request = new MessageDto("user", "", LocalDateTime.now());

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                chatService.sendMessage(request, userDetails));

        assertEquals("Message cannot be empty", exception.getMessage());
    }

    @Test
    void changeInterviewProfession_ShouldClearMessagesAndAddNew() {
        ProfessionalPositionDto request = new ProfessionalPositionDto("Java Developer");

        when(userDetails.getUsername()).thenReturn("testUser");
        when(userRepository.findByLogin("testUser")).thenReturn(Optional.of(testUser));
        when(messageRepository.findAllByUserOrderByIdAsc(testUser)).thenReturn(List.of(new Message(), new Message()));

        chatService.changeInterviewProfession(request, userDetails);

        // проверяем вызовы
        verify(gptService).changeInterviewProfession("Java Developer");
        verify(messageRepository).deleteAll(anyList());
        verify(messageService, times(2)).addMessage(anyString(), anyString(), eq(testUser), any(LocalDateTime.class));
    }
}
