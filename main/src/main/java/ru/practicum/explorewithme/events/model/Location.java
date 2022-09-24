package ru.practicum.explorewithme.events.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Setter
@Getter
@AllArgsConstructor
public class Location {
    // географические координаты события
    // широта
    @NotNull
    private Float lat;
    // долгота
    @NotNull
    private Float lon;
}
