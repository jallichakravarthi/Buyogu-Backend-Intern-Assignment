package com.buyogo.intern.dto;

public class TopDefectLineResponse {

    private String lineId;
    private long totalDefects;
    private long eventCount;
    private double defectsPercent;

    public TopDefectLineResponse(
            String lineId,
            long totalDefects,
            long eventCount,
            double defectsPercent
    ) {
        this.lineId = lineId;
        this.totalDefects = totalDefects;
        this.eventCount = eventCount;
        this.defectsPercent = defectsPercent;
    }

    public String getLineId() { return lineId; }
    public long getTotalDefects() { return totalDefects; }
    public long getEventCount() { return eventCount; }
    public double getDefectsPercent() { return defectsPercent; }
}
