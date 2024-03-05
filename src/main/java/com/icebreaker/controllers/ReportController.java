package com.icebreaker.controllers;

import com.icebreaker.services.ReportService;
import org.springframework.web.bind.annotation.*;

@RestController
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/fetchReportsForUser")
    public String fetchReportsForUser(@RequestParam(name = "roomCode") String roomCode,
                                      @RequestParam(name = "userID") String userID) {
        return reportService.fetchReportsForUser(roomCode, userID);
    }


    @GetMapping("/fetchReportOfUser")
    public String fetchReportOfUser(@RequestParam(name = "roomCode") String roomCode,
                                    @RequestParam(name = "userID1") String userID1,
                                    @RequestParam(name = "userID2") String userID2) {
        return reportService.fetchReportOfUser(roomCode, userID1, userID2);
    }
}
