package ru.practicum.ewm.public1;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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
    public List<Compilation> getCompilations(@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                       @Positive @RequestParam(name = "size", defaultValue = "10") Integer size),
                                        @RequestParam(value = "pinned", defaultValue = "false") boolean unique) {
        log.info("Get all compilations from={}, size={}", from, size);
        return publicService.Compilations(from, size);
    }

    @GetMapping("/compilations/{compId}")
    public ResponseEntity<Object> getCompilation(@PathVariable Long compId) {
        log.info("Get compilation {}", compId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping("/categories")
    public List<Category> getCompilations(@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                             @Positive @RequestParam(name = "size", defaultValue = "10") Integer size),
    @RequestParam(value = "pinned", defaultValue = "false") boolean unique) {
        log.info("Get all compilations from={}, size={}", from, size);
        return publicService.Compilations(from, size);
    }

    @GetMapping("/compilations/{compId}")
    public ResponseEntity<Object> getCompilation(@PathVariable Long compId) {
        log.info("Get compilation {}", compId);
        return bookingClient.getBooking(userId, bookingId);
    }

}
