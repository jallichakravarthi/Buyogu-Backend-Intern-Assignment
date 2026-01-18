package com.buyogo.intern.service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

import com.buyogo.intern.dto.MachineStatsResponse;
import com.buyogo.intern.model.EventEntity;
import com.buyogo.intern.repository.EventRepository;

@Service
public class StatsService {

    private final EventRepository eventRepository;

    public StatsService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public MachineStatsResponse getMachineStats(
            String machineId,
            Instant start,
            Instant end
    ) {

        List<EventEntity> events =
                eventRepository.findByMachineIdFlexible(
                        machineId, start, end
                );

        long eventsCount = events.size();

        long defectsCount = 0L;
        for(EventEntity event : events) {
            long defectCount = event.getDefectCount();
            if(defectCount >= 0)
            defectsCount += defectCount;
        }

        double windowHours =
                Duration.between(start, end).toSeconds() / 3600.0;

        double avgDefectRate =
                windowHours == 0 ? 0.0 : defectsCount / windowHours;

        String status = avgDefectRate < 2.0 ? "Healthy" : "Warning";

        return new MachineStatsResponse(
                machineId,
                start,
                end,
                eventsCount,
                defectsCount,
                avgDefectRate,
                status
        );
    }
}
