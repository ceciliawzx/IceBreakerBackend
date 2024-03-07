package com.icebreaker.httprequests;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpRequestsHandlerTest {

    @Test
    public void handlerCanReceiveRequestAndReply() {
        HttpRequestTestController handler = new HttpRequestTestController();

        // Test with a null message
        String responseNull = handler.handleRequest(null);
        assertEquals("Received message: null", responseNull);

        // Test with a non-null message
        String response = handler.handleRequest("Hello");
        assertEquals("Received message: Hello", response);
    }
}
