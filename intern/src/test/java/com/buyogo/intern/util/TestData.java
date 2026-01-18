package com.buyogo.intern.util;

import java.time.Instant;

import com.buyogo.intern.dto.EventRequest;
import com.buyogo.intern.model.EventEntity;

public final class TestData {

    private TestData() {
    }

    /*
     * -----------------------------
     * EventRequest helpers
     * -----------------------------
     */

    public static EventRequest validEventRequest(String eventId) {
        EventRequest req = new EventRequest();
        req.setEventId(eventId);
        req.setEventTime(Instant.parse("2026-01-15T10:00:00Z"));
        req.setMachineId("F01-L01-M001");
        req.setDurationMs(1000);
        req.setDefectCount(2);
        return req;
    }

    public static EventRequest eventRequestWithOlderReceivedTime(String eventId) {
        EventRequest req = validEventRequest(eventId);
        req.setReceivedTime(Instant.now().minusSeconds(3600));
        return req;
    }

    public static EventRequest invalidDurationRequest() {
        EventRequest req = validEventRequest("E-INVALID");
        req.setDurationMs(-10);
        return req;
    }

    public static EventRequest futureEventRequest() {
        EventRequest req = validEventRequest("E-FUTURE");
        req.setEventTime(Instant.now().plusSeconds(3600)); // 1 hour in future
        return req;
    }

    /*
     * -----------------------------
     * EventEntity helpers
     * -----------------------------
     */

    public static EventEntity eventEntity(
            String eventId,
            Instant eventTime,
            Instant receivedTime,
            String machineId,
            long durationMs,
            int defectCount) {
        String payloadHash = PayloadHashUtil.hash(
                eventId,
                eventTime.toString(),
                machineId,
                durationMs,
                defectCount);

        return new EventEntity(
                eventId,
                eventTime,
                receivedTime,
                machineId,
                durationMs,
                defectCount,
                payloadHash);
    }

    public static EventEntity eventWithDefect(int defectCount) {
        return new EventEntity(
                "E-DEF",
                Instant.parse("2026-01-15T10:00:00Z"),
                Instant.now(),
                "F01-L01-M001",
                1000,
                defectCount,
                "hash");
    }

    public static EventEntity eventWithDefectAndLine(int defectCount, String lineId) {
        return new EventEntity(
                "E-DEF",
                Instant.parse("2026-01-15T10:00:00Z"),
                Instant.now(),
                lineId+"-M001",
                1000,
                defectCount,
                "hash");
    }

    public static EventEntity eventAt(Instant time) {
        return new EventEntity(
                "E-TIME",
                time,
                Instant.now(),
                "F01-L01-M001",
                1000,
                1,
                "hash");
    }

    public static EventEntity eventAtWithId(String eventId, Instant time) {
        return new EventEntity(
                eventId,
                time,
                Instant.now(),
                "F01-L01-M001",
                1000,
                1,
                "hash");
    }
}
