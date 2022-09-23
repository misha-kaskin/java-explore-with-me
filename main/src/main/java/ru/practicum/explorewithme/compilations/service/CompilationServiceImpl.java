package ru.practicum.explorewithme.compilations.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.categories.dto.Category;
import ru.practicum.explorewithme.categories.storage.CategoryRepository;
import ru.practicum.explorewithme.compilations.dto.Compilation;
import ru.practicum.explorewithme.compilations.dto.EventCompilation;
import ru.practicum.explorewithme.compilations.dto.NewCompilationDto;
import ru.practicum.explorewithme.compilations.storage.CompilationEventRepository;
import ru.practicum.explorewithme.compilations.storage.CompilationRepository;
import ru.practicum.explorewithme.events.dto.Event;
import ru.practicum.explorewithme.events.dto.EventShortDto;
import ru.practicum.explorewithme.events.storage.EventRepository;
import ru.practicum.explorewithme.exception.NotFoundException;
import ru.practicum.explorewithme.exception.ValidationException;
import ru.practicum.explorewithme.handlers.Mapper;
import ru.practicum.explorewithme.users.dto.User;
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
    @Transactional
    public Compilation addCompilation(NewCompilationDto compilationDto) {
        List<Long> events = compilationDto.getEvents();

        for (Long event : events) {
            if (!eventRepository.existsById(event)) {
                throw new NotFoundException("Не найдено событие в подборке");
            }
        }

        Compilation compilation = compilationRepository.save(Compilation.builder()
                .pinned(compilationDto.getPinned())
                .title(compilationDto.getTitle())
                .build());

        List<EventCompilation> eventCompilations = events.stream()
                .map(event -> Mapper.mapEventCompilation(event, compilation.getId()))
                .collect(Collectors.toList());

        compilationEventRepository.saveAll(eventCompilations);

        List<EventShortDto> eventShortDtoList = eventRepository.findAllByIds(events)
                .stream()
                .map(event -> mapEventToShortDto(event))
                .collect(Collectors.toList());

        compilation.setEvents(eventShortDtoList);

        return Compilation.builder()
                .id(compilation.getId())
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .events(eventShortDtoList)
                .build();
    }

    @Override
    @Transactional
    public void deleteCompilation(Long compId) {
        if (!compilationRepository.existsById(compId)) {
            throw new NotFoundException("Не найдена подборка событий");
        }

        List<Long> ids = compilationEventRepository.findAllByCompilationId(compId);

        compilationEventRepository.deleteAllById(ids);
        compilationRepository.deleteById(compId);
    }

    @Override
    @Transactional
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

        Long id = compilationEventRepository.findEventIdByEventIdAndCompilationId(eventId, compId);

        compilationEventRepository.deleteById(id);
    }

    @Override
    @Transactional
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

        compilationEventRepository.save(Mapper.mapEventCompilation(eventId, compId));
    }

    @Override
    @Transactional
    public void pinFalseCompilation(Long compId) {
        if (!compilationRepository.existsById(compId)) {
            throw new NotFoundException("Не найдена подборка событий");
        }

        Compilation compilationDto = compilationRepository.getReferenceById(compId);

        if (!compilationDto.getPinned()) {
            throw new ValidationException("Подборка не закреплена на главной странице");
        }

        compilationDto.setPinned(false);
        compilationRepository.save(compilationDto);
    }

    @Override
    @Transactional
    public void pinTrueCompilation(Long compId) {
        if (!compilationRepository.existsById(compId)) {
            throw new NotFoundException("Не найдена подборка событий");
        }

        Compilation compilationDto = compilationRepository.getReferenceById(compId);

        if (compilationDto.getPinned()) {
            throw new ValidationException("Подборка уже закреплена на главной странице");
        }

        compilationDto.setPinned(true);
        compilationRepository.save(compilationDto);
    }

    @Override
    public List<Compilation> getCompilations(Boolean pinned, Integer from, Integer size) {
        Page<Compilation> compilationDtoPage;

        if (pinned != null) {
            compilationDtoPage = compilationRepository
                    .findAllByPinned(pinned, PageRequest.of(from, size));
        } else {
            compilationDtoPage = compilationRepository.findAll(PageRequest.of(from, size));
        }

        List<Compilation> compilationDtoList = compilationDtoPage.stream()
                .peek(compilation -> fillCompilation(compilation))
                .collect(Collectors.toList());

        if (compilationDtoList.isEmpty()) {
            throw new NotFoundException("Подборки событий не найдены");
        }

        return compilationDtoList;
    }

    @Override
    public Compilation getCompilation(Long compId) {
        if (!compilationRepository.existsById(compId)) {
            throw new NotFoundException("Не найдена подборка");
        }

        Compilation compilationDto = compilationRepository.findCompilationById(compId);
        fillCompilation(compilationDto);

        return compilationDto;
    }

    private EventShortDto mapEventToShortDto(Event event) {
        Category category = categoryRepository.findCategoryById(event.getCategory());
        User user = userRepository.getReferenceById(event.getInitiator());

        return Mapper.mapEventToShortDto(event, category, user);
    }

    private void fillCompilation(Compilation compilation) {
        List<Long> eventsIds = compilationEventRepository.findAllByCompilationId(compilation.getId());
        List<EventShortDto> eventShortDtoList = eventRepository.findAllByIds(eventsIds)
                .stream()
                .map(event -> mapEventToShortDto(event))
                .collect(Collectors.toList());

        compilation.setEvents(eventShortDtoList);
    }
}
