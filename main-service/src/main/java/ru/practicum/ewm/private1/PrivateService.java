package ru.practicum.ewm.private1;

import ru.practicum.ewm.model.event.EventFullDto;
import ru.practicum.ewm.model.event.EventRequestDto;
import ru.practicum.ewm.model.event.UpdateEventAdminRequest;
import ru.practicum.ewm.model.request.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.model.request.EventRequestStatusUpdateResult;
import ru.practicum.ewm.model.request.ParticipationRequestDto;

import java.util.List;

public interface PrivateService {
    List<EventFullDto> getEvents(Long userId, Integer from, Integer size);

    EventFullDto saveEvent(Long userId, EventRequestDto eventRequestDto);

    EventFullDto getEvent(Long userId, Long eventId);

    EventFullDto patchEvent(UpdateEventAdminRequest updateEventAdminRequest, Long userId, Long eventId);

    List<ParticipationRequestDto> getAllRequest(Long userId);

    EventRequestStatusUpdateResult patchRequest(EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest, Long userId, Long eventId);

    ParticipationRequestDto saveRequest(Long userId, Long eventId);

    ParticipationRequestDto cancelRequest(Long userId, Long requestId);

    List<ParticipationRequestDto> getRequests(Long userId, Long eventId);
}
