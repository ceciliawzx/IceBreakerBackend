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
    private boolean firstName = false;
    @Getter
    @Setter
    private boolean lastName = false;
    @Getter
    @Setter
    private boolean country = false;
    @Getter
    @Setter
    private boolean city = false;
    @Getter
    @Setter
    private boolean feeling = false;
    @Getter
    @Setter
    private boolean favFood = false;
    @Getter
    @Setter
    private boolean favActivity = false;

    public PresentRoomInfo(@NonNull boolean firstName, @NonNull boolean lastName, @NonNull boolean country, @NonNull boolean city, @NonNull boolean feeling, @NonNull boolean favFood, @NonNull boolean favActivity) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.country = country;
        this.city = city;
        this.feeling = feeling;
        this.favFood = favFood;
        this.favActivity = favActivity;
    }

}
