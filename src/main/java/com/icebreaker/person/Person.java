package com.icebreaker.person;

import lombok.*;
import java.net.InetSocketAddress;

@Data
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
    protected int id;
    protected byte[] profileImage;
    protected String firstName;
    protected String lastName;
    protected String country;
    protected String city;
    protected String feeling;
    protected String favFood;
    protected String favActivities;
}
