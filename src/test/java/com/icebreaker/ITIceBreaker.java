package com.icebreaker;

import io.restassured.RestAssured;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class ITIceBreaker {

    @Test
    public void EndpointCanReceiveRequestAndResponse() {
        // Specify the base URL of your deployed server
        RestAssured.baseURI = "http://ljthey.co.uk:8080/";
        given()
                .queryParam("message", "Hello")
                .when()
                .get("/myEndpoint")
                .then()
                .statusCode(200) // expecting HTTP 200 OK
//                .body(equalTo("Received message: Hello"));
                .body(equalTo("Received message: Hello"));
    }


}
