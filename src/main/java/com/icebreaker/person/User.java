package com.icebreaker.person;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.net.InetSocketAddress;


@Getter
@Setter
public class User extends Person {

    public User(@NonNull InetSocketAddress address, @NonNull String nickname, @NonNull Integer roomId, int id, byte[] profileImage, String firstName, String lastName, String country, String city, String feeling, String favFood, String favActivities) {
        super(address, nickname, roomId, id, profileImage, firstName, lastName, country, city, feeling, favFood, favActivities);
    }
}

