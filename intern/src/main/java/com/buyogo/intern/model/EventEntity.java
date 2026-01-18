package com.buyogo.intern.model;

import java.time.Instant;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity
@Table(
    name = "events",
    indexes = {
        @Index(name = "idx_machine_time", columnList = "machineId,eventTime")
    }
)
public class EventEntity {

    @Id
    @Column(length = 64, nullable = false)
    private String eventId;

    @Column(nullable = false)
    private Instant eventTime;

    @Column(nullable = false)
    private Instant receivedTime;

    @Column(nullable = false, length = 64)
    private String machineId;

    @Column(nullable = false)
    private long durationMs;

    @Column(nullable = false)
    private int defectCount;

    @Column(nullable = false, length = 64)
    private String payloadHash;

    protected EventEntity() {
        // JPA requires no-arg constructor
    }

    public EventEntity(
            String eventId,
            Instant eventTime,
            Instant receivedTime,
            String machineId,
            long durationMs,
            int defectCount,
            String payloadHash
    ) {
        this.eventId = eventId;
        this.eventTime = eventTime;
        this.receivedTime = receivedTime;
        this.machineId = machineId;
        this.durationMs = durationMs;
        this.defectCount = defectCount;
        this.payloadHash = payloadHash;
    }

    // Getters only (controlled mutation via service)

    public String getEventId() {
        return eventId;
    }

    public Instant getEventTime() {
        return eventTime;
    }

    public Instant getReceivedTime() {
        return receivedTime;
    }

    public String getMachineId() {
        return machineId;
    }

    public long getDurationMs() {
        return durationMs;
    }

    public int getDefectCount() {
        return defectCount;
    }

    public String getPayloadHash() {
        return payloadHash;
    }

    // Controlled update method
    public void updateFrom(EventEntity newer) {
        this.eventTime = newer.eventTime;
        this.receivedTime = newer.receivedTime;
        this.machineId = newer.machineId;
        this.durationMs = newer.durationMs;
        this.defectCount = newer.defectCount;
        this.payloadHash = newer.payloadHash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EventEntity)) return false;
        EventEntity that = (EventEntity) o;
        return eventId.equals(that.eventId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId);
    }
}

