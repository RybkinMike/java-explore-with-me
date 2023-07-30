package ru.practicum.ewm.admin;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.ResponseException;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.model.category.Category;
import ru.practicum.ewm.model.category.CategoryDto;
import ru.practicum.ewm.model.category.CategoryMapper;
import ru.practicum.ewm.model.category.CategoryRepository;
import ru.practicum.ewm.model.compilation.*;
import ru.practicum.ewm.model.event.*;
import ru.practicum.ewm.model.location.LocationRepository;
import ru.practicum.ewm.model.user.User;
import ru.practicum.ewm.model.user.UserDto;
import ru.practicum.ewm.model.user.UserMapper;
import ru.practicum.ewm.model.user.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AdminServiceImpl implements AdminService {


    CategoryRepository categoryRepository;
    EventRepository eventRepository;
    UserRepository userRepository;
    CompilationRepository compilationRepository;
    LocationRepository locationRepository;

    CompilationMapper compilationMapper = new CompilationMapper();

    EventMapper eventMapper = new EventMapper();

    CategoryMapper categoryMapper = new CategoryMapper();

    UserMapper userMapper = new UserMapper();

    static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public CategoryDto saveCategory(CategoryDto categoryDto) {
        if (categoryRepository.findByName(categoryDto.getName()).isPresent()) {
            throw new ValidationException("Category exist");
        }
        return categoryMapper.toDtoFromEntity(categoryRepository.save(categoryMapper.toEntityFromDto(categoryDto)));
    }

    @Override
    public void deleteCategory(Long catId) {
        Category category = categoryRepository.findById(catId).orElseThrow(() -> new NotFoundException("Category is not found"));
        if (eventRepository.findByCategory(category).isPresent()) {
            throw new ValidationException("Category is related with Event");
        }
        categoryRepository.deleteById(catId);
    }

    @Override
    public CategoryDto patchCategory(CategoryDto categoryDto, Long catId) {
        categoryDto.setId(catId);
        Category categoryFromRepository = categoryRepository.findById(catId).orElseThrow(() -> new NotFoundException("Cat is not found"));
        if (categoryDto.getName().equals(categoryFromRepository.getName())) {
            return categoryDto;
        }
        if (categoryRepository.findByName(categoryDto.getName()).isPresent()) {
            throw new ValidationException("Category exist");
        }
        return categoryMapper.toDtoFromEntity(categoryRepository.save(categoryMapper.toEntityFromDto(categoryDto)));
    }

    @Override
    public List<EventFullDto> getEvents(List<Long> users, List<String> states, List<Long> categories, String rangeStart, String rangeEnd, Integer from, Integer size) {
        Sort sortByDate = Sort.by(Sort.Direction.ASC, "id");
        int pageIndex = from / size;
        Pageable page = PageRequest.of(pageIndex, size, sortByDate);
        LocalDateTime start;
        if (users.isEmpty()) {
            users = userRepository.findAllId();
        }
        if (categories.isEmpty()) {

            categories = categoryRepository.findAllId();

        }
        if (states.isEmpty()) {

            states = List.of("PUBLISHED", "WAITING", "REJECTED", "CANCELED");

        }
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
        List<User> usersList = userRepository.findByIds(users);
        List<Category> categoriesList = categoryRepository.findByIds(categories);
        return eventMapper.toListEventFullDtoFromListEvent(eventRepository.findByInitiatorAndStateAndCategoryAndEventDateBeforeEnd(usersList, states, categoriesList, start, end, page).toList());
    }

    @Override
    public EventFullDto patchEvent(UpdateEventAdminRequest updateEventAdminRequest, Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event not found"));
        if (!event.getState().equals("WAITING")) {
            throw new ValidationException("Event state is not WAITING");
        }
        if (updateEventAdminRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventAdminRequest.getAnnotation());
        }
        if (updateEventAdminRequest.getCategory() != null) {
            Category category = categoryRepository.findById(updateEventAdminRequest.getCategory()).orElseThrow(() -> new ValidationException("Category not found"));
            event.setCategory(category);
        }
        if (updateEventAdminRequest.getDescription() != null) {
            event.setDescription(updateEventAdminRequest.getDescription());
        }
        if (updateEventAdminRequest.getEventDate() != null) {
            LocalDateTime eventDate = LocalDateTime.parse(updateEventAdminRequest.getEventDate(), dateTimeFormatter);
            if (eventDate.plusHours(2).isBefore(LocalDateTime.now())) {
                throw new ResponseException("Event date is not correct");
            }
            event.setEventDate(eventDate);
        }
        if (updateEventAdminRequest.getLocation() != null) {
            event.setLocation(locationRepository.save(updateEventAdminRequest.getLocation()));
        }
        if (updateEventAdminRequest.getParticipantLimit() != 0) {
            event.setParticipantLimit(updateEventAdminRequest.getParticipantLimit());
        }
        if (updateEventAdminRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventAdminRequest.getRequestModeration());
        }
        if (updateEventAdminRequest.getPaid() != null) {
            event.setPaid(updateEventAdminRequest.getPaid());
        }
        if (updateEventAdminRequest.getStateAction() != null) {
            if (updateEventAdminRequest.getStateAction().equals("PUBLISH_EVENT")) {
                event.setState("PUBLISHED");
                event.setPublishedOn(LocalDateTime.now());
            }
            if (updateEventAdminRequest.getStateAction().equals("REJECT_EVENT")) {
                event.setState("REJECTED");
                event.setPublishedOn(null);
            }
            if (updateEventAdminRequest.getStateAction().equals("CANCEL_REVIEW")) {
                event.setState("CANCELED");
                event.setPublishedOn(null);
            }
        }
        if (updateEventAdminRequest.getTitle() != null) {
            event.setTitle(updateEventAdminRequest.getTitle());
        }
        event.setId(eventId);

        return eventMapper.toFullDtoFromEntity(eventRepository.save(event), event.getConfirmedRequests());
    }

    @Override
    public List<UserDto> getUsers(List<Long> users, Integer from, Integer size) {
        Sort sortByDate = Sort.by(Sort.Direction.ASC, "id");
        int pageIndex = from / size;
        Pageable page = PageRequest.of(pageIndex, size, sortByDate);
        if (users.isEmpty()) {
            users = userRepository.findAllId();
        }
        return userMapper.toListDtoFromListEntity(userRepository.findById(users, page).toList());
    }

    @Override
    public UserDto saveUser(UserDto userDto) {
        if (userRepository.findByName(userDto.getName()).isPresent()) {
            throw new ValidationException("user exist");
        }
        return userMapper.toDtoFromEntity(userRepository.save(userMapper.toEntityFromDto(userDto)));
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public CompilationDto saveCompilation(NewCompilationDto newCompilationDto) {
        List<Event> events = eventRepository.findByIds(newCompilationDto.getEvents());
        Compilation compilation = compilationMapper.toEntityFromNewComp(newCompilationDto, events);
        return compilationMapper.toDtoFromEntity(compilationRepository.save(compilation));
    }

    @Override
    public void deleteCompilation(Long compId) {
        compilationRepository.deleteById(compId);
    }

    @Override
    public CompilationDto patchCompilation(UpdateCompilationRequest updateCompilationRequest, Long compId) {
        List<Event> events = eventRepository.findByIds(updateCompilationRequest.getEvents());
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() -> new NotFoundException("Compilation not found"));
        if (events != null) {
            compilation.setEvents(events);
        }
        if (updateCompilationRequest.getPinned() != null) {
            compilation.setPinned(updateCompilationRequest.getPinned());
        }
        if (updateCompilationRequest.getTitle() != null) {
            compilation.setTitle(updateCompilationRequest.getTitle());
        }
        return compilationMapper.toDtoFromEntity(compilationRepository.save(compilation));
    }
}
