package ru.practicum.explorewithme.specificlocation.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SpecificLocationUpdate {
    private String name;
    private Float lat;
    private Float lon;
    private Float radius;
}
