package ru.practicum.ewm.stats;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<Stats, Long> {
    long countByUriAndCreatedAfterAndCreatedBefore(String uri, LocalDateTime start, LocalDateTime end);

    long countByCreatedAfterAndCreatedBefore(LocalDateTime start, LocalDateTime end);

    @Query("select count(distinct s.ip) " +
            "from Stats as s " +
            "where s.uri = ?1 " +
            "and s.created > ?2 and s.created < ?3")
    long countByUriForUniqueIP(String uri, LocalDateTime start, LocalDateTime end);

    @Query("select count(distinct s.ip) " +
            "from Stats as s " +
            "where s.created > ?1 and s.created < ?2")
    long countAllForUniqueIP(LocalDateTime start, LocalDateTime end);

    List<Stats> getByUriAndCreatedAfterAndCreatedBefore(String uri, LocalDateTime start, LocalDateTime end);

    List<Stats> getByCreatedAfterAndCreatedBefore(LocalDateTime start, LocalDateTime end);

    @Query("select distinct s " +
            "from Stats as s " +
            "where s.uri = ?1 " +
            "and s.created > ?2 and s.created < ?3")
    List<Stats> getByUriForUniqueIP(String uri, LocalDateTime start, LocalDateTime end);

    @Query("select distinct s " +
            "from Stats as s " +
            "where s.created > ?1 and s.created < ?2")
    List<Stats> getAllForUniqueIP(LocalDateTime start, LocalDateTime end);

    @Query("select distinct s.uri " +
            "from Stats as s")
    String[] getDistinctUri();
}
