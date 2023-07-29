package ru.practicum.ewm.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("select e " +
            "from Event as e " +
            "where e.initiator in ?1 " +
            "and e.state in ?2 " +
            "and e.category in ?3 " +
            "and e.eventDate > ?4 and e.eventDate < ?5 " +

            "order by e.eventDate desc")
    Page<Event> findByInitiatorAndStateAndCategoryAndEventDateBeforeEnd(List<Long> users, List<String> states, List<Integer> categories, LocalDateTime start, LocalDateTime end, Pageable page);

    @Query("select e " +
            "from Event as e " +
            "where e.annotation like %?1% " +
            "and e.category.id in (?2) " +
            "and e.paid = ?3 " +
            "and e.eventDate > ?4 and e.eventDate < ?5")
    Page<Event> findByQuery(String text, List<Long> categories, String paid, LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable page);

//    @Query("select e " +
//            "from Event as e " +
//            "where e.annotation like %?1% " +
//            "and e.category in ?2 " +
//            "and e.paid = ?3 " +
//            "and e.eventDate > ?4 and e.eventDate < ?5 " +
//
//            "order by e.eventDate desc")
//    Page<Event> findByQuery(String text, List<Long> categories, boolean paid, String rangeStart, String rangeEnd, boolean onlyAvailable, String sort, Pageable page);

    Event findByIdAndInitiator(Long eventId, User initiator);

    Page<Event> findByInitiator(User initiator, Pageable page);

    @Query("select e " +
            "from Event as e " +
            "where e.annotation like %?1% " +
            "and e.paid = ?2 " +
            "and e.eventDate > ?3 and e.eventDate < ?4")
    Page<Event> getEventsNoCategory(String text, String paid, LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable page);
}
