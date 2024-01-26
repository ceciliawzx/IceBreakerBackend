package com.icebreaker.person;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.net.InetSocketAddress;

@Getter
@Setter
public class Admin extends Person {
    public Admin(@NonNull String displayName, @NonNull String roomCode, @NonNull String userID, String profileImage, String firstName, String lastName, String country, String city, String feeling, String favFood, String favActivity, boolean completed, boolean isPresenter) {
        super(displayName, roomCode, userID, profileImage, firstName, lastName, country, city, feeling, favFood, favActivity, completed, isPresenter);
    }

    public Admin(@NonNull String displayName, @NonNull String roomCode, @NonNull String userID) {
        super(displayName, roomCode, userID);
    }
}

