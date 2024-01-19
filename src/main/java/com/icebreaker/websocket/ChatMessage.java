package com.icebreaker.websocket;

import lombok.*;

import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    private int roomNumber;
    private String content;
    private LocalDateTime timestamp;
    private String sender;

    @Override
    public String toString() {
        return "Sender " + sender + " has sent a message: " + content + " in room: " + roomNumber + " at time: " + timestamp.toString();
    }
}

