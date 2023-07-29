package ru.practicum.ewm.public1;

import ru.practicum.ewm.model.category.CategoryDto;
import ru.practicum.ewm.model.compilation.CompilationDto;
import ru.practicum.ewm.model.event.EventFullDto;

import java.util.List;

public interface PublicService {
    List<CompilationDto> getCompilations(Integer from, Integer size, boolean pinned);

    CompilationDto getCompilation(Long compId);

    List<CategoryDto> getCategories(Integer from, Integer size);

    CategoryDto getCategory(Long catId);

    List<EventFullDto> getEvents(String text, List<Long> categories, String paid, String rangeStart, String rangeEnd, String onlyAvailable, String sort, Integer from, Integer size);

    EventFullDto getEvent(Long id, String uri, String ip);
}
