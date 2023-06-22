package ru.practicum.ewm.stats;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.stats.StatsDTO;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class StatsServiceImpl implements StatsService {

    StatsRepository repository;

    final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public List<StatsDTO> getStatsFromDB(String start, String end, String[] uris, boolean unique) {
        LocalDateTime from = LocalDateTime.parse(start, dateTimeFormatter);
        LocalDateTime to = LocalDateTime.parse(end, dateTimeFormatter);
        if (uris == null || uris.length == 0 || uris[0].equals("events/") || uris[0].isBlank()) {
            uris = repository.getDistinctUri();
        }
        List<StatsDTO> listDTO = new ArrayList<>();
        if (unique) {
            for (int i = 0; i < uris.length; i++) {
                listDTO.add(new StatsDTO("ewm-main-service", uris[i], repository.countByUriForUniqueIP(uris[i], from, to)));
            }
        } else {
            for (int i = 0; i < uris.length; i++) {
                listDTO.add(new StatsDTO("ewm-main-service", uris[i], repository.countByUriAndCreatedAfterAndCreatedBefore(uris[i], from, to)));
            }
        }
        listDTO.sort((dto1, dto2) -> (int) (dto2.getHits() - dto1.getHits()));
        return listDTO;
    }

    @Override
    public Stats saveStats(Stats stats) {
        stats.setCreated(LocalDateTime.now());
        return repository.save(stats);
    }
}
