package com.icebreaker.person;

import lombok.Getter;
import lombok.NonNull;

import java.net.InetSocketAddress;

@Getter
public class User extends Person {

    public User(@NonNull InetSocketAddress address, @NonNull String nickname, @NonNull Integer roomId, Integer id, byte[] profileImage, String firstName, String lastName, String country, String city, String feeling, String favFood, String favActivities) {
        super(address, nickname, roomId, id, profileImage, firstName, lastName, country, city, feeling, favFood, favActivities);
    }
}
