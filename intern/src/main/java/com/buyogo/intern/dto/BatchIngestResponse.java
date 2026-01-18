package com.buyogo.intern.dto;

import java.util.ArrayList;
import java.util.List;

public class BatchIngestResponse {

    private int accepted;
    private int deduped;
    private int updated;
    private int rejected;

    private List<Rejection> rejections = new ArrayList<>();

    public static class Rejection {
        private String eventId;
        private String reason;

        public Rejection(String eventId, String reason) {
            this.eventId = eventId;
            this.reason = reason;
        }

        public String getEventId() {
            return eventId;
        }

        public String getReason() {
            return reason;
        }
    }

    public void incrementAccepted() { accepted++; }
    public void incrementDeduped() { deduped++; }
    public void incrementUpdated() { updated++; }
    public void incrementRejected() { rejected++; }

    public void addRejection(String eventId, String reason) {
        rejections.add(new Rejection(eventId, reason));
    }

    public int getAccepted() { return accepted; }
    public int getDeduped() { return deduped; }
    public int getUpdated() { return updated; }
    public int getRejected() { return rejected; }
    public List<Rejection> getRejections() { return rejections; }
}
