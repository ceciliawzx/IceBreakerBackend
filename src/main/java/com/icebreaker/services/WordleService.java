package com.icebreaker.services;

import com.icebreaker.websocket.WordleMessage;
import com.icebreaker.websocket.WordleStateCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WordleService {
    private final Map<String, String> answers = new HashMap<String, String>();
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public WordleService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void checkCorrectness(String roomCode, WordleMessage message) {
        if (message.getIsCheck()) {
            List<WordleMessage.Letters> guess = message.getLetters();
            String answer = answers.get(roomCode);
            for (int i = 0; i < answer.length(); i++) {
                Character currentChar = guess.get(i).getLetter();
                if (currentChar.equals(answer.charAt(i))) {
                    guess.get(i).setState(WordleStateCode.GREEN);
                } else if (answer.contains(currentChar.toString())) {
                    guess.get(i).setState(WordleStateCode.YELLOW);
                } else {
                    guess.get(i).setState(WordleStateCode.Grey);
                }
            }
        }
    }

    public void broadcastResult(String roomCode, String message) {
        messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/wordle", message);
    }
}
