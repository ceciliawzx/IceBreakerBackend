package com.icebreaker.intergationTests;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class ITIceBreaker {

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "https://ljthey.co.uk:8080/";
    }

    @Test
    public void endpointCanReceiveRequestAndResponse() {
        given()
                .queryParam("message", "Hello")
                .when()
                .get("/myEndpoint")
                .then()
                .statusCode(200) // expecting HTTP 200 OK
                .body(equalTo("Received message: Hello"));
    }
}
