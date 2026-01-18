package com.buyogo.intern.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.buyogo.intern.dto.BatchIngestResponse;
import com.buyogo.intern.dto.EventRequest;
import com.buyogo.intern.mapper.EventMapper;
import com.buyogo.intern.model.EventEntity;
import com.buyogo.intern.repository.EventRepository;

@Service
public class EventIngestionService {

    private final EventRepository eventRepository;

    private static final Logger log = LoggerFactory.getLogger(EventIngestionService.class);

    public EventIngestionService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Transactional
    public BatchIngestResponse ingestBatch(List<EventRequest> requests) {

        log.debug("Starting batch ingestion. batchSize={}", requests.size());

        BatchIngestResponse response = new BatchIngestResponse();

        // Batch save optimization
        List<EventEntity> entitiesToSave = new ArrayList<>();

        //  Snapshot of what existed BEFORE this batch
        List<String> requestIds = requests.stream().map(EventRequest::getEventId).toList();
        List<EventEntity> existingEntities = eventRepository.findAllById(requestIds);

        HashMap<String, EventEntity> existingIdsBeforeBatch = new HashMap<>();
        for (EventEntity entity : existingEntities) {
            existingIdsBeforeBatch.put(entity.getEventId(), entity);
        }

        //  Track first occurrence in this batch
        Map<String, EventEntity> seenInBatch = new HashMap<>();

        for (EventRequest req : requests) {

            String validationError = EventValidator.validate(req);
            if (validationError != null) {
                response.incrementRejected();
                response.addRejection(req.getEventId(), validationError);
                log.debug(
                        "Rejected eventId={} due to {}",
                        req.getEventId(),
                        validationError);

                continue;
            }

            EventEntity incoming = EventMapper.toEntity(req);
            String eventId = incoming.getEventId();

            log.debug(
                    "Processing eventId={}, eventTime={}, machineId={}",
                    incoming.getEventId(),
                    incoming.getEventTime(),
                    incoming.getMachineId());

            // 🔹 Case A: truly new (did not exist before batch)
            if (!existingIdsBeforeBatch.containsKey(eventId) && !seenInBatch.containsKey(eventId)) {
                entitiesToSave.add(incoming);
                response.incrementAccepted();
                seenInBatch.put(eventId, incoming);
                log.debug(
                        "Accepted new eventId={} (did not exist before batch)",
                        eventId);

                continue;
            }

            EventEntity existing = null;
            if (existingIdsBeforeBatch.containsKey(eventId)) {
                existing = existingIdsBeforeBatch.get(eventId);
            }
            if (seenInBatch.containsKey(eventId)) {
                existing = seenInBatch.get(eventId);
            }

            // 🔹 Case B: compare payload
            if ((existingIdsBeforeBatch.containsKey(eventId) || seenInBatch.containsKey(eventId)) &&
                    existing.getPayloadHash().equals(incoming.getPayloadHash())) {
                response.incrementDeduped();
                seenInBatch.put(eventId, incoming);
                log.debug(
                        "Deduped eventId={} (identical payload)",
                        eventId);

                continue;
            }

            // 🔹 Case C: update vs ignore
            if (existing != null &&
                    incoming.getReceivedTime().isAfter(existing.getReceivedTime())) {
                existing.updateFrom(incoming);
                entitiesToSave.add(existing);
                response.incrementUpdated();
                log.debug(
                        "Updated eventId={} (newer receivedTime: {} > {})",
                        eventId,
                        incoming.getReceivedTime(),
                        existing.getReceivedTime());

            } else {
                log.debug(
                        "Ignored eventId={} update (older receivedTime: {} <= {})",
                        eventId,
                        incoming.getReceivedTime(),
                        existing.getReceivedTime());

                response.incrementDeduped();
            }

            seenInBatch.put(eventId, incoming);
        }

        log.debug(
                "Batch ingestion completed. accepted={}, updated={}, deduped={}, rejected={}",
                response.getAccepted(),
                response.getUpdated(),
                response.getDeduped(),
                response.getRejected());

        if (!entitiesToSave.isEmpty()) {
            eventRepository.saveAll(entitiesToSave);
        }

        return response;
    }

}