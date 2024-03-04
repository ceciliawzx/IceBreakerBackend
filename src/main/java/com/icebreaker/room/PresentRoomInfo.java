package com.icebreaker.room;

import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@Data
public class PresentRoomInfo {

    // true -> field has been revealed
    @Getter
    @Setter
    private boolean firstName;
    @Getter
    @Setter
    private boolean lastName;
    @Getter
    @Setter
    private boolean country;
    @Getter
    @Setter
    private boolean city;
    @Getter
    @Setter
    private boolean feeling;
    @Getter
    @Setter
    private boolean favFood;
    @Getter
    @Setter
    private boolean favActivity;

    public PresentRoomInfo(@NonNull boolean firstName, @NonNull boolean lastName, @NonNull boolean country, @NonNull boolean city, @NonNull boolean feeling, @NonNull boolean favFood, @NonNull boolean favActivity) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.country = country;
        this.city = city;
        this.feeling = feeling;
        this.favFood = favFood;
        this.favActivity = favActivity;
    }

    public PresentRoomInfo() {
        this.firstName = false;
        this.lastName = false;
        this.country = false;
        this.city = false;
        this.feeling = false;
        this.favFood = false;
        this.favActivity = false;
    }

    @Override
    public String toString() {
        return "PresentRoomInfo, firstName " + firstName + " lastName " + lastName + " country " + country + "city " + city + " feeling" +
                feeling + " favFood " + favFood + "favActivity " + favActivity;
    }
}
