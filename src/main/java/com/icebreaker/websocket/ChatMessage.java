package com.icebreaker.websocket;

import lombok.*;

import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    private String roomCode;
    private String content;
    private LocalDateTime timestamp;
    private String sender;
    private String senderId;

    @Override
    public String toString() {
        return sender + " with id: " + senderId + " has sent a message: " + content + " in room: " + roomCode + " at time: " + timestamp.toString();
    }
}

