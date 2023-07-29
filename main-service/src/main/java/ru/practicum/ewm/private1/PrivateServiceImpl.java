package ru.practicum.ewm.private1;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.ResponseException;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.model.category.Category;
import ru.practicum.ewm.model.category.CategoryRepository;
import ru.practicum.ewm.model.event.*;
import ru.practicum.ewm.model.location.Location;
import ru.practicum.ewm.model.location.LocationRepository;
import ru.practicum.ewm.model.request.*;
import ru.practicum.ewm.model.user.User;
import ru.practicum.ewm.model.user.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PrivateServiceImpl implements PrivateService {

    CategoryRepository categoryRepository;
    EventRepository eventRepository;
    UserRepository userRepository;

    RequestRepository requestRepository;
    LocationRepository locationRepository;
    EventMapper eventMapper = new EventMapper();
    RequestMapper requestMapper = new RequestMapper();

    static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public List<EventFullDto> getEvents(Long userId, Integer from, Integer size) {
        Sort sortByDate = Sort.by(Sort.Direction.ASC, "id");
        int pageIndex = from / size;
        Pageable page = PageRequest.of(pageIndex, size, sortByDate);
        User initiator = userRepository.findById(userId).orElseThrow();
        return eventMapper.toListEventFullDtoFromListEvent(eventRepository.findByInitiator(initiator, page).toList());
    }

    @Override
    public EventFullDto saveEvent(Long userId, EventRequestDto eventRequestDto) {
        LocalDateTime eventDate = LocalDateTime.parse(eventRequestDto.getEventDate(), dateTimeFormatter);
        if (eventDate.plusHours(2).isBefore(LocalDateTime.now())) {
            throw new ResponseException("Event date is not correct");
        }
        if (eventRequestDto.getPaid() == null) {
            eventRequestDto.setPaid(false);
        }
        if (eventRequestDto.getRequestModeration() == null) {
            eventRequestDto.setRequestModeration(true);
        }
        Location location = locationRepository.save(eventRequestDto.getLocation());
        Category category = categoryRepository.findById(eventRequestDto.getCategory()).orElseThrow(() -> new NotFoundException("Category not found"));
        User initiator = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        Event event = eventMapper.toEntityFromRequest(eventRequestDto, location, category, initiator);
        EventFullDto eventFullDto = eventMapper.toFullDtoFromEntity(eventRepository.save(event), event.getConfirmedRequests());
        if (eventFullDto.getState().equals("WAITING")) {
            eventFullDto.setState("PENDING");
        }
        return eventFullDto;
    }

    @Override
    public EventFullDto getEvent(Long userId, Long eventId) {
        User initiator = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        Event event = eventRepository.findByIdAndInitiator(eventId, initiator);
        return eventMapper.toFullDtoFromEntity(event, event.getConfirmedRequests());
    }

    @Override
    public EventFullDto patchEvent(UpdateEventAdminRequest updateEventAdminRequest, Long userId, Long eventId) {
        User initiator = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event not found"));
        if (event.getState().equals("PUBLISHED")) {
            throw new ValidationException(String.format("Event with ID =%d is PUBLISHED", userId));
        }
        if (!initiator.equals(userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found")))) {
            throw new ValidationException(String.format("User with ID =%d is not initiator", userId));
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
        if (updateEventAdminRequest.getStateAction() != null) {
            if (updateEventAdminRequest.getStateAction().equals("SEND_TO_REVIEW")) {
                event.setState("PENDING");
                event.setPublishedOn(LocalDateTime.now());
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

        EventFullDto eventFullDto = eventMapper.toFullDtoFromEntity(eventRepository.save(event), event.getConfirmedRequests());
        if (eventFullDto.getState().equals("WAITING")) {
            eventFullDto.setState("PENDING");
        }
        return eventFullDto;
    }

    @Override
    public List<ParticipationRequestDto> getAllRequest(Long userId) {
        User requester = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));

        return requestMapper.toListParticipationRequestDtoFromListRequest(requestRepository.findByRequester(requester));
    }

    @Override
    public EventRequestStatusUpdateResult patchRequest(EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest, Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException(String.format("Событие с ID =%d не найден", eventId)));
        if (event.getConfirmedRequests() >= event.getParticipantLimit() && event.getParticipantLimit() != 0) {
            throw new ValidationException("Confirmed requests is over");
        }
        List<Long> requestIds = eventRequestStatusUpdateRequest.getRequestIds();
        String status = eventRequestStatusUpdateRequest.getStatus();
        requestRepository.confirmRequestStatus(status, requestIds);
        EventRequestStatusUpdateResult eventRequestStatusUpdateResult = new EventRequestStatusUpdateResult();
        List<Request> confirmedRequest = requestRepository.findAllByStatusAndEventId("CONFIRMED", eventId);
        List<Request> rejectedRequest = requestRepository.findAllByStatusAndEventId("REJECTED", eventId);
        List<ParticipationRequestDto> confirmedParticipationRequest = new ArrayList<>();
        List<ParticipationRequestDto> rejectedParticipationRequest = new ArrayList<>();
        for (Request request:confirmedRequest
             ) {
            confirmedParticipationRequest.add(requestMapper.toParticipationRequestDtoFromRequest(request));
        }
        for (Request request:rejectedRequest
        ) {
            rejectedParticipationRequest.add(requestMapper.toParticipationRequestDtoFromRequest(request));
        }
        eventRequestStatusUpdateResult.setConfirmedRequests(confirmedParticipationRequest);
        eventRequestStatusUpdateResult.setRejectedRequests(rejectedParticipationRequest);
        eventRepository.updateConfirmedRequest(confirmedParticipationRequest.size(), eventId);
        return eventRequestStatusUpdateResult;
    }

    @Override
    public List<ParticipationRequestDto> getRequests(Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException(String.format("Событие с ID =%d не найден", eventId)));
        if (event.getInitiator().getId() != userId) {
            throw new ValidationException("User is not initiator");
        }
        return requestMapper.toListParticipationRequestDtoFromListRequest(requestRepository.findByEventId(eventId));
    }

    @Override
    public ParticipationRequestDto saveRequest(Long userId, Long eventId) {
        Request request = new Request();
        request.setCreated(LocalDateTime.now());
        request.setRequester(userRepository.findById(userId).orElseThrow(() -> new NotFoundException(String.format("Пользователь с ID =%d не найден", userId))));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException(String.format("Событие с ID =%d не найден", eventId)));
        if (event.getInitiator().getId() == userId) {
            throw new ValidationException("Requester is initiator");
        }
        if (!event.getState().equals("PUBLISHED")) {
            throw new ValidationException("Event is not PUBLISHED");
        }
        List<Request> requestList = requestRepository.findByRequesterId(userId);
        if (!requestList.isEmpty()) {
            for (Request requestFromRepository:requestList
                 ) {
                if (requestFromRepository.getEvent().equals(event)) {
                    throw new ValidationException("Request is exist");
                }
            }
        }
        if (event.getConfirmedRequests() >= event.getParticipantLimit() && event.getParticipantLimit() != 0) {
            throw new ValidationException("Confirmed requests is over");
        }
        request.setEvent(event);

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            request.setStatus("CONFIRMED");
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.save(event);
        } else {
            request.setStatus("PENDING");
        }
        return requestMapper.toParticipationRequestDtoFromRequest(requestRepository.save(request));
    }

    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {

        Request request = requestRepository.findById(requestId).orElseThrow(() -> new NotFoundException("Request not found"));
        if (request.getRequester().getId() != userId) {
            throw new ValidationException("User is not requester");
        }
        request.setStatus("CANCELED");
        return requestMapper.toParticipationRequestDtoFromRequest(requestRepository.save(request));
    }
}
