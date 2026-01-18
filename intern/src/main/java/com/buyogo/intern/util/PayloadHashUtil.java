package com.buyogo.intern.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class PayloadHashUtil {

    private PayloadHashUtil() {}

    public static String hash(
            String eventId,
            String eventTime,
            String machineId,
            long durationMs,
            int defectCount
    ) {
        String canonical = String.join("|",
                eventId,
                eventTime,
                machineId,
                String.valueOf(durationMs),
                String.valueOf(defectCount)
        );

        return sha256(canonical);
    }

    private static String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encoded = digest.digest(input.getBytes(StandardCharsets.UTF_8));

            StringBuilder hex = new StringBuilder();
            for (byte b : encoded) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
