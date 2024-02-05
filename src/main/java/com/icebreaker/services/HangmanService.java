package com.icebreaker.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icebreaker.websocket.BackMessage;
import com.icebreaker.websocket.HangmanMessage;
import com.icebreaker.websocket.WordleMessage;
import com.icebreaker.websocket.WordleStateCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class HangmanService {
    private final Map<String, String> answers = new HashMap<String, String>();
    private final Map<String, Character[]> guessedLetters = new HashMap<>();
    private final Map<String, List<WordleStateCode>> letterStates = new HashMap<>();
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public HangmanService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
        setAnswers("1234", "GOOD");
    }

    public boolean setAnswers(String roomCode, String answer) {
        if (!answers.containsKey(roomCode)) {
            answers.put(roomCode, answer.toUpperCase());
            guessedLetters.put(roomCode, new Character[answer.length()]);
            List<WordleStateCode> code = new ArrayList<>();
            for (int i = 0; i < 26; i++) {
                code.add(WordleStateCode.UNCHECKED);
            }
            letterStates.put(roomCode, code);
            System.out.println("Set Hangman Answer: " + roomCode + " " + answer.toUpperCase());
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

    private List<Integer> checkLetter(String roomCode, Character guessLetter) {
        if (roomExist(roomCode)) {
            String answer = getAnswer(roomCode);
            List<Integer> positions = new ArrayList<>();
            for (int i = 0; i < answer.length(); i++) {
                if (guessLetter.equals(answer.charAt(i))) {
                    positions.add(i);
                    guessedLetters.get(roomCode)[i] = guessLetter;
                }
            }

            if (positions.isEmpty()) {
                letterStates.get(roomCode).set(guessLetter - 'A', WordleStateCode.GREY);
            } else {
                letterStates.get(roomCode).set(guessLetter - 'A', WordleStateCode.GREEN);
            }
            return positions.isEmpty() ? null : positions;
        }
        letterStates.get(roomCode).set(guessLetter - 'A', WordleStateCode.GREY);
        return null;
    }

    public void returnToPresentingRoom(String roomCode) {
        BackMessage backMessage = new BackMessage(roomCode);
        messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/hangman", backMessage);
    }

    public void broadcastResult(String roomCode, HangmanMessage message) {
        Character guessLetter = message.getGuessLetter();
        List<Integer> correctPositions = checkLetter(roomCode, guessLetter);
        boolean isCorrect = correctPositions != null;



        message.setCorrectPositions(correctPositions);
        message.setIsCorrect(isCorrect);

        Character[] currentStages = guessedLetters.get(roomCode);
        boolean isFinish = true;
        for (Character c : currentStages) {
            if (c == null) {
                isFinish = false;
                break;
            }
        }

        message.setIsFinished(isFinish);
        message.setAllLetterStat(letterStates.get(roomCode));
        message.setCurrentStages(currentStages);

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
        messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/hangman", message);
        if (isFinish) {
            System.out.println("Remove Hangman Answer: " + roomCode);
            answers.remove(roomCode);
            guessedLetters.remove(roomCode);
            letterStates.remove(roomCode);
        }
    }
}
