package ru.practicum.ewm.stats;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<Stats, Long> {

    @Query("select distinct s.uri " +
            "from Stats as s")
    List<String> getDistinctUri();

    @Query("select s.uri " +
            "from Stats as s " +
            "where s.uri in (?1) " +
            "and s.created > ?2 and s.created < ?3 " +
            "group by s.ip")
    List<String> getUrisByUriForUniqueIP(List<String> uris, LocalDateTime from, LocalDateTime to);

    @Query("select s.uri " +
            "from Stats as s " +
            "where s.uri in (?1) " +
            "and s.created > ?2 and s.created < ?3")
    List<String> getUrisByUri(List<String> uris, LocalDateTime from, LocalDateTime to);
}
