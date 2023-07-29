package ru.practicum.ewm.model;


import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventRequestDto {

    String annotation;

    Long category;

    String description;

    String eventDate;

    Location location;

    String paid;

    int participantLimit;

    String publishedOn;

    Boolean requestModeration;

    String state;

    String title;

}

