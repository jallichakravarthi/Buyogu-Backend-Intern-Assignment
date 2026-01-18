package com.buyogo.intern.repository;

import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.buyogo.intern.model.EventEntity;

@Repository
public interface EventRepository extends JpaRepository<EventEntity, String> {

    List<EventEntity> findByEventTimeBetween(Instant from, Instant to);
    
    @Query("SELECT e FROM EventEntity e " +
           "WHERE e.machineId LIKE %:machineId " + // Matches any prefix
           "AND e.eventTime >= :start " +
           "AND e.eventTime < :end")
    List<EventEntity> findByMachineIdFlexible(
            @Param("machineId") String machineId, 
            @Param("start") Instant start, 
            @Param("end") Instant end
    );
    
}

