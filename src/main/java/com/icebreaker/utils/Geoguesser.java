package com.icebreaker.utils;

import groovy.transform.Sealed;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.glassfish.grizzly.utils.Pair;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class Geoguesser {

    private int score = 0;
    private double correctLatitude;
    private double correctLongitude;

    @Setter
    @Getter
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

}
