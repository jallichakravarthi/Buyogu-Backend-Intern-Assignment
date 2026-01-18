package com.buyogo.intern.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.buyogo.intern.dto.BatchIngestResponse;
import com.buyogo.intern.dto.EventRequest;
import com.buyogo.intern.service.EventIngestionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/events")
public class EventIngestionController {

    private final EventIngestionService ingestionService;

    public EventIngestionController(EventIngestionService ingestionService) {
        this.ingestionService = ingestionService;
    }

    @PostMapping("/batch")
    public ResponseEntity<BatchIngestResponse> ingestBatch(
            @RequestBody @Valid List<EventRequest> events
    ) {
        BatchIngestResponse response = ingestionService.ingestBatch(events);
        return ResponseEntity.ok(response);
    }
}
