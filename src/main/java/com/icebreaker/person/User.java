package com.icebreaker.person;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.net.InetSocketAddress;

@Getter
public class User extends Person {
    public User(@NonNull InetSocketAddress address, @NonNull int id, @NonNull byte[] profileImage, @NonNull String firstName, @NonNull String lastName, @NonNull String country, @NonNull String city, @NonNull String feeling, @NonNull String favFood, @NonNull String favActivities) {
        super(address, id, profileImage, firstName, lastName, country, city, feeling, favFood, favActivities);
    }
}
