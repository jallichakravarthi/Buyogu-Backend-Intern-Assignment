package com.buyogo.intern.service;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.buyogo.intern.dto.BatchIngestResponse;
import com.buyogo.intern.dto.EventRequest;
import com.buyogo.intern.model.EventEntity;
import com.buyogo.intern.repository.EventRepository;
import com.buyogo.intern.util.TestData;

@ExtendWith(MockitoExtension.class)
class EventIngestionServiceTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventIngestionService ingestionService;

    @Test
    void identicalDuplicateEvent_isDeduped() {
        // Arrange
        String eventId = "E-1";
        EventRequest request = TestData.validEventRequest(eventId);
        // Create an entity that matches the request perfectly (same hash)
        EventEntity existing = TestData.eventEntity(
                eventId, request.getEventTime(), Instant.now(),
                request.getMachineId(), request.getDurationMs(), request.getDefectCount());

        when(eventRepository.findAllById(anyList())).thenReturn(List.of(existing));

        // Act
        BatchIngestResponse response = ingestionService.ingestBatch(List.of(request));

        // Assert
        assertEquals(1, response.getDeduped());
        verify(eventRepository, never()).save(any());
    }

    @Test
    void differentPayloadWithNewerReceivedTime_updates() {
        // Arrange
        String eventId = "E-1";
        EventRequest request = TestData.validEventRequest(eventId);
        request.setReceivedTime(Instant.parse("2026-01-18T10:00:00Z"));

        // Existing entity has an older received time
        EventEntity existing = TestData.eventEntity(
                eventId, request.getEventTime(), Instant.parse("2026-01-17T10:00:00Z"),
                request.getMachineId(), request.getDurationMs(), 5 // Different defect count to change hash
        );

        when(eventRepository.findAllById(anyList())).thenReturn(List.of(existing));

        // Act
        BatchIngestResponse response = ingestionService.ingestBatch(List.of(request));

        // Assert
        assertEquals(1, response.getUpdated());
        verify(eventRepository).saveAll(List.of(existing));
    }

    @Test
    void differentPayloadWithOlderReceivedTime_isIgnored() {
        // Arrange
        String eventId = "E-1";
        EventRequest request = TestData.eventRequestWithOlderReceivedTime(eventId);

        // Existing entity is "newer" than the incoming request
        EventEntity existing = TestData.eventEntity(
                eventId, request.getEventTime(), Instant.now(),
                request.getMachineId(), request.getDurationMs(), 10);

        when(eventRepository.findAllById(anyList())).thenReturn(List.of(existing));

        // Act
        BatchIngestResponse response = ingestionService.ingestBatch(List.of(request));

        // Assert
        assertEquals(1, response.getDeduped()); // Ignored updates count as deduped in your service logic
        verify(eventRepository, never()).save(any());
    }

    @Test
    void invalidDuration_isRejected() {
        // Act
        BatchIngestResponse response = ingestionService.ingestBatch(
                List.of(TestData.invalidDurationRequest()));

        // Assert
        assertEquals(1, response.getRejected());
    }

    @Test
    void futureEventTime_isRejected() {
        // Act
        BatchIngestResponse response = ingestionService.ingestBatch(
                List.of(TestData.futureEventRequest()));

        // Assert
        assertEquals(1, response.getRejected());
    }

    @Test
    void newEvent_isAccepted() {
        // Arrange
        String eventId = "E-NEW";
        when(eventRepository.findAllById(anyList())).thenReturn(Collections.emptyList());

        // Act
        BatchIngestResponse response = ingestionService.ingestBatch(
                List.of(TestData.validEventRequest(eventId)));

        // Assert
        assertEquals(1, response.getAccepted());
        verify(eventRepository).saveAll(anyList());
    }

    @Test
    void eventTimeMoreThanReceivedTime_isRejected() {
        // Arrange
        String eventId = "E-1";
        EventRequest request = TestData.validEventRequest(eventId);
        request.setEventTime(Instant.parse("2026-01-20T10:00:00Z"));
        request.setReceivedTime(Instant.parse("2026-01-19T10:00:00Z")); // receivedTime before eventTime

        // Act
        BatchIngestResponse response = ingestionService.ingestBatch(List.of(request));

        // Assert
        assertEquals(1, response.getRejected());
    }

    @Test
    void processThousandEvents_underReasonableTime() {

        List<EventRequest> batch = IntStream.range(0, 1000)
                .mapToObj(i -> TestData.validEventRequest("E-" + i))
                .toList();

        long start = System.currentTimeMillis();

        ingestionService.ingestBatch(batch);

        long duration = System.currentTimeMillis() - start;

        System.out.println("Processed 1000 events in " + duration + " ms");

        assertTrue(duration < 2000); // sanity threshold
    }

}