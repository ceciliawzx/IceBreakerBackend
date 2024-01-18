package com.icebreaker.person;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.net.InetSocketAddress;

@Builder
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class Person {
    @NonNull
    protected InetSocketAddress address;
    @NonNull
    protected String nickname;
    @NonNull
    protected Integer roomId;
    protected Integer id;
    protected byte[] profileImage;
    protected String firstName;
    protected String lastName;
    protected String country;
    protected String city;
    protected String feeling;
    protected String favFood;
    protected String favActivities;
}
