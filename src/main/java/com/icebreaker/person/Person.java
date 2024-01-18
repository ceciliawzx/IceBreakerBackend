package com.icebreaker.person;

import lombok.*;
import java.net.InetSocketAddress;

@Data
@Builder
@RequiredArgsConstructor
public class Person {
    @NonNull
    protected InetSocketAddress address;
    @NonNull
    protected int id;
    @NonNull
    protected byte[] profileImage;
    @NonNull
    protected String firstName;
    @NonNull
    protected String lastName;
    @NonNull
    protected String country;
    @NonNull
    protected String city;
    @NonNull
    protected String feeling;
    @NonNull
    protected String favFood;
    @NonNull
    protected String favActivities;
}
