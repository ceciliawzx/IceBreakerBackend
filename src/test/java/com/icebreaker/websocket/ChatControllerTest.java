package com.icebreaker.websocket;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ChatControllerTest {

    @Test
    public void whenSendMessage_thenReturnsSameMessage() {
        ChatController controller = new ChatController();
        ChatMessage incomingMessage = new ChatMessage();
        incomingMessage.setContent("Hello World");
        // Assuming ChatMessage has a setter for 'content'

        ChatMessage returnedMessage = controller.sendMessage(0, incomingMessage);
        assertEquals("Hello World", returnedMessage.getContent());
        // Additional assertions as needed
    }

}
