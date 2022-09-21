package ru.practicum.explorewithme.events.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Setter
@Getter
@AllArgsConstructor
public class Location {
    @NotNull
    private Float lat;
    @NotNull
    private Float lon;
}
