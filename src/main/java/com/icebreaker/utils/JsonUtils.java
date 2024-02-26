package com.icebreaker.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class JsonUtils {

    public static final String roomNotFound = "Room Not Found";
    public static final String personNotFound = "Person Not Found";
    public static final String unknownError = "Unknown Error";


    public static String returnJson(Map<String, Object> objects, String errorMessage) {

        ObjectMapper objectMapper = new ObjectMapper();
        if (objects != null) {
            String json;
            try {
                json = objectMapper.writeValueAsString(objects);
            } catch (Exception e) {
                e.printStackTrace();
                json = "{\"error\": \"Serialization error\"}";
            }
            return json;
        }
        String jsonError;
        try {
            jsonError = objectMapper.writeValueAsString(Map.of("error", errorMessage));
        } catch (Exception e) {
            // Handle exception if JSON serialization fails
            e.printStackTrace();
            jsonError = "{\"error\": \"Serialization error\"}";
        }
        return jsonError;
    }

    public static String returnJsonError(String errorMessage) {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonError;
        try {
            jsonError = objectMapper.writeValueAsString(Map.of("error", errorMessage));
        } catch (Exception e) {
            // Handle exception if JSON serialization fails
            e.printStackTrace();
            jsonError = "{\"error\": \"Serialization error\"}";
        }
        return jsonError;
    }


    public static String returnRoomNotFoundJsonError() {
        return returnJsonError(roomNotFound);
    }

    public static String returnPersonNotFoundJsonError() {
        return returnJsonError(personNotFound);
    }

    public static String returnUnknownError() {
        return returnJsonError(unknownError);
    }
}
