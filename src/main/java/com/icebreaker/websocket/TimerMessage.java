package com.icebreaker.websocket;

import com.icebreaker.room.RoomStatus;
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
