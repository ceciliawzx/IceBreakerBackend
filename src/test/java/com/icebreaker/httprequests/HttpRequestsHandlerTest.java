package com.icebreaker.httprequests;

import com.icebreaker.controllers.httphandlers.HttpRequestsController;
import com.icebreaker.services.ChatService;
import com.icebreaker.services.HangmanService;
import com.icebreaker.services.WordleService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.mockito.Mock;
import org.mockito.Mockito;

public class HttpRequestsHandlerTest {

    @Test
    public void handlerCanReceiveRequestAndReply() {
        HttpRequestsController handler = new HttpRequestsController();

        // Test with a null message
        String responseNull = handler.handleRequest(null);
        assertEquals("Received message: null", responseNull);

        // Test with a non-null message
        String response = handler.handleRequest("Hello");
        assertEquals("Received message: Hello", response);
    }
}
