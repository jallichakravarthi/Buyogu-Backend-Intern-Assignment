package com.buyogo.intern.service;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.buyogo.intern.dto.MachineStatsResponse;
import com.buyogo.intern.repository.EventRepository;
import com.buyogo.intern.util.TestData;

@ExtendWith(MockitoExtension.class)
class StatsServiceTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private StatsService statsService;

    @Test
    void defectCountMinusOne_isIgnored() {
        when(eventRepository
                .findByMachineIdFlexible(
                        any(), any(), any()))
                .thenReturn(List.of(
                        TestData.eventWithDefect(-1),
                        TestData.eventWithDefect(3)));

        MachineStatsResponse stats =
                statsService.getMachineStats(
                        "M1",
                        Instant.now().minusSeconds(3600),
                        Instant.now());

        assertEquals(3, stats.getDefectsCount());
    }
}
