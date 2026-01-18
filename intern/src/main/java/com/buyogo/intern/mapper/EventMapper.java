package com.buyogo.intern.mapper;

import java.time.Instant;

import com.buyogo.intern.dto.EventRequest;
import com.buyogo.intern.model.EventEntity;
import com.buyogo.intern.util.PayloadHashUtil;

public class EventMapper {

    public static EventEntity toEntity(EventRequest req) {

        Instant receivedTime = req.getReceivedTime() != null
                    ? req.getReceivedTime()
                    : Instant.now();

        String payloadHash = PayloadHashUtil.hash(
                req.getEventId(),
                req.getEventTime().toString(),
                req.getMachineId(),
                req.getDurationMs(),
                req.getDefectCount()
        );

        return new EventEntity(
                req.getEventId(),
                req.getEventTime(),
                receivedTime,
                req.getMachineId(),
                req.getDurationMs(),
                req.getDefectCount(),
                payloadHash
        );
    }
}
