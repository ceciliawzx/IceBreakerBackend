package com.icebreaker;

import com.icebreaker.websocket.ChatMessage;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class ITWebSocket {

    private final BlockingQueue<ChatMessage> blockingQueue = new LinkedBlockingQueue<>();
    private final String WEBSOCKET_URI = "http://ljthey.co.uk:8080/chat";
    private final String WEBSOCKET_TOPIC = "/topic/room/1";
//
//    @Test
//    public void testSendMessage() throws Exception {
//        WebSocketStompClient stompClient = new WebSocketStompClient(new SockJsClient(createTransportClient()));
//        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
//
//        StompSession session = stompClient.connectAsync(WEBSOCKET_URI, new StompSessionHandlerAdapter() {
//        }).get(1, TimeUnit.SECONDS);
//        session.subscribe(WEBSOCKET_TOPIC, new DefaultStompFrameHandler());
//
//        ChatMessage message = ChatMessage.builder()
//                .roomNumber(1)
//                .content("Hello, room 1!")
//                .timestamp(LocalDateTime.now())
//                .sender("TestUser")
//                .build();
//
//        session.send("/app/room/1/sendMessage", message);
//
//        ChatMessage receivedMessage = blockingQueue.poll(5, TimeUnit.SECONDS);
//
//        Assert.assertNotNull(receivedMessage);
//        Assert.assertEquals("Hello, room 1!", receivedMessage.getContent());
//    }
//
//    private List<Transport> createTransportClient() {
//        return Collections.singletonList(new WebSocketTransport(new StandardWebSocketClient()));
//    }
//
//    private class DefaultStompFrameHandler implements StompFrameHandler {
//        @Override
//        public Type getPayloadType(StompHeaders headers) {
//            return ChatMessage.class;
//        }
//
//        @Override
//        public void handleFrame(StompHeaders headers, Object payload) {
//            blockingQueue.offer((ChatMessage) payload);
//        }
//    }
}
