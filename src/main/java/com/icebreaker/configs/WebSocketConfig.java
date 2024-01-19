package com.icebreaker.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/chat/{userId}")
                .setAllowedOriginPatterns("*localhost*", "*ljthey*")
                .withSockJS();
    }



    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Each room will have its unique topic based on the room number
        registry.enableSimpleBroker("/topic/room");
        registry.setApplicationDestinationPrefixes("/app");
    }
}
