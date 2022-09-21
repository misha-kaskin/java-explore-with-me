package ru.practicum.explorewithme.compilations.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.categories.storage.CategoryRepository;
import ru.practicum.explorewithme.compilations.dto.CompilationDto;
import ru.practicum.explorewithme.compilations.dto.EventCompilation;
import ru.practicum.explorewithme.compilations.dto.NewCompilationDto;
import ru.practicum.explorewithme.compilations.storage.CompilationEventRepository;
import ru.practicum.explorewithme.compilations.storage.CompilationRepository;
import ru.practicum.explorewithme.events.dto.EventShortDto;
import ru.practicum.explorewithme.events.dto.Mapper;
import ru.practicum.explorewithme.events.storage.EventRepository;
import ru.practicum.explorewithme.exception.NotFoundException;
import ru.practicum.explorewithme.exception.ValidationException;
import ru.practicum.explorewithme.users.storage.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final CompilationEventRepository compilationEventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public NewCompilationDto addCompilation(NewCompilationDto compilationDto) {
        List<Long> events = compilationDto.getEvents();

        for (Long event : events) {
            if (!eventRepository.existsById(event)) {
                throw new NotFoundException("Не найдено событие в подборке");
            }
        }

        CompilationDto compilation = compilationRepository.save(CompilationDto.builder()
                .pinned(compilationDto.getPinned())
                .title(compilationDto.getTitle())
                .build());

        List<EventCompilation> eventCompilations = events
                .stream()
                .map(
                        event -> EventCompilation
                                .builder()
                                .eventId(event)
                                .compilationId(compilation.getId())
                                .build()
                )
                .collect(Collectors.toList());

        compilationEventRepository.saveAll(eventCompilations);
        compilation.setId(compilation.getId());

        return compilationDto;
    }

    @Override
    public void deleteCompilation(Long compId) {
        if (!compilationRepository.existsById(compId)) {
            throw new NotFoundException("Не найдена подборка событий");
        }

        List<Long> ids = compilationEventRepository.findAllByCompilationId(compId);

        compilationEventRepository.deleteAllById(ids);
        compilationRepository.deleteById(compId);
    }

    @Override
    public void deleteEventFromCompilation(Long compId, Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException("Не найдено событие");
        }

        if (!compilationRepository.existsById(compId)) {
            throw new NotFoundException("Не найдена подборка");
        }

        if (!compilationEventRepository.existsByEventCompilationId(eventId, compId)) {
            throw new NotFoundException("Не найдено событие в подборке");
        }

        Long id = compilationEventRepository.selectByEventIdAndCompilationId(eventId, compId);

        compilationEventRepository.deleteById(id);
    }

    @Override
    public void addEventToCompilation(Long compId, Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException("Не найдено событие");
        }

        if (!compilationRepository.existsById(compId)) {
            throw new NotFoundException("Не найдена подборка");
        }

        if (compilationEventRepository.existsByEventCompilationId(eventId, compId)) {
            throw new NotFoundException("Событие уже добавлено в подборку");
        }

        compilationEventRepository.save(EventCompilation
                .builder()
                .eventId(eventId)
                .compilationId(compId)
                .build());
    }

    @Override
    public void pinFalseCompilation(Long compId) {
        if (!compilationRepository.existsById(compId)) {
            throw new NotFoundException("Не найдена подборка событий");
        }

        CompilationDto compilationDto = compilationRepository.getReferenceById(compId);

        if (!compilationDto.getPinned()) {
            throw new ValidationException("Подборка не закреплена на главной странице");
        }

        compilationDto.setPinned(false);
        compilationRepository.save(compilationDto);
    }

    @Override
    public void pinTrueCompilation(Long compId) {
        if (!compilationRepository.existsById(compId)) {
            throw new NotFoundException("Не найдена подборка событий");
        }

        CompilationDto compilationDto = compilationRepository.getReferenceById(compId);

        if (compilationDto.getPinned()) {
            throw new ValidationException("Подборка уже закреплена на главной странице");
        }

        compilationDto.setPinned(true);
        compilationRepository.save(compilationDto);
    }

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        Page<CompilationDto> compilationDtoPage;

        if (pinned != null) {
            compilationDtoPage = compilationRepository
                    .findAllPinnedCompilations(pinned, PageRequest.of(from, size));
        } else {
            compilationDtoPage = compilationRepository.findAll(PageRequest.of(from, size));
        }

        List<NewCompilationDto> newCompilationDtoList = compilationDtoPage.stream()
                .map(
                        compilationDto -> NewCompilationDto.builder()
                                .id(compilationDto.getId())
                                .events(compilationEventRepository.findAllByCompilationId(compilationDto.getId()))
                                .pinned(compilationDto.getPinned())
                                .title(compilationDto.getTitle())
                                .build()
                )
                .collect(Collectors.toList());

        List<CompilationDto> compilationDtoList = newCompilationDtoList
                .stream()
                .map(newCompilationDto -> CompilationDto.builder()
                        .id(newCompilationDto.getId())
                        .events(eventRepository.findAllById(newCompilationDto.getEvents())
                                .stream()
                                .map(
                                        event -> Mapper.mapEventToShortDto(event,
                                                categoryRepository.findCategoryDtoById(event.getCategory()),
                                                userRepository.getReferenceById(event.getInitiator()))
                                )
                                .collect(Collectors.toList()))
                        .pinned(newCompilationDto.getPinned())
                        .title(newCompilationDto.getTitle())
                        .build()
                )
                .collect(Collectors.toList());

        if (compilationDtoList.isEmpty()) {
            throw new NotFoundException("Подборки событий не найдены");
        }

        return compilationDtoList;
    }

    @Override
    public CompilationDto getCompilation(Long compId) {
        if (!compilationRepository.existsById(compId)) {
            throw new NotFoundException("Не найдена подборка");
        }

        CompilationDto compilationDto = compilationRepository.getReferenceById(compId);
        List<Long> eventsIds = compilationEventRepository.findAllByCompilationId(compId);

        List<EventShortDto> eventShortDtoList = eventRepository.findAllById(eventsIds)
                .stream()
                .map(event -> Mapper.mapEventToShortDto(event,
                        categoryRepository.findCategoryDtoById(event.getCategory()),
                        userRepository.getReferenceById(event.getInitiator())))
                .collect(Collectors.toList());

        return CompilationDto.builder()
                .id(compilationDto.getId())
                .pinned(compilationDto.getPinned())
                .title(compilationDto.getTitle())
                .events(eventShortDtoList)
                .build();
    }
}
