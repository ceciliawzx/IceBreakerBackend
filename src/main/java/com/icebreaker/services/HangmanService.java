package com.icebreaker.services;

import com.icebreaker.dto.games.HangmanData;
import com.icebreaker.dto.room.Target;
import com.icebreaker.dto.websocket.BackMessage;
import com.icebreaker.dto.websocket.HangmanMessage;
import com.icebreaker.dto.websocket.ModalMessage;
import com.icebreaker.dto.websocket.TimerMessage;
import com.icebreaker.enums.RoomStatus;
import com.icebreaker.enums.WordleStateCode;
import com.icebreaker.serverrunner.ServerRunner;
import com.icebreaker.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HangmanService {

    private final Map<String, HangmanData> gameData = new HashMap<>();
    private final SimpMessagingTemplate messagingTemplate;
    private final ServerRunner runner = ServerRunner.getInstance();
    private final WaitRoomService waitRoomService;

    @Autowired
    public HangmanService(SimpMessagingTemplate messagingTemplate, WaitRoomService waitRoomService) {
        this.messagingTemplate = messagingTemplate;
        this.waitRoomService = waitRoomService;
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
        messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/hangman", modalMessage);
    }

    public void resetSession(String roomCode) {
        gameData.remove(roomCode);
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

        messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/hangman", message);
        if (isWordCorrect) {
            resetSession(roomCode);
        }
    }

    public void broadCastTimerStarted(String roomCode) {
        TimerMessage timerMessage = new TimerMessage();
        timerMessage.setStarted(true);
        messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/hangman", timerMessage);
    }

    public boolean startHangman(String roomCode, String userID, String field) {
        if (runner.changeRoomStatus(roomCode, RoomStatus.HANGMAN)) {
            String word = runner.getFieldValue(roomCode, userID, field);
            boolean result = setAnswers(roomCode, word, field);
            waitRoomService.broadcastMessage(roomCode);
            return result;
        }
        return false;
    }

    public Character[] getHangmanInfo(String roomCode) {
        if (roomExist(roomCode)) {
            return getCurrentStages(roomCode);
        }
        return null;
    }

    public String getHangmanAnswer(String roomCode) {
        if (roomExist(roomCode)) {
            Target target = getAnswer(roomCode);
            return JsonUtils.returnJson(Map.of("target", target), JsonUtils.unknownError);
        }
        return JsonUtils.returnRoomNotFoundJsonError();
    }

    public String getHangmanGameStatus(String roomCode) {
        if (roomExist(roomCode)) {
            try {
                return JsonUtils.returnJson(Map.of("hangmanmessage", getGameStatus(roomCode)), "Error fetching Hangman Status");
            } catch (Exception e) {
                return JsonUtils.returnJsonError(e.getMessage());
            }
        }
        return JsonUtils.returnRoomNotFoundJsonError();
    }
}
