package com.buyogo.intern.dto;

import java.time.Instant;

public class MachineStatsResponse {

    private String machineId;
    private Instant start;
    private Instant end;

    private long eventsCount;
    private long defectsCount;
    private double avgDefectRate;
    private String status;

    public MachineStatsResponse(
            String machineId,
            Instant start,
            Instant end,
            long eventsCount,
            long defectsCount,
            double avgDefectRate,
            String status
    ) {
        this.machineId = machineId;
        this.start = start;
        this.end = end;
        this.eventsCount = eventsCount;
        this.defectsCount = defectsCount;
        this.avgDefectRate = avgDefectRate;
        this.status = status;
    }

    public String getMachineId() { return machineId; }
    public Instant getStart() { return start; }
    public Instant getEnd() { return end; }
    public long getEventsCount() { return eventsCount; }
    public long getDefectsCount() { return defectsCount; }
    public double getAvgDefectRate() { return avgDefectRate; }
    public String getStatus() { return status; }
}
