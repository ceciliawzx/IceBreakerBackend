package com.icebreaker.httprequests;

import com.icebreaker.controllers.ChatController;
import com.icebreaker.controllers.HttpRequestsHandler;
import com.icebreaker.services.ChatService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.mockito.Mock;
import org.mockito.Mockito;

public class HttpRequestsHandlerTest {

    @Mock
    private ChatService chatService;

    @Test
    public void handlerCanReceiveRequestAndReply() {
        HttpServletRequest mockRequest = Mockito.mock(HttpServletRequest.class);
        HttpRequestsHandler handler = new HttpRequestsHandler(chatService);

        // Test with a null message
        String responseNull = handler.handleRequest(null);
        assertEquals("Received message: null", responseNull);

        // Test with a non-null message
        String response = handler.handleRequest("Hello");
        assertEquals("Received message: Hello", response);
    }
}
