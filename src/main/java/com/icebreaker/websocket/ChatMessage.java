package com.icebreaker.websocket;

import lombok.*;

import java.time.LocalDateTime;


@Data
@Builder
@RequiredArgsConstructor
public class ChatMessage {
    @NonNull
    private int roomNumber;
    @NonNull
    private String content;
    @NonNull
    private LocalDateTime timestamp;
    @NonNull
    private String sender;
}
