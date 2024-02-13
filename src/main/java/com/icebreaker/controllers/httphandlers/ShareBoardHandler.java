package com.icebreaker.controllers.httphandlers;

import com.icebreaker.room.RoomStatus;
import com.icebreaker.room.Target;
import com.icebreaker.serverrunner.ServerRunner;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ShareBoardHandler {
    private final ServerRunner runner = ServerRunner.getInstance();

    @PostMapping("/startShareBoard")
    public boolean startShareBoard(@RequestParam(name = "roomCode", required = true) String roomCode,
                                   @RequestParam(name = "fieldName", required = true) String fieldName) {
        if (runner.changeRoomStatus(roomCode, RoomStatus.SHAREBOARD)) {
            runner.setTargetInRoom(roomCode, new Target(fieldName, ""));
            return true;
        }
        return false;
    }
}
