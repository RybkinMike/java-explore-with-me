package ru.practicum.ewm.stats;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<Stats, Long> {
    long countByUriAndCreatedAfterAndCreatedBefore(StringBuilder uri, LocalDateTime start, LocalDateTime end);

    @Query("select count(distinct s.ip) " +
            "from Stats as s " +
            "where s.uri = ?1 " +
            "and s.created > ?2 and s.created < ?3")
    long countByUriForUniqueIP(StringBuilder uris, LocalDateTime start, LocalDateTime end);


    @Query("select distinct s.uri " +
            "from Stats as s")
    String [] getDistinctUri();

    @Query("select s.uri " +
            "from Stats as s " +
            "where s.uri in (?1) " +
            "and s.created > ?2 and s.created < ?3 " +
            "GROUP BY s.ip")
    List<String> getUrisByUriForUniqueIP(String [] uris, LocalDateTime from, LocalDateTime to);

    @Query("select s.uri " +
            "from Stats as s " +
            "where s.uri in (?1) " +
            "and s.created > ?2 and s.created < ?3")
    List<String> getUrisByUri(String [] uris, LocalDateTime from, LocalDateTime to);
}
