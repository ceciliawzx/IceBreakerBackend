package com.icebreaker.person;

import lombok.*;

import java.net.InetSocketAddress;

@Data
@Builder
public class Person {
    @NonNull
    protected int id;
    @NonNull
    protected String nickName;
    protected InetSocketAddress address;
    protected byte[] profileImage;
    protected String firstName;
    protected String lastName;
    protected String country;
    protected String city;
    protected String feeling;
    protected String favFood;
    protected String favActivities;
}
