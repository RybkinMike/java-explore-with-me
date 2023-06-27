package ru.practicum.ewm.stats;

import ru.practicum.ewm.dto.stats.StatsDTO;

import java.util.List;

public interface StatsService {

    List<StatsDTO> getStatsFromDB(String start, String end, List<String> uris, boolean unique);

    Stats saveStats(Stats stats);
}
