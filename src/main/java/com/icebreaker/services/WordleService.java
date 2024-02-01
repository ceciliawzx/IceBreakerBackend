package com.icebreaker.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icebreaker.websocket.WordleMessage;
import com.icebreaker.websocket.WordleStateCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;


@Service
public class WordleService {
    private final Map<String, String> answers = new HashMap<String, String>();
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public WordleService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
        answers.put("1234", "GET");
    }

    public boolean setAnswers(String roomCode, String answer) {
        if (!answers.containsKey(roomCode)) {
            answers.put(roomCode, answer);
            return true;
        }
        return false;
    }

    public String getAnswer(String roomCode) {
        return answers.get(roomCode);
    }

    public boolean roomExist(String roomCode) {
        return answers.containsKey(roomCode);
    }

    private boolean checkCorrectness(String roomCode, WordleMessage message) {
        boolean isCorrect = true;
        if (message.getIsCheck()) {
            List<WordleMessage.Letters> guess = message.getLetters();
            String answer = answers.get(roomCode);
            for (int i = 0; i < answer.length(); i++) {
                Character currentChar = guess.get(i).getLetter();
                if (currentChar.equals(answer.charAt(i))) {
                    guess.get(i).setState(WordleStateCode.GREEN);
                } else if (answer.contains(currentChar.toString())) {
                    guess.get(i).setState(WordleStateCode.YELLOW);
                    isCorrect = false;
                } else {
                    guess.get(i).setState(WordleStateCode.Grey);
                    isCorrect = false;
                }
            }
        } else {
            isCorrect = false;
        }
        return isCorrect;
    }

    public void broadcastResult(String roomCode, WordleMessage message) {
        if (checkCorrectness(roomCode, message)) {
            answers.remove(roomCode);
        }
//        WordleMessage msg = new WordleMessage();
//        WordleMessage.Letters let1 = new WordleMessage.Letters('G', WordleStateCode.UNCHECKED);
//        WordleMessage.Letters let2 = new WordleMessage.Letters('E', WordleStateCode.UNCHECKED);
//        WordleMessage.Letters let3 = new WordleMessage.Letters('T', WordleStateCode.UNCHECKED);
//        List<WordleMessage.Letters> lets = new ArrayList<>();
//        lets.add(let1);
//        lets.add(let2);
//        lets.add(let3);
//        msg.setLetters(lets);
//        msg.setIsCheck(false);
//        msg.setRoomCode("1234");

        ObjectMapper objectMapper = new ObjectMapper();
        String json;

        try {
            json = objectMapper.writeValueAsString(message);
        } catch (Exception e) {
            // Handle exception if JSON serialization fails
            e.printStackTrace();
            json = "{\"error\": \"Serialization error\"}"; // A fallback JSON response in case of an error
        }
        System.out.println(json);
        messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/wordle", json);
    }
}

