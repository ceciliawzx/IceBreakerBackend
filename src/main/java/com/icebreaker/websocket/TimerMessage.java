package com.icebreaker.websocket;

import com.icebreaker.room.RoomStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimerMessage {
    private String roomCode;
    private RoomStatus roomStatus;
    private int seconds;

    @Override
    public String toString() {
        return "Timer message: roomCode " + roomCode + " roomStatus " + roomStatus + " seconds " + seconds;
    }
}
