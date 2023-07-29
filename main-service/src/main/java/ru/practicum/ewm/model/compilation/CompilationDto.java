package ru.practicum.ewm.model.compilation;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.model.event.Event;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompilationDto {
    long id;

    List<Event> events;

    Boolean pinned;

    String title;
}
