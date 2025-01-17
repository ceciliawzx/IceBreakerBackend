package com.icebreaker.dto.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeoguesserMessage {
    private String roomCode;
    private String displayName;
    private String location;
}
