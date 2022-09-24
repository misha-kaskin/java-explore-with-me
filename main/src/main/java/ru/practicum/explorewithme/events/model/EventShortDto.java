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

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
public class EventShortDto {
    private Long id;
    private String annotation;
    private Category category;
    private Long confirmedRequests;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = Patterns.DEFAULT_PATTERN)
    private LocalDateTime eventDate;
    private ShortUserDto initiator;
    private Boolean paid;
    private String title;
    private Long views;
}
