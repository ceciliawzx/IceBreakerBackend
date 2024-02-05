package com.icebreaker.utils;

import com.icebreaker.person.Person;
import groovy.transform.Sealed;
import lombok.*;
import org.glassfish.grizzly.utils.Pair;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class Geoguesser {

    private int score = 0;
    private double correctLatitude;
    private double correctLongitude;

    @Setter
    @Getter
    @NonNull
    private GeoguesserStatus status;

    private List<Pair<String, Double>> guesses;

    public boolean startGame(String location) {
        this.score = 0;
        this.guesses = new ArrayList<>();
        String[] parts = location.split(",");
        this.correctLatitude = Double.parseDouble(parts[0].trim());
        this.correctLongitude = Double.parseDouble(parts[1].trim());
        this.status = GeoguesserStatus.PLAYER_CHOOSE;
        return true;
    }

    public boolean makeGuess(String userID, String location) {
        for (Pair<String, Double> pair : guesses) {
            if (pair.getFirst().equals(userID)) {
                return false;
            }
        }

        String[] parts = location.split(",");
        double latitude = Double.parseDouble(parts[0].trim());
        double longitude = Double.parseDouble(parts[1].trim());

        double distance = calculateDistance(latitude, longitude, correctLatitude, correctLongitude);
        guesses.add(new Pair<>(userID, distance));

        return true;
    }

    public int answersSumitted() {
        return guesses.size();
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        return Math.sqrt(Math.pow(lat1 - lat2, 2) + Math.pow(lon1 - lon2, 2));
    }

    public boolean checkNotSubmitted(String userID) {
        for (Pair<String, Double> pair : guesses) {
            if (pair.getFirst().equals(userID)) {
                return false;
            }
        }

        return true;
    }

    public List<String> geoGuesserWinner() {
        if (guesses == null || guesses.isEmpty()) {
            return new ArrayList<>();
        }

        double minDouble = guesses.get(0).getSecond();
        List<String> minStrings = new ArrayList<>();

        for (Pair<String, Double> pair : guesses) {
            double currentDouble = pair.getSecond();

            if (currentDouble < minDouble) {
                minDouble = currentDouble;
                minStrings.clear();
                minStrings.add(pair.getFirst());
            } else if (currentDouble == minDouble) {
                minStrings.add(pair.getFirst());
            }
        }

        return minStrings;
    }

}
