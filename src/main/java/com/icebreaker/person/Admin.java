package com.icebreaker.person;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.net.InetSocketAddress;

@Getter
@Setter
public class Admin extends Person {

    public Admin(@NonNull String nickname, @NonNull Integer roomId, @NonNull String id, byte[] profileImage, String firstName, String lastName, String country, String city, String feeling, String favFood, String favActivities) {
        super(nickname, roomId, id, profileImage, firstName, lastName, country, city, feeling, favFood, favActivities);
    }
}

