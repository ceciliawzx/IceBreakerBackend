package com.icebreaker.services;

import com.icebreaker.websocket.HangmanMessage;
import com.icebreaker.utils.WordleStateCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HangmanData {
    public final int MAX_WRONG_GUESSES = 6;
    private String roomCode;
    private String fieldName;
    private String answer;
    private Character[] guessedLetters;
    private List<WordleStateCode> letterStates;
    private int currentWrongGuesses;
    private int currentGuesses;
    private HangmanMessage prevMessage;
}
