package com.icebreaker.person;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.net.InetSocketAddress;

@Getter
@Setter
public class Admin extends Person {
    public Admin(@NonNull String nickName, @NonNull int id, @NonNull InetSocketAddress address, byte[] profileImage, @NonNull String firstName, @NonNull String lastName, @NonNull String country, String city, String feeling, String favFood, String favActivities) {
        super(id, nickName, address, profileImage, firstName, lastName, country, city, feeling, favFood, favActivities);
    }
}

