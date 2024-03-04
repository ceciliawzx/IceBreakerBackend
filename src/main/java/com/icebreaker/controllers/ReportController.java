package com.icebreaker.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icebreaker.person.Person;
import com.icebreaker.serverrunner.ServerRunner;
import com.icebreaker.utils.JsonUtils;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class ReportController {

    private final ServerRunner runner = ServerRunner.getInstance();
    private final ObjectMapper objectMapper; // For JSON parsing
    private final RestTemplate restTemplate;
    private final Map<String, Map<String, Object>> allReports = new ConcurrentHashMap<>();

    public ReportController(RestTemplateBuilder restTemplateBuilder, ObjectMapper objectMapper) {
        this.restTemplate = restTemplateBuilder.build();
        this.objectMapper = objectMapper;
    }

    public void fetchReports(String roomCode) {
        if (allReports.containsKey(roomCode)) {
            return; // Return cached reports if available
        }
        HttpHeaders headers = new HttpHeaders();
        List<Person> players = runner.getRoom(roomCode).getPlayers();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<List<Person>> request = new HttpEntity<>(players, headers);

        // TODO: change this url after cicd works
        String pythonServiceUrl = "http://localhost:8000/generate_reports";
        ResponseEntity<String> response = restTemplate.postForEntity(pythonServiceUrl, request, String.class);

        // Parse the JSON response and store it in allReports
        try {
            Object reports = objectMapper.readValue(response.getBody(), Object.class); // Parse JSON to Object
            System.out.println(reports);
            allReports.put(roomCode, (Map<String, Object>) reports); // Cache the reports
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/fetchReportsForUser")
    public String fetchReportsForUser(@RequestParam(name = "roomCode") String roomCode,
                                      @RequestParam(name = "userID") String userID) {
        if (!allReports.containsKey(roomCode)) {
            fetchReports(roomCode);
        }

        Map<String, Object> reports = allReports.get(roomCode);
        Object userReport = reports.getOrDefault(userID, "No report available for this user.");

        return JsonUtils.returnJson(Map.of("reports", userReport), JsonUtils.unknownError);
    }


    @GetMapping("/fetchReportOfUser")
    public String fetchReportOfUser(@RequestParam(name = "roomCode") String roomCode,
                                    @RequestParam(name = "userID1") String userID1,
                                    @RequestParam(name = "userID2") String userID2) {
        if (!allReports.containsKey(roomCode)) {
            fetchReports(roomCode);
        }

        Map<String, Object> reports = allReports.get(roomCode);
        Map<String, Object> user1Reports = (Map<String, Object>) reports.getOrDefault(userID1, Collections.emptyMap());

        Map<String, String> reportForUser2 = new HashMap<>();
        for (Map.Entry<String, Object> entry : user1Reports.entrySet()) {
            String category = entry.getKey();
            Map<String, String> details = (Map<String, String>) entry.getValue();
            if (details.containsKey(userID2)) {
                reportForUser2.put(category, details.get(userID2));
            }
        }
        return JsonUtils.returnJson(Map.of("report", reportForUser2), JsonUtils.unknownError);
    }


}
