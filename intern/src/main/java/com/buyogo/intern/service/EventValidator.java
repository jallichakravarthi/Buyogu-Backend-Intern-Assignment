package com.buyogo.intern.service;

import java.time.Duration;
import java.time.Instant;

import com.buyogo.intern.dto.EventRequest;

public class EventValidator {

    private static final long MAX_DURATION_MS = 6 * 60 * 60 * 1000; // 6 hours
    private static final Duration MAX_FUTURE_OFFSET = Duration.ofMinutes(15);

    public static String validate(EventRequest req) {

        if (req.getDurationMs() < 0 || req.getDurationMs() > MAX_DURATION_MS) {
            return "INVALID_DURATION";
        }

        Instant now = Instant.now();
        if (req.getEventTime().isAfter(now.plus(MAX_FUTURE_OFFSET))) {
            return "EVENT_TIME_IN_FUTURE";
        }

        if (req.getReceivedTime() != null && req.getEventTime().isAfter(req.getReceivedTime())) {
            return "EVENT_TIME_AFTER_RECEIVED_TIME";
        }

        return null; // valid
    }
}
