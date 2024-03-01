package com.icebreaker.controllers.httphandlers;

import com.icebreaker.room.PresentRoomInfo;
import com.icebreaker.serverrunner.ServerRunner;
import com.icebreaker.services.DrawingService;
import com.icebreaker.services.HangmanService;
import com.icebreaker.services.WordleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MockRoomHandler {
    private final WordleService wordleService;
    private final HangmanService hangmanService;
    private final ServerRunner runner = ServerRunner.getInstance();

    @Autowired
    public MockRoomHandler(WordleService wordleService, HangmanService hangmanService) {
        this.wordleService = wordleService;
        this.hangmanService = hangmanService;
    }

    @PostMapping("/restartMockRoom")
    public boolean restartMockRoom() {
        System.out.println("Restart Mock Room");
        final String MOCK_ROOM_CODE = "TEST";
        wordleService.resetSession(MOCK_ROOM_CODE);
        hangmanService.resetSession(MOCK_ROOM_CODE);
        return runner.restartMockRoom();
    }
}
