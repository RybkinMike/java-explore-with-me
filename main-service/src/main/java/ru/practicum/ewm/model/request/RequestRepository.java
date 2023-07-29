package ru.practicum.ewm.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findByRequesterAndEvent(Long userId, Long eventId);

    List<Request> findByRequesterId(Long userId);

    Request findByEventId(Long eventId);

    Request findByRequesterIdAndEventId(Long userId, Long eventId);
}
