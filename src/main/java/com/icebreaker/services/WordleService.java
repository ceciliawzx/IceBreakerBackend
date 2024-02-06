package com.icebreaker.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icebreaker.websocket.BackMessage;
import com.icebreaker.websocket.WordleMessage;
import com.icebreaker.websocket.WordleStateCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class WordleService {
    private final Map<String, String> answers = new HashMap<String, String>();
    private final Map<String, List<WordleStateCode>> letterStates = new HashMap<>();
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public WordleService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
        // answers.put("1234", "GET");
    }

    public boolean setAnswers(String roomCode, String answer) {
        if (!answers.containsKey(roomCode)) {
            answers.put(roomCode, answer.toUpperCase());
            System.out.println("Set Answer: " + roomCode + " " + answer.toUpperCase());
            List<WordleStateCode> code = new ArrayList<>();
            for (int i = 0; i < 26; i++) {
                code.add(WordleStateCode.UNCHECKED);
            }
            letterStates.put(roomCode, code);
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

    public boolean resetSession(String roomCode) {
        if (answers.containsKey(roomCode)) {
            answers.remove(roomCode);
            letterStates.remove(roomCode);
            return true;
        }
        return false;
    }

    private boolean checkCorrectness(String roomCode, WordleMessage message) {
        boolean isCorrect = true;
        if (message.getIsCheck()) {
            List<List<WordleMessage.WordleLetter>> guesses = message.getLetters();
            int currentRound = message.getCurrentAttempt();
            List<WordleMessage.WordleLetter> guess = guesses.get(currentRound);
            String answer = answers.get(roomCode);
            for (int i = 0; i < answer.length(); i++) {
                Character currentChar = guess.get(i).getLetter().charAt(0);
                if (currentChar.equals(answer.charAt(i))) {
                    guess.get(i).setState(WordleStateCode.GREEN);
                    letterStates.get(roomCode).set(currentChar - 'A', WordleStateCode.GREEN);
                } else if (answer.contains(currentChar.toString())) {
                    guess.get(i).setState(WordleStateCode.YELLOW);
                    if (!letterStates.get(roomCode).get(currentChar - 'A').equals(WordleStateCode.GREEN)) {
                        letterStates.get(roomCode).set(currentChar - 'A', WordleStateCode.YELLOW);
                    }
                    isCorrect = false;
                } else {
                    guess.get(i).setState(WordleStateCode.GREY);
                    if ((!letterStates.get(roomCode).get(currentChar - 'A').equals(WordleStateCode.GREEN)) &&
                            (!letterStates.get(roomCode).get(currentChar - 'A').equals(WordleStateCode.YELLOW))) {
                        letterStates.get(roomCode).set(currentChar - 'A', WordleStateCode.GREY);
                    }
                    isCorrect = false;
                }
            }
        } else {
            isCorrect = false;
        }
        return isCorrect;
    }

    public void returnToPresentingRoom(String roomCode) {
        BackMessage backMessage = new BackMessage(roomCode);
        System.out.println("Send return to presenting room wordle back message");
        messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/wordle", backMessage);
    }

    public void broadcastResult(String roomCode, WordleMessage message) {
        boolean isCorrect = checkCorrectness(roomCode, message);

        message.setAllLetterStat(letterStates.get(roomCode));
        message.setIsCorrect(isCorrect);

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
        messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/wordle", message);
        if (isCorrect || (message.getCurrentAttempt() == message.getTotalAttempt() && message.getIsCheck())) {
            System.out.println("Remove Answer: " + roomCode);
            returnToPresentingRoom(roomCode);
        }
    }
}

