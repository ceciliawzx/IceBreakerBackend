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
//    private final Map<String, String> answers = new HashMap<String, String>();
//    private final Map<String, Character[]> guessedLetters = new HashMap<>();
//    private final Map<String, List<WordleStateCode>> letterStates = new HashMap<>();
    private final Map<String, HangmanData> gameData = new HashMap<>();
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public HangmanService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public boolean setAnswers(String roomCode, String answer) {
        if (!gameData.containsKey(roomCode)) {
            List<WordleStateCode> code = new ArrayList<>();
            for (int i = 0; i < 26; i++) {
                code.add(WordleStateCode.UNCHECKED);
            }
            gameData.put(roomCode, new HangmanData(
                    roomCode, answer.toUpperCase(), new Character[answer.length()], code, 0));
            System.out.println("Set Hangman Answer: " + roomCode + " " + answer.toUpperCase());
            return true;
        }
        return false;
    }

    public String getAnswer(String roomCode) {
        return gameData.get(roomCode).getAnswer();
    }

    public boolean roomExist(String roomCode) {
        return gameData.containsKey(roomCode);
    }

    private List<Integer> checkLetter(String roomCode, Character guessLetter) {
        if (roomExist(roomCode)) {
            HangmanData data = gameData.get(roomCode);
            String answer = getAnswer(roomCode);
            List<Integer> positions = new ArrayList<>();
            for (int i = 0; i < answer.length(); i++) {
                if (guessLetter.equals(answer.charAt(i))) {
                    positions.add(i);
                    data.getGuessedLetters()[i] = guessLetter;
                }
            }

            if (positions.isEmpty()) {
                data.getLetterStates().set(guessLetter - 'A', WordleStateCode.GREY);
            } else {
                data.getLetterStates().set(guessLetter - 'A', WordleStateCode.GREEN);
            }
            return positions.isEmpty() ? null : positions;
        }
        return null;
    }

    public void returnToPresentingRoom(String roomCode) {
        BackMessage backMessage = new BackMessage(roomCode);
        messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/hangman", backMessage);
    }

    public boolean resetSession(String roomCode) {
        if (gameData.containsKey(roomCode)) {
            gameData.remove(roomCode);
            return true;
        }
        return false;
    }

    public void broadcastResult(String roomCode, HangmanMessage message) {
        Character guessLetter = message.getGuessLetter();
        List<Integer> correctPositions = checkLetter(roomCode, guessLetter);
        boolean isThisLetterCorrect = correctPositions != null;

        message.setCorrectPositions(correctPositions);

        HangmanData data = gameData.get(roomCode);

        Character[] currentStages = data.getGuessedLetters();
        boolean isWordCorrect = true;
        for (Character c : currentStages) {
            if (c == null) {
                isWordCorrect = false;
                break;
            }
        }

        if (!isThisLetterCorrect) {
            data.setCurrentWrongGuesses(data.getCurrentWrongGuesses() + 1);
        }

        boolean isFinish = data.getCurrentWrongGuesses() == data.MAX_WRONG_GUESSES || isWordCorrect;

        message.setIsCorrect(isWordCorrect);
        message.setIsFinished(isFinish);
        message.setAllLetterStat(data.getLetterStates());
        message.setCurrentStages(currentStages);
        message.setCurrentWrongGuesses(data.getCurrentWrongGuesses());

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
        if (isWordCorrect) {
            System.out.println("Remove Hangman Answer: " + roomCode);
            resetSession(roomCode);
        }
    }
}
