package com.icebreaker.dto.websocket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

