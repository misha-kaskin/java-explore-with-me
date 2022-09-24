package ru.practicum.explorewithme.compilations.model;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewCompilationDto {
    @NotNull
    private List<Long> events;
    private Long id;
    @NotNull
    private Boolean pinned;
    @NotBlank
    private String title;
}
