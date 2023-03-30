package com.kourchenko.service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ResponseService {

    public static String parseResponse(String response, Throwable error, int status) {
        Map<String, Object> map = new HashMap<>();
        try {
            map.put("timestamp", Instant.now().toString());
            map.put("response", response);
            map.put("status", status);
            map.put("error", error != null ? error.getLocalizedMessage() : null);

            return new ObjectMapper().writeValueAsString(map);
        } catch (JsonProcessingException e) {
            return "";
        }
    }
}
