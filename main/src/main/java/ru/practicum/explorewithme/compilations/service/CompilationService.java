package ru.practicum.explorewithme.compilations.service;

import ru.practicum.explorewithme.compilations.dto.Compilation;
import ru.practicum.explorewithme.compilations.dto.NewCompilationDto;

import java.util.List;

public interface CompilationService {
    Compilation addCompilation(NewCompilationDto compilationDto);

    void deleteCompilation(Long compId);

    void deleteEventFromCompilation(Long compId, Long eventId);

    void addEventToCompilation(Long compId, Long eventId);

    void pinFalseCompilation(Long compId);

    void pinTrueCompilation(Long compId);

    List<Compilation> getCompilations(Boolean pinned, Integer from, Integer size);

    Compilation getCompilation(Long compId);
}
