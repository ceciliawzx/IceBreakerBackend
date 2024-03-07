package com.icebreaker.dto.websocket;

import com.icebreaker.enums.RoomStatus;
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
    private boolean isStarted;

    @Override
    public String toString() {
        return "Timer message: roomCode " + roomCode + " roomStatus " + roomStatus + " seconds " + seconds;
    }
}
