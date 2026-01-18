package com.buyogo.intern.repository;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.buyogo.intern.model.EventEntity;
import com.buyogo.intern.util.TestData;

@DataJpaTest
class EventRepositoryTest {

    @Autowired
    private EventRepository repository;

    @Test
    void startInclusive_endExclusive() {
        Instant start = Instant.parse("2026-01-01T10:00:00Z");
        Instant end = Instant.parse("2026-01-01T11:00:00Z");

        repository.save(TestData.eventAtWithId("E1", start));
        repository.save(TestData.eventAtWithId("E2", end.minusSeconds(1)));
        repository.save(TestData.eventAtWithId("E3", end)); // should be excluded

        List<EventEntity> result =
                repository.findByMachineIdFlexible("M001", start, end);

        assertEquals(2, result.size());
    }
}
