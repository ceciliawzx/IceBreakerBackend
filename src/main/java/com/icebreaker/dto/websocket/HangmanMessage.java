package com.icebreaker.dto.websocket;

import com.icebreaker.enums.WordleStateCode;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HangmanMessage {
    private Character guessLetter;
    private Boolean isCorrect;
    private List<Integer> correctPositions;
    private Character[] currentStages;
    private List<WordleStateCode> allLetterStat;
    private Boolean isFinished;
    private String roomCode;
    private Integer currentWrongGuesses;
    private Integer currentGuesses;
}
