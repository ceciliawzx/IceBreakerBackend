//package com.icebreaker;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.icebreaker.websocket.ChatMessage;
//import org.java_websocket.client.WebSocketClient;
//import org.java_websocket.handshake.ServerHandshake;
//
//import java.lang.reflect.Type;
//import java.net.URI;
//import java.net.URISyntaxException;
//import java.time.LocalDateTime;
//import java.util.Collections;
//import java.util.List;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.TimeUnit;
//import org.junit.Assert;
//import org.junit.Test;
//import org.springframework.messaging.converter.MappingJackson2MessageConverter;
//import org.springframework.messaging.simp.stomp.StompFrameHandler;
//import org.springframework.messaging.simp.stomp.StompHeaders;
//import org.springframework.messaging.simp.stomp.StompSession;
//import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
//import org.springframework.web.socket.client.standard.StandardWebSocketClient;
//import org.springframework.web.socket.messaging.WebSocketStompClient;
//import org.springframework.web.socket.sockjs.client.SockJsClient;
//import org.springframework.web.socket.sockjs.client.Transport;
//import org.springframework.web.socket.sockjs.client.WebSocketTransport;
//
//public class ITWebSocket {
//
//    @Test
//    public void testSendMessage() throws Exception {
//        WebSocketStompClient stompClient = new WebSocketStompClient(new SockJsClient(createTransportClient()));
//        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
//
//        StompSession session = stompClient.connect("http://ljthey.co.uk:8080/chat", new StompSessionHandlerAdapter() {
//        }).get(1, TimeUnit.SECONDS);
//
//        // Subscribe to the topic to receive messages from the server
//        session.subscribe("/topic/room/0", new StompFrameHandler() {
//            @Override
//            public Type getPayloadType(StompHeaders headers) {
//                return ChatMessage.class;
//            }
//
//            @Override
//            public void handleFrame(StompHeaders headers, Object payload) {
//                ChatMessage message = (ChatMessage) payload;
//                System.out.println("Received: " + message.getContent());
//                Assert.assertEquals("Hello, room 0!", message.getContent());
//                session.disconnect();
//            }
//        });
//
//        // Send a message
//        ChatMessage chatMessage = new ChatMessage(0, "Hello, room 0!", LocalDateTime.now(), "TestUser");
//        session.send("/app/room/0/sendMessage", chatMessage);
//
//        // Sleep to allow message exchange
//        Thread.sleep(5000); // Adjust this time as needed
//    }
//
//    private List<Transport> createTransportClient() {
//        return Collections.singletonList(new WebSocketTransport(new StandardWebSocketClient()));
//    }
//}
