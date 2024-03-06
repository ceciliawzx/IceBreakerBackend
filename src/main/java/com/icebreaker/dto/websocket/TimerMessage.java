package com.icebreaker.dto.websocket;

import com.icebreaker.enums.RoomStatus;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimerMessage {
    private String roomCode;
    private RoomStatus roomStatus;
    private int seconds;
    private boolean isStarted;

    @Override
    public String toString() {
        return "Timer message: roomCode " + roomCode + " roomStatus " + roomStatus + " seconds " + seconds;
    }
}
