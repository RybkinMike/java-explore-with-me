package ru.practicum.ewm.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "events", schema = "public")
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @Column
    String annotation;

    @ManyToOne
    Category category;

    @Column
    LocalDateTime createdOn;

    @Column
    String description;

    @Column
    LocalDateTime eventDate;

    @ManyToOne
    User initiator;

    @ManyToOne
    Location location;

    @Column
    String paid;

    @Column
    int participantLimit;

    @Column
    LocalDateTime publishedOn;

    @Column
    Boolean requestModeration;

    @Column
    String state;

    @Column
    String title;

    @Column
    Long views;
}
