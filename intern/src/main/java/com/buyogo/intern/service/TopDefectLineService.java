package com.buyogo.intern.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.buyogo.intern.dto.TopDefectLineResponse;
import com.buyogo.intern.model.EventEntity;
import com.buyogo.intern.repository.EventRepository;
import com.buyogo.intern.util.MachineIdParser;

@Service
public class TopDefectLineService {

    private final EventRepository eventRepository;

    public TopDefectLineService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<TopDefectLineResponse> getTopDefectLines(
            String factoryId,
            Instant from,
            Instant to,
            int limit) {

        List<EventEntity> events = eventRepository.findByEventTimeBetween(from, to);

        Map<String, List<EventEntity>> eventsByLine = new HashMap<>();

        for (EventEntity event : events) {
            if (MachineIdParser.extractFactoryId(event.getMachineId()).equals(factoryId)) {
                String lineId = MachineIdParser.extractLineId(event.getMachineId());
                eventsByLine.putIfAbsent(lineId, new ArrayList<>());
                eventsByLine.get(lineId).add(event);
            }
        }

        System.out.println(eventsByLine);

        List<TopDefectLineResponse> results = new ArrayList<>();

        for (Map.Entry<String, List<EventEntity>> entry : eventsByLine.entrySet()) {

            String lineId = entry.getKey();
            List<EventEntity> lineEvents = entry.getValue();

            long eventCount = lineEvents.size();

            long totalDefects = 0L;

            for (EventEntity event : lineEvents) {
                long defectCount = event.getDefectCount();
                if (defectCount >= 0)
                    totalDefects += defectCount;
            }

            double defectsPercent = eventCount == 0 ? 0.0 : Math.round((totalDefects * 10000.0) / eventCount) / 100.0;

            results.add(new TopDefectLineResponse(
                    lineId,
                    totalDefects,
                    eventCount,
                    defectsPercent));
        }

        if (limit <= 0 || results.isEmpty()) {
            return Collections.emptyList();
        }

        Collections.sort(results, (e1, e2) -> Long.compare(e1.getTotalDefects(), e2.getTotalDefects()) * -1);
        return results.subList(0, Math.min(results.size(), limit));
    }
}
