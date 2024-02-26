package com.icebreaker.httprequests;

import com.icebreaker.controllers.httphandlers.HttpRequestsHandler;
import com.icebreaker.services.ChatService;
import com.icebreaker.services.HangmanService;
import com.icebreaker.services.WordleService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.mockito.Mock;
import org.mockito.Mockito;

public class HttpRequestsHandlerTest {

    @Mock
    private ChatService chatService;
    private WordleService wordleService;
    private HangmanService hangmanService;

    @Test
    public void handlerCanReceiveRequestAndReply() {
        HttpServletRequest mockRequest = Mockito.mock(HttpServletRequest.class);
        HttpRequestsHandler handler = new HttpRequestsHandler();

        // Test with a null message
        String responseNull = handler.handleRequest(null);
        assertEquals("Received message: null", responseNull);

        // Test with a non-null message
        String response = handler.handleRequest("Hello");
        assertEquals("Received message: Hello", response);
    }
}
