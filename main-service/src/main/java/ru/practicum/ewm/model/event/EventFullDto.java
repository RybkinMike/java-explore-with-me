package ru.practicum.ewm.model.event;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.model.category.Category;
import ru.practicum.ewm.model.location.Location;
import ru.practicum.ewm.model.user.User;

@Getter
@Setter
@ToString
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventFullDto {
    long id;

    String annotation;

    Category category;

    String createdOn;

    String description;

    String eventDate;

    User initiator;

    Location location;

    Boolean paid;

    int participantLimit;

    String publishedOn;

    Boolean requestModeration;

    String state;

    String title;

    Long views;

    int confirmedRequests;
}
