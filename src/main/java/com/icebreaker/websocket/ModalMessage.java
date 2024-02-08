package com.icebreaker.websocket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ModalMessage {
    private String roomCode;
    private boolean show;
}
