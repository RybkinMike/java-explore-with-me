package ru.practicum.ewm.public1;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.StatsClient;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.ResponseException;
import ru.practicum.ewm.model.category.CategoryDto;
import ru.practicum.ewm.model.category.CategoryMapper;
import ru.practicum.ewm.model.category.CategoryRepository;
import ru.practicum.ewm.model.compilation.CompilationDto;
import ru.practicum.ewm.model.compilation.CompilationMapper;
import ru.practicum.ewm.model.compilation.CompilationRepository;
import ru.practicum.ewm.model.event.Event;
import ru.practicum.ewm.model.event.EventFullDto;
import ru.practicum.ewm.model.event.EventMapper;
import ru.practicum.ewm.model.event.EventRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PublicServiceImpl implements PublicService {

    CategoryRepository categoryRepository;
    EventRepository eventRepository;

    CompilationRepository compilationRepository;

    EventMapper eventMapper = new EventMapper();

    StatsClient client;

    CategoryMapper categoryMapper = new CategoryMapper();

    CompilationMapper compilationMapper = new CompilationMapper();

    static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public List<CompilationDto> getCompilations(Integer from, Integer size, boolean pinned) {
        Sort sortByDate = Sort.by(Sort.Direction.ASC, "id");
        int pageIndex = from / size;
        Pageable page = PageRequest.of(pageIndex, size, sortByDate);
        return compilationMapper.toListDtoFromListEntity(compilationRepository.findByPinned(pinned, page).toList());
    }

    @Override
    public CompilationDto getCompilation(Long compId) {
        return compilationMapper.toDtoFromEntity(compilationRepository.findById(compId).orElseThrow(() -> new NotFoundException("Compilation not found")));
    }

    @Override
    public List<CategoryDto> getCategories(Integer from, Integer size) {
        Sort sortByDate = Sort.by(Sort.Direction.ASC, "id");
        int pageIndex = from / size;
        Pageable page = PageRequest.of(pageIndex, size, sortByDate);
        return categoryMapper.toListDtoFromListEntity(categoryRepository.findAll(page).toList());
    }

    @Override
    public CategoryDto getCategory(Long catId) {
        return categoryMapper.toDtoFromEntity(categoryRepository.findById(catId).orElseThrow(() -> new NotFoundException("Category is not found")));
    }

    @Override
    public List<EventFullDto> getEvents(String text, List<Long> categories, String paid, String rangeStart, String rangeEnd, String onlyAvailable, String sort, Integer from, Integer size, String uri, String ip) {
        Sort sortByDate = Sort.by(Sort.Direction.ASC, "id");
        int pageIndex = from / size;
        Pageable page = PageRequest.of(pageIndex, size, sortByDate);
        LocalDateTime start;
        if (rangeStart != null) {
            start = LocalDateTime.parse(rangeStart, dateTimeFormatter);
        } else {
            start = LocalDateTime.now();
        }
        LocalDateTime end;
        if (rangeEnd != null) {
            end = LocalDateTime.parse(rangeEnd, dateTimeFormatter);
        } else {
            end = LocalDateTime.of(3000, 1, 1, 1, 1);
        }
        if (end.isBefore(LocalDateTime.now()) || end.isBefore(start)) {
            throw new ResponseException("Incorrectly made request");
        }
        List<Boolean> listPaid = new ArrayList<>();
        if (paid == null) {
            listPaid.add(false);
            listPaid.add(true);
        } else {
            if (paid.equals("true")) {
                listPaid.add(true);
            }
            if (paid.equals("false")) {
                listPaid.add(false);
            }
        }
        if (categories.isEmpty()) {
            List<Event> events = eventRepository.getEventsNoCategory(text, listPaid, start, end, page).toList();
            client.postHit(uri, ip);
            return eventMapper.toListEventFullDtoFromListEvent(eventRepository.getEventsNoCategory(text, listPaid, start, end, page).toList());
        }
            client.postHit(uri, ip);
        return eventMapper.toListEventFullDtoFromListEvent(eventRepository.findByQuery(text, categories, listPaid, start, end, page).toList());
    }

    @Override
    public EventFullDto getEvent(Long id, String uri, String ip) {
        Event event = eventRepository.findById(id).orElseThrow(() -> new NotFoundException("Event not found"));
        if (event.getState().equals("WAITING")) {
            throw new NotFoundException("Event not found");
        }
        client.postHit(uri, ip);
        event.setViews(client.getViews(uri));
        eventRepository.save(event);
        return eventMapper.toFullDtoFromEntity(event, event.getConfirmedRequests());
    }
}
