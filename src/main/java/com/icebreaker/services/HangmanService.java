package com.icebreaker.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icebreaker.room.Target;
import com.icebreaker.websocket.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class HangmanService {

    private final Map<String, HangmanData> gameData = new HashMap<>();
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public HangmanService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public boolean setAnswers(String roomCode, String answer, String fieldName) {
        if (!gameData.containsKey(roomCode)) {
            List<WordleStateCode> code = new ArrayList<>();
            for (int i = 0; i < 26; i++) {
                code.add(WordleStateCode.UNCHECKED);
            }
            Character[] currentStages = new Character[answer.length()];
            for (int i = 0; i < answer.length(); i++) {
                char temp = answer.charAt(i);
                if (!((temp >= 'a' && temp <= 'z') || (temp >= 'A' && temp <= 'Z'))) {
                    currentStages[i] = temp;
                }
            }
            gameData.put(roomCode, new HangmanData(
                    roomCode, fieldName, answer.toUpperCase(), currentStages, code, 0, 0, null));
            System.out.println("Set Hangman Answer: " + roomCode + " " + answer.toUpperCase());
            return true;
        }
        return false;
    }

    public Target getAnswer(String roomCode) {
        String fieldName = gameData.get(roomCode).getFieldName();
        String answer = gameData.get(roomCode).getAnswer();
        return new Target(fieldName, answer);
    }

    public Character[] getCurrentStages(String roomCode) {
        return gameData.get(roomCode).getGuessedLetters();
    }

    public boolean roomExist(String roomCode) {
        return gameData.containsKey(roomCode);
    }

    private List<Integer> checkLetter(String roomCode, Character guessLetter) {
        if (roomExist(roomCode)) {
            HangmanData data = gameData.get(roomCode);
            String answer = getAnswer(roomCode).getTargetWord();
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

    public void showModal(String roomCode) {
        ModalMessage modalMessage = new ModalMessage(roomCode, true);
        System.out.println("Send show modal message to Hangman Room");
        messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/hangman", modalMessage);
    }

    public boolean resetSession(String roomCode) {
        if (gameData.containsKey(roomCode)) {
            gameData.remove(roomCode);
            return true;
        }
        return false;
    }

    public HangmanMessage getGameStatus(String roomCode) {
        if (gameData.containsKey(roomCode)) {
            return gameData.get(roomCode).getPrevMessage();
        }
        return null;
    }

    public void broadcastResult(String roomCode, HangmanMessage message) {
        Character guessLetter = message.getGuessLetter();
        List<Integer> correctPositions = checkLetter(roomCode, guessLetter);
        boolean isThisLetterCorrect = correctPositions != null;

        message.setCorrectPositions(correctPositions);

        HangmanData data = gameData.get(roomCode);

        data.setCurrentGuesses(data.getCurrentGuesses() + 1);

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
        message.setCurrentGuesses(data.getCurrentGuesses());

        data.setPrevMessage(message);

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

    public void broadCastTimerStarted(String roomCode) {
        TimerMessage timerMessage = new TimerMessage();
        timerMessage.setStarted(true);
        messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/hangman", timerMessage);
    }
}
