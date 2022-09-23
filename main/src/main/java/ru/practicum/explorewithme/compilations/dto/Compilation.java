package ru.practicum.explorewithme.compilations.dto;

import lombok.*;
import ru.practicum.explorewithme.events.dto.EventShortDto;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Table(name = "compilations")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private Boolean pinned;
    @NotBlank
    private String title;
    @Transient
    private List<EventShortDto> events;
}
