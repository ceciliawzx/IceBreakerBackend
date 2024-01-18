package com.icebreaker.websocket;

import com.icebreaker.controller.ChatController;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ChatControllerTest {

    @Test
    public void whenSendMessage_thenReturnsSameMessage() {
        ChatController controller = new ChatController();
        ChatMessage incomingMessage = new ChatMessage(0, "Hello World!", LocalDateTime.now(), "UnitTestUser");

        ChatMessage returnedMessage = controller.handleMessage(0, incomingMessage);
        assertTrue(returnedMessage.getContent().contains("Hello World!"));
    }
}
