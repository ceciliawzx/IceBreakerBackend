//package com.icebreaker;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import java.time.LocalDateTime;
//import java.util.concurrent.ArrayBlockingQueue;
//import java.util.concurrent.BlockingQueue;
//import java.util.concurrent.TimeUnit;
//
//import com.icebreaker.websocket.ChatMessage;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.web.server.LocalServerPort;
//import org.springframework.messaging.converter.MappingJackson2MessageConverter;
//import org.springframework.messaging.simp.stomp.StompFrameHandler;
//import org.springframework.messaging.simp.stomp.StompHeaders;
//import org.springframework.messaging.simp.stomp.StompSession;
//import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//import org.springframework.web.socket.client.WebSocketClient;
//import org.springframework.web.socket.client.standard.StandardWebSocketClient;
//import org.springframework.web.socket.messaging.WebSocketStompClient;
//import org.springframework.web.socket.sockjs.client.SockJsClient;
//import org.springframework.web.socket.sockjs.client.WebSocketTransport;
//
//import java.lang.reflect.Type;
//import java.util.List;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {IceBreaker.class})
//@ContextConfiguration(classes = IceBreaker.class)
//public class ITWebSocket {
//
//    @LocalServerPort
//    private int port;
//
//    @Test
//    public void verifyMessageExchange() throws Exception {
//        WebSocketClient simpleClient = new StandardWebSocketClient();
//        WebSocketStompClient stompClient = new WebSocketStompClient(new SockJsClient(List.of(new WebSocketTransport(simpleClient))));
//        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
//
//        BlockingQueue<ChatMessage> blockingQueue = new ArrayBlockingQueue<>(1);
//
//        String url = "ws://localhost:" + port + "/chat";
//        StompSession session = stompClient.connect(url, new StompSessionHandlerAdapter() {}).get(1, TimeUnit.SECONDS);
//
//        session.subscribe("/topic/room/1", new StompFrameHandler() {
//            @Override
//            public Type getPayloadType(StompHeaders headers) {
//                return ChatMessage.class;
//            }
//
//            @Override
//            public void handleFrame(StompHeaders headers, Object payload) {
//                blockingQueue.add((ChatMessage) payload);
//            }
//        });
//
//        ChatMessage testMessage = ChatMessage.builder()
//                .roomNumber(1)
//                .content("Hello, world!")
//                .timestamp(LocalDateTime.now())
//                .sender("TestUser")
//                .build();
//
//        session.send("/app/room/1/sendMessage", testMessage);
//
//        ChatMessage receivedMessage = blockingQueue.poll(1, TimeUnit.SECONDS);
//        assertThat(receivedMessage).isEqualToComparingFieldByField(testMessage);
//
//        session.disconnect();
//    }
//}
