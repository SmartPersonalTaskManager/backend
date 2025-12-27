package com.sptm.backend.repository;

import com.sptm.backend.model.CalendarEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CalendarEventRepository extends JpaRepository<CalendarEvent, Long> {
    List<CalendarEvent> findByUserId(Long userId);

    Optional<CalendarEvent> findByUserIdAndGoogleEventId(Long userId, String googleEventId);
}
