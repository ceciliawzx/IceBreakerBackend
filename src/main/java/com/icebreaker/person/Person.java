package com.icebreaker.person;

import com.icebreaker.utils.Constants;
import lombok.*;

@Data
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor
public class Person {
    @NonNull
    protected String displayName;
    @NonNull
    protected String roomCode;
    @NonNull
    protected String userID;
    protected String profileImage = Constants.getYellowDuck();
    protected String firstName;
    protected String lastName;
    protected String country;
    protected String city;
    protected String feeling;
    protected String favFood;
    protected String favActivity;
    protected boolean completed;
}
