package com.icebreaker.websocket;

import lombok.Data;
import java.time.LocalDateTime;

import com.icebreaker.person.Person;

@Data
public class ChatMessage {
    private String content;
    private int roomNumber;
    private LocalDateTime timestamp;
    private Person sender;
}
