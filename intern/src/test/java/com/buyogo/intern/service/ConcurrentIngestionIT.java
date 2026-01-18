package com.buyogo.intern.service;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.buyogo.intern.dto.BatchIngestResponse;
import com.buyogo.intern.dto.EventRequest;
import com.buyogo.intern.repository.EventRepository;
import com.buyogo.intern.util.TestData;

@SpringBootTest
class ConcurrentIngestionIT {

    @Autowired
    private EventIngestionService ingestionService;

    @Autowired
    private EventRepository eventRepository;

    @AfterEach
    void tearDown() {
        eventRepository.deleteAll();
    }

    @Test
    void concurrentIngestion_onlyOneAccepted() throws Exception {

        EventRequest req = TestData.validEventRequest("E-1");

        ExecutorService executor = Executors.newFixedThreadPool(5);

        List<Callable<BatchIngestResponse>> tasks =
                IntStream.range(0, 10)
                         .mapToObj(i -> (Callable<BatchIngestResponse>)
                                 () -> ingestionService.ingestBatch(List.of(req)))
                         .toList();

        executor.invokeAll(tasks);
        executor.shutdown();

        assertEquals(1, eventRepository.count());
    }
}
