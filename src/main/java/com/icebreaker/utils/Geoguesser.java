package com.icebreaker.utils;

import com.icebreaker.person.Person;
import groovy.transform.Sealed;
import lombok.*;
import org.glassfish.grizzly.utils.Pair;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static java.lang.Math.toRadians;

@RequiredArgsConstructor
public class Geoguesser {

    private static final double EARTH_RADIUS = 6371;
    private int score = 0;
    private double correctLatitude;
    private double correctLongitude;
    @Getter
    private String location;

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
        this.location = location;
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
        lat1 = toRadians(lat1);
        lon1 = toRadians(lon1);
        lat2 = toRadians(lat2);
        lon2 = toRadians(lon2);

        // Calculate the differences in latitude and longitude
        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;

        // Calculate the distance using Haversine formula
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(lat1) * Math.cos(lat2) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = EARTH_RADIUS * c;

        return distance;
    }

    public boolean checkNotSubmitted(String userID) {
        if (guesses == null || guesses.isEmpty()) {
            return true;
        }

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

    public List<Pair<String, Double>> geoGuesserRank() {
        if (guesses == null) {
            return Collections.emptyList();
        }
        guesses.sort(Comparator.comparingDouble(Pair::getSecond));
        return guesses;
    }

}
