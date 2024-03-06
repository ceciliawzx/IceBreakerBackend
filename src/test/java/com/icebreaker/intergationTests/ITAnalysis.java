package com.icebreaker.intergationTests;

import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;

import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ITAnalysis {

    @BeforeAll
    public static void setup() {
        // TODO: change this url after adding SSL info to flask run command
        RestAssured.baseURI = "https://localhost:8000/";
    }

    @Test
    public void testFetchReportsForUserIntegration() {
        String roomCode = "TEST";
        String userID = "1";

        Response response = given()
                .queryParam("roomCode", roomCode)
                .queryParam("userID", userID)
                .when()
                .get("/fetchReportsForUser") // Ensure the endpoint matches your Spring Controller's mapping
                .then()
                .statusCode(200) // Verify that the response status code is 200 OK
                .extract().response();

        // Print the response body
        System.out.println("Response Body:");
        System.out.println(response.asString());
    }
}
