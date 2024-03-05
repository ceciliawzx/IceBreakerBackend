package com.icebreaker.person;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class Admin extends Person {
    public Admin(@NonNull String displayName, @NonNull String roomCode, @NonNull String userID, String profileImage, String firstName, String lastName, String country, String city, String feeling, String favFood, String favActivity, boolean completed) {
        super(displayName, roomCode, userID, profileImage, firstName, lastName, country, city, feeling, favFood, favActivity, completed);
    }

    public Admin(@NonNull String displayName, @NonNull String roomCode, @NonNull String userID) {
        super(displayName, roomCode, userID);
    }
}

