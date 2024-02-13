package com.icebreaker.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class JsonUtils {

    public static String returnJson(String objectName, Object object) {
        ObjectMapper objectMapper = new ObjectMapper();
        if (object != null) {
            String json;
            try {
                json = objectMapper.writeValueAsString(Map.of(objectName, object));
            } catch (Exception e) {
                e.printStackTrace();
                json = "{\"error\": \"Serialization error\"}";
            }
            return json;
        }
        String jsonError;
        try {
            jsonError = objectMapper.writeValueAsString(Map.of("error", "error fetching presentRoomInfo"));
        } catch (Exception e) {
            // Handle exception if JSON serialization fails
            e.printStackTrace();
            jsonError = "{\"error\": \"Serialization error\"}";
        }
        return jsonError;
    }
}
