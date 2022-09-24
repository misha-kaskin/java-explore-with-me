package ru.practicum.explorewithme.events.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.*;
import ru.practicum.explorewithme.categories.model.Category;
import ru.practicum.explorewithme.handlers.Patterns;
import ru.practicum.explorewithme.users.model.ShortUserDto;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
public class EventFullDto {
    private String annotation;
    private Long id;
    private Long confirmedRequests;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = Patterns.DEFAULT_PATTERN)
    private LocalDateTime createdOn;
    private String description;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = Patterns.DEFAULT_PATTERN)
    private LocalDateTime eventDate;
    private Category category;
    private ShortUserDto initiator;
    private Location location;
    private Boolean paid;
    private Long participantLimit;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = Patterns.DEFAULT_PATTERN)
    private LocalDateTime publishedOn;
    private Boolean requestModeration;
    private State state;
    private String title;
    private Long views;
    private List<String> nearestLocations;
}
