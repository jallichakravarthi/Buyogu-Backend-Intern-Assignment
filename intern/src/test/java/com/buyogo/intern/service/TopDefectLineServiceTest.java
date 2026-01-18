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

import com.buyogo.intern.repository.EventRepository;
import com.buyogo.intern.util.TestData;

@ExtendWith(MockitoExtension.class)
class TopDefectLineServiceTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private TopDefectLineService service;

    @Test
    void topDefectLines_sortedAndLimited() {
        when(eventRepository.findByEventTimeBetween(any(), any()))
                .thenReturn(List.of(
                        TestData.eventWithDefectAndLine(5, "F01-L01"),
                        TestData.eventWithDefectAndLine(1, "F01-L02"),
                        TestData.eventWithDefectAndLine(10, "F01-L03")));

        var result = service.getTopDefectLines(
                "F01",
                Instant.now().minusSeconds(3600),
                Instant.now(),
                2);

        assertEquals("F01-L03", result.get(0).getLineId());
        assertEquals(10, result.get(0).getTotalDefects());
        assertEquals(2, result.size());
    }
}
