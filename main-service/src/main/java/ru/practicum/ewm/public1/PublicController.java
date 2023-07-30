package ru.practicum.ewm.public1;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.model.category.CategoryDto;
import ru.practicum.ewm.model.compilation.CompilationDto;
import ru.practicum.ewm.model.event.EventFullDto;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
@Validated
public class PublicController {

    private final PublicService publicService;

    @GetMapping("/compilations")
    public List<CompilationDto> getCompilations(@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                @Positive @RequestParam(name = "size", defaultValue = "10") Integer size,
                                                @RequestParam(value = "pinned", defaultValue = "false") boolean pinned) {
        log.info("Get all compilations from={}, size={}, pinned={}", from, size, pinned);
        return publicService.getCompilations(from, size, pinned);
    }

    @GetMapping("/compilations/{compId}")
    public CompilationDto getCompilation(@PathVariable Long compId) {
        log.info("Get compilation {}", compId);
        return publicService.getCompilation(compId);
    }

    @GetMapping("/categories")
    public List<CategoryDto> getCategories(@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                           @Positive @RequestParam(name = "size", defaultValue = "10") Integer size,
                                           @RequestParam(value = "pinned", defaultValue = "false") boolean unique) {
        log.info("Get all categories from={}, size={}", from, size);
        return publicService.getCategories(from, size);
    }

    @GetMapping("/categories/{catId}")
    public CategoryDto getCategory(@PathVariable Long catId) {
        log.info("Get category {}", catId);
        return publicService.getCategory(catId);
    }

    @GetMapping("/events")
    public List<EventFullDto> getEvents(@RequestParam(name = "text", defaultValue = " ") String text,
                                        @RequestParam(value = "categories", defaultValue = "") List<Long> categories,
                                        @RequestParam(value = "paid", required = false) String paid,
                                        @RequestParam(value = "rangeStart", required = false) String rangeStart,
                                        @RequestParam(value = "rangeEnd", required = false) String rangeEnd,
                                        @RequestParam(value = "onlyAvailable", required = false) String onlyAvailable,
                                        @RequestParam(value = "sort",  defaultValue = "EVENT_DATE") String sort,
                                        @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                        @Positive @RequestParam(name = "size", defaultValue = "10") Integer size,
                                        HttpServletRequest request) {
        log.info("Get all Events from={}, size={}, text={}, categories={}, paid={}, rangeStart={}, rangeEnd={}, onlyAvailable={}, sort={}", from, size, text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort);
        String ip = request.getRemoteAddr();
        log.info("client ip: {}", ip);
        String uri = request.getRequestURI();
        log.info("endpoint path: {}", uri);
        return publicService.getEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, uri, ip);
    }

    @GetMapping("/events/{id}")
    public EventFullDto getEvent(@PathVariable Long id, HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        log.info("client ip: {}", ip);
        String uri = request.getRequestURI();
        log.info("endpoint path: {}", uri);
        log.info("Get event {}", id);
        return publicService.getEvent(id, uri, ip);
    }
}
