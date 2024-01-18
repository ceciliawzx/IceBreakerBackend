package com.icebreaker.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

//@CrossOrigin(origins = "*")
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Register a WebSocket endpoint and enable SockJS fallback options
        registry.addEndpoint("/chat")
                .setAllowedOrigins("*")
                .setAllowedOrigins("http://localhost:3000")
                .withSockJS();
    }

//    @Override
//    public void registerStompEndpoints(StompEndpointRegistry registry) {
//        registry.addEndpoint("/chat")
//                .setAllowedOrigins("http://localhost:3000") // Set allowed origins here
//                .withSockJS();
//    }


    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Each room will have its unique topic based on the room number
        registry.enableSimpleBroker("/topic/room");
        registry.setApplicationDestinationPrefixes("/app");
    }
}
