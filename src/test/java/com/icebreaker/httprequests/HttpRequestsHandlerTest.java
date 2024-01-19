package com.icebreaker.httprequests;

import com.icebreaker.controllers.HttpRequestsHandler;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.mockito.Mockito;

public class HttpRequestsHandlerTest {

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
