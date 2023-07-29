package ru.practicum.ewm.model.compilation;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompDto {
    long id;


    List<Long> events;


    Boolean pinned;


    String title;
}
