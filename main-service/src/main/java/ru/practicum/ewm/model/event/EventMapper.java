package ru.practicum.ewm.model;

import lombok.NoArgsConstructor;
import ru.practicum.ewm.exception.ValidationException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor
public class EventMapper {

    static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Event toEntity(EventRequestDto eventRequestDto, Location location, Category category, User initiator) {
        Event event = new Event();
        event.setAnnotation(eventRequestDto.getAnnotation());
        event.setCategory(category);
        event.setDescription(eventRequestDto.getDescription());
        event.setEventDate(LocalDateTime.parse(eventRequestDto.getEventDate(), dateTimeFormatter));
        event.setLocation(location);
        event.setPaid(eventRequestDto.getPaid());
        event.setParticipantLimit(eventRequestDto.getParticipantLimit());

        event.setCreatedOn(LocalDateTime.now());

        event.setRequestModeration(eventRequestDto.getRequestModeration());
        event.setState("WAITING");
        event.setTitle(eventRequestDto.getTitle());

        event.setInitiator(initiator);
        return event;
    }
}
