package com.buyogo.intern.controller;

import java.time.Instant;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.buyogo.intern.dto.MachineStatsResponse;
import com.buyogo.intern.dto.TopDefectLineResponse;
import com.buyogo.intern.service.StatsService;
import com.buyogo.intern.service.TopDefectLineService;

@RestController
@RequestMapping("/stats")
public class StatsController {

    private final StatsService statsService;
    private final TopDefectLineService topDefectLineService;

    public StatsController(
            StatsService statsService,
            TopDefectLineService topDefectLineService) {
        this.statsService = statsService;
        this.topDefectLineService = topDefectLineService;
    }

    @GetMapping
    public MachineStatsResponse getStats(
            @RequestParam String machineId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant end) {
        return statsService.getMachineStats(machineId, start, end);
    }

    @GetMapping("/top-defect-lines")
    public List<TopDefectLineResponse> getTopDefectLines(
            @RequestParam String factoryId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @RequestParam(defaultValue = "10") int limit) {
        return topDefectLineService.getTopDefectLines(factoryId, from, to, limit);
    }

}
