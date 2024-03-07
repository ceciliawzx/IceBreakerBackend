package com.icebreaker.services;

import com.icebreaker.serverrunner.ServerRunner;
import org.springframework.stereotype.Service;

@Service
public class MockRoomService {
    private final WordleService wordleService;
    private final HangmanService hangmanService;
    private final ServerRunner runner = ServerRunner.getInstance();

    public MockRoomService(WordleService wordleService, HangmanService hangmanService) {
        this.wordleService = wordleService;
        this.hangmanService = hangmanService;
    }

    public boolean restartMockRoom() {
        final String MOCK_ROOM_CODE = "TEST";
        wordleService.resetSession(MOCK_ROOM_CODE);
        hangmanService.resetSession(MOCK_ROOM_CODE);
        return runner.restartMockRoom();
    }
}
