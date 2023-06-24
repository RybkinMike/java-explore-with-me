package ru.practicum.ewm.stats;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.stats.StatsDTO;

import java.util.List;


@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
@Validated
public class StatsController {
    private final StatsService statsService;

    @GetMapping("/stats")
    public List<StatsDTO> getStats(@RequestParam(value = "start") String start,
                                   @RequestParam(value = "end") String end,
                                   @RequestParam(value = "uris", defaultValue = "") String[] uris,
                                   @RequestParam(value = "unique", defaultValue = "false") boolean unique) {
        return statsService.getStatsFromDB(start, end, uris, unique);
    }

    @PostMapping("/hit")
    public Stats saveStats(@RequestBody Stats stats) {
        return statsService.saveStats(stats);
    }


}