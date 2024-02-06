package com.icebreaker.controllers.httphandlers;

import com.icebreaker.room.RoomStatus;
import com.icebreaker.serverrunner.ServerRunner;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

public class ShareBoardHandler {
    private final ServerRunner runner = ServerRunner.getInstance();

    @PostMapping("/startShareBoard")
    public boolean startShareBoard(@RequestParam(name = "roomCode", required = true) String roomCode) {
        if (runner.changeRoomStatus(roomCode, RoomStatus.SHAREBOARD)) {
            runner.setTargetInRoom(roomCode, "");
            return true;
        }
        return false;
    }
}
