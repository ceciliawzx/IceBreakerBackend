package com.icebreaker.services;

import com.icebreaker.enums.RoomStatus;
import com.icebreaker.dto.room.Target;
import com.icebreaker.serverrunner.ServerRunner;
import com.icebreaker.utils.JsonUtils;
import com.icebreaker.enums.WordleStateCode;
import com.icebreaker.dto.websocket.BackMessage;
import com.icebreaker.dto.websocket.ModalMessage;
import com.icebreaker.dto.websocket.TimerMessage;
import com.icebreaker.dto.websocket.WordleMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class WordleService {
    private final Map<String, Target> answers = new HashMap<>();
    private final Map<String, List<WordleStateCode>> letterStates = new HashMap<>();
    private final Map<String, WordleMessage> gameStatus = new HashMap<>();
    private final SimpMessagingTemplate messagingTemplate;
    private final ServerRunner runner = ServerRunner.getInstance();
    private final WaitRoomService waitRoomService;

    @Autowired
    public WordleService(SimpMessagingTemplate messagingTemplate, WaitRoomService waitRoomService) {
        this.messagingTemplate = messagingTemplate;
        this.waitRoomService = waitRoomService;
    }

    public boolean setAnswers(String roomCode, String fieldName, String answer) {
        if (!answers.containsKey(roomCode)) {
            answers.put(roomCode, new Target(fieldName, answer.toUpperCase()));
            List<WordleStateCode> code = new ArrayList<>();
            for (int i = 0; i < 26; i++) {
                code.add(WordleStateCode.UNCHECKED);
            }
            letterStates.put(roomCode, code);
            gameStatus.put(roomCode, null);
            return true;
        }
        return false;
    }

    public Target getAnswer(String roomCode) {
        return answers.get(roomCode);
    }

    public boolean roomExist(String roomCode) {
        return answers.containsKey(roomCode);
    }

    public void resetSession(String roomCode) {
        if (answers.containsKey(roomCode)) {
            answers.remove(roomCode);
            letterStates.remove(roomCode);
            gameStatus.remove(roomCode);
        }
    }

    public WordleMessage getGameStatus(String roomCode) {
        if (gameStatus.containsKey(roomCode)) {
            return gameStatus.get(roomCode);
        }
        return null;
    }

    private boolean checkCorrectness(String roomCode, WordleMessage message) {
        boolean isCorrect = true;
        if (message.getIsCheck()) {
            List<List<WordleMessage.WordleLetter>> guesses = message.getLetters();
            int currentRound = message.getCurrentAttempt();
            List<WordleMessage.WordleLetter> guess = guesses.get(currentRound);
            Target answer = answers.get(roomCode);
            String answerWord = answer.getTargetWord();
            for (int i = 0; i < answerWord.length(); i++) {
                Character currentChar = guess.get(i).getLetter().charAt(0);
                if (currentChar.equals(answerWord.charAt(i))) {
                    guess.get(i).setState(WordleStateCode.GREEN);
                    letterStates.get(roomCode).set(currentChar - 'A', WordleStateCode.GREEN);
                } else if (answerWord.contains(currentChar.toString())) {
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
        messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/wordle", backMessage);
    }

    public void showModal(String roomCode) {
        ModalMessage modalMessage = new ModalMessage(roomCode, true);
        messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/wordle", modalMessage);
    }

    public void broadcastResult(String roomCode, WordleMessage message) {
        boolean isCorrect = checkCorrectness(roomCode, message);

        message.setAllLetterStat(letterStates.get(roomCode));
        message.setIsCorrect(isCorrect);

        messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/wordle", message);
        gameStatus.put(roomCode, message);
        if (isCorrect || (Objects.equals(message.getCurrentAttempt(), message.getTotalAttempt()) && message.getIsCheck())) {
            resetSession(roomCode);
        }
    }

    public void broadCastTimerStarted(String roomCode) {
        TimerMessage timerMessage = new TimerMessage();
        timerMessage.setStarted(true);
        messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/wordle", timerMessage);
    }

    public boolean startWordle(String roomCode, String userID, String field) {
        if (runner.changeRoomStatus(roomCode, RoomStatus.WORDLING)) {
            String word = runner.getFieldValue(roomCode, userID, field);
            boolean result = setAnswers(roomCode, field, word);
            waitRoomService.broadcastMessage(roomCode);
            return result;
        }
        return false;
    }

    public int getWordleInfo(String roomCode) {
        if (roomExist(roomCode)) {
            return getAnswer(roomCode).getTargetWord().length();
        }
        return -1;
    }

    public String getWordleAnswer(String roomCode) {
        if (roomExist(roomCode)) {
            Target target = getAnswer(roomCode);
            return JsonUtils.returnJson(Map.of("target", target), "Error fetching Wordle answer");
        }
        return JsonUtils.returnRoomNotFoundJsonError();
    }

    public String getWordleGameStatus(String roomCode) {
        if (roomExist(roomCode)) {
            try {
                return JsonUtils.returnJson(Map.of("wordlemessage", getGameStatus(roomCode)), "Error fetching Wordle Status");
            } catch (Exception e) {
                return JsonUtils.returnJsonError("Error fetching Wordle Status");
            }
        }
        return JsonUtils.returnRoomNotFoundJsonError();
    }
}

