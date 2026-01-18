package com.buyogo.intern.util;

public class MachineIdParser {

    private MachineIdParser() {}

    public static String extractFactoryId(String machineId) {
        // F01-L02-M005 -> F01
        return machineId.split("-")[0];
    }

    public static String extractLineId(String machineId) {
        // F01-L02-M005 -> F01-L02
        String[] parts = machineId.split("-");
        return parts[0] + "-" + parts[1];
    }
}
