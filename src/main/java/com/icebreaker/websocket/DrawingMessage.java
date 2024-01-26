package com.icebreaker.websocket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DrawingMessage {

    @Data
    public static class DrawingData {
        private double x;
        private double y;
        private boolean drawing;
        private String color;
        private String strokeWidth;
    }

    private int roomCode;
    // such as stroke data (coordinates, color, thickness), the user who drew it, and the room it belongs to.
    private DrawingData drawingData;
    private LocalDateTime timestamp;
    private String drawer; // UserID of the drawer

    @Override
    public String toString() {
        return drawer + " has sent a message: " + drawingData + " in room: " + roomCode + " at time: " + timestamp.toString();
    }
}
