package com.icebreaker.websocket;

import com.icebreaker.controllers.ChatController;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ChatControllerTest {

    @Test
    public void whenSendMessage_thenReturnsSameMessage() {
        ChatController controller = new ChatController(new SimpMessagingTemplate(new MessageChannel() {
            @Override
            public boolean send(Message<?> message, long timeout) {
                return true;
            }
        }));
        ChatMessage incomingMessage = new ChatMessage(0, "Hello World!", LocalDateTime.now(), "UnitTestUser", "UnitTestUserId");

        ChatMessage returnedMessage = controller.handleMessage(incomingMessage);
        assertTrue(returnedMessage.getContent().contains("Hello World!"));
    }
}
