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
    public List<StatsDTO> getStatsFromDB(String start, String end, List<String> uris, boolean unique) {
        System.out.println("Вход");
        LocalDateTime from = LocalDateTime.parse(start, dateTimeFormatter);
        LocalDateTime to = LocalDateTime.parse(end, dateTimeFormatter);
        if (uris == null || uris.size() == 0 || uris.get(0).equals("events/") || uris.get(0).isBlank()) {
            uris = repository.getDistinctUri();
        }
        List<StatsDTO> listDTO = new ArrayList<>();
        List<String> urisToList;
        System.out.println("Ветвление");
        if (unique) {
            System.out.println("Вход в айли");
            urisToList = repository.getUrisByUriForUniqueIP(uris, from, to);
            System.out.println("Выход из айпи");
        } else {
            urisToList = repository.getUrisByUri(uris, from, to);
            }
        for (String uri : uris) {
            StatsDTO statsDTO = new StatsDTO("ewm-main-service", uri, Collections.frequency(urisToList, uri));
            System.out.println(statsDTO);
            listDTO.add(statsDTO);
        }
        System.out.println(urisToList);
        listDTO.sort((dto1, dto2) -> (int) (dto2.getHits() - dto1.getHits()));
        return listDTO;
    }

    @Override
    public Stats saveStats(Stats stats) {
        stats.setCreated(LocalDateTime.now());
        return repository.save(stats);
    }
}
