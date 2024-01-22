package com.icebreaker.websocket;

import com.icebreaker.controllers.ChatController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)

public class ChatControllerTest {

    @InjectMocks
    private ChatController controller;

    @Test
    public void whenSendMessage_thenReturnsSameMessage() {
        ChatMessage incomingMessage = new ChatMessage(0, "Hello World!", LocalDateTime.now(), "UnitTestUser", "UnitTestUserId");

        ChatMessage returnedMessage = controller.handleMessage(incomingMessage);

        assertTrue(returnedMessage.getContent().contains("Hello World!"));
    }
}
