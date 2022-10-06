package ru.practicum.explorewithme.events.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import ru.practicum.explorewithme.categories.model.Category;
import ru.practicum.explorewithme.categories.storage.CategoryRepository;
import ru.practicum.explorewithme.events.model.*;
import ru.practicum.explorewithme.events.storage.EventRepository;
import ru.practicum.explorewithme.exception.NotFoundException;
import ru.practicum.explorewithme.exception.ValidationException;
import ru.practicum.explorewithme.handlers.Mapper;
import ru.practicum.explorewithme.specificlocation.model.SpecificLocation;
import ru.practicum.explorewithme.specificlocation.model.Status;
import ru.practicum.explorewithme.specificlocation.storage.SpecificLocationRepository;
import ru.practicum.explorewithme.users.model.User;
import ru.practicum.explorewithme.users.storage.UserRepository;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final SpecificLocationRepository specificLocationRepository;
    private final Client client;

    @Override
    @Transactional
    public EventFullDto addEvent(Long userId, NewEventDto newEventDto) {
        if (LocalDateTime.now().plusHours(2L).isAfter(newEventDto.getEventDate())) {
            throw new ValidationException("Событие намечено менее чем за 2 часа от текущего времени");
        }

        if (newEventDto.getAnnotation().length() < 20 || newEventDto.getAnnotation().length() > 2000) {
            throw new ValidationException("Недопустимая длина слова");
        }

        if (newEventDto.getDescription().length() < 20 || newEventDto.getDescription().length() > 7000) {
            throw new ValidationException("Недопустимая длина слова");
        }

        if (newEventDto.getTitle().length() < 3 || newEventDto.getTitle().length() > 120) {
            throw new ValidationException("Недопустимая длина слова");
        }

        if (!categoryRepository.existsById(newEventDto.getCategory())) {
            throw new NotFoundException("Не найдена указанная категория");
        }

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Не найден указанный пользователь");
        }

        if (!eventRepository.belongsLocation(newEventDto.getLocation().getLon(),
                newEventDto.getLocation().getLat())) {
            throw new ValidationException("Событие не принадлежит определенной локации");
        }

        Event event = eventRepository.save(Mapper.mapNewEventDtoToEvent(newEventDto, userId));

        return mapEventToFullDto(event);
    }

    @Override
    public List<EventShortDto> getEvents(Long userId, Integer from, Integer size) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Не найден указанный пользователь");
        }

        List<EventShortDto> eventShorts = eventRepository
                .findAllByInitiator(userId, PageRequest.of(from, size))
                .stream()
                .map(event -> mapEventToShortDto(event))
                .collect(Collectors.toList());

        return eventShorts;
    }

    @Override
    @Transactional
    public EventFullDto updateEvent(Long userId, UpdateEventRequest updateEventRequest) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Не найден указанный пользователь");
        }

        if (!eventRepository.existsById(updateEventRequest.getEventId())) {
            throw new NotFoundException("Не найдено указанное событие");
        }

        Event event = eventRepository.getReferenceById(updateEventRequest.getEventId());

        if (!event.getInitiator().equals(userId)) {
            throw new ValidationException("Событие может изменить только инициатор");
        }

        if (event.getState().equals(State.PUBLISHED)) {
            throw new ValidationException("Некорректное состояние");
        }

        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(2L))) {
            throw new ValidationException("Менее чем за 2 часа до начала события");
        }

        if (updateEventRequest.getCategory() != null) {
            if (!categoryRepository.existsById(updateEventRequest.getCategory())) {
                throw new NotFoundException("Не найдена указанная категория");
            }
            event.setCategory(updateEventRequest.getCategory());
        }

        if (updateEventRequest.getAnnotation() != null) {
            if (updateEventRequest.getAnnotation().length() < 20
                    || updateEventRequest.getAnnotation().length() > 2000) {
                throw new ValidationException("Некорректная длина строки");
            }
            event.setAnnotation(updateEventRequest.getAnnotation());
        }

        if (updateEventRequest.getDescription() != null) {
            if (updateEventRequest.getDescription().length() < 20
                    || updateEventRequest.getDescription().length() > 7000) {
                throw new ValidationException("Некорректная длина строки");
            }
            event.setDescription(updateEventRequest.getDescription());
        }

        if (updateEventRequest.getTitle() != null) {
            if (updateEventRequest.getTitle().length() < 3
                    || updateEventRequest.getTitle().length() > 120) {
                throw new ValidationException("Некорректная длина строки");
            }
            event.setTitle(updateEventRequest.getTitle());
        }

        if (updateEventRequest.getEventDate() != null) {
            if (updateEventRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(2L))) {
                throw new ValidationException("Некорректное время");
            }
            event.setEventDate(updateEventRequest.getEventDate());
        }

        if (updateEventRequest.getPaid() != null) {
            event.setPaid(updateEventRequest.getPaid());
        }

        if (updateEventRequest.getParticipantLimit() != null) {
            if (updateEventRequest.getParticipantLimit() < 0) {
                throw new ValidationException("Некорректное число участников");
            }
            event.setParticipantLimit(updateEventRequest.getParticipantLimit());
        }

        return mapEventToFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto getEvent(Long userId, Long eventId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь не найден");
        }

        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException("Событие не найдено");
        }

        Event event = eventRepository.getReferenceById(eventId);

        if (!event.getInitiator().equals(userId)) {
            throw new ValidationException("Некорректный идентификатор пользователя");
        }

        return mapEventToFullDto(event);
    }

    @Override
    @Transactional
    public EventFullDto cancelEvent(Long userId, Long eventId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Не найден пользователь");
        }

        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException("Не найдено событие");
        }

        Event event = eventRepository.getReferenceById(eventId);

        if (!event.getInitiator().equals(userId)) {
            throw new ValidationException("Пользователь не является инициатором события");
        }

        if (!event.getState().equals(State.PENDING)) {
            throw new ValidationException("Отменить можно только событие в состоянии ожидания");
        }

        event.setState(State.CANCELED);

        return mapEventToFullDto(eventRepository.save(event));
    }

    @Override
    public List<EventShortDto> publicGetEvents(String text, List<Long> categories,
                                               Boolean paid, LocalDateTime rangeStart,
                                               LocalDateTime rangeEnd, Boolean onlyAvailable,
                                               String sort, String locationName, Integer from, Integer size,
                                               HttpServletRequest request) {
        try {
            SortValues.valueOf(sort);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Несуществующий способ сортировки");
        }

        BooleanExpression onlyPublishedEvents = QEvent.event.state.eq(State.PUBLISHED);
        BooleanExpression basedExpression = onlyPublishedEvents;

        if (StringUtils.hasText(locationName)) {
            if (!specificLocationRepository.existsByName(locationName)) {
                throw new NotFoundException("Не найдена локация");
            }

            SpecificLocation specificLocation = specificLocationRepository.findSpecificLocationByName(locationName);

            if (!specificLocation.getStatus().equals(Status.APPROVED)) {
                throw new ValidationException("Недопустимая локация");
            }

            List<Long> ids = eventRepository.findAllIdsByLocation(specificLocation.getLon(),
                    specificLocation.getLat(),
                    specificLocation.getRadius());

            basedExpression = basedExpression.and(QEvent.event.id.in(ids));
        }

        if (StringUtils.hasText(text)) {
            basedExpression = basedExpression
                    .and(QEvent.event.annotation.containsIgnoreCase(text)
                            .or(QEvent.event.description.containsIgnoreCase(text)));
        }

        if (categories != null && !categories.isEmpty()) {
            basedExpression = basedExpression
                    .and(QEvent.event.category
                            .in(categories));
        }

        if (paid != null) {
            basedExpression = basedExpression
                    .and(QEvent.event.paid
                            .eq(paid));
        }

        if (rangeStart != null) {
            basedExpression = basedExpression
                    .and(QEvent.event.eventDate
                            .after(rangeStart));
        } else {
            basedExpression = basedExpression
                    .and(QEvent.event.eventDate
                            .after(LocalDateTime.now()));
        }

        if (rangeEnd != null) {
            basedExpression = basedExpression
                    .and(QEvent.event.eventDate
                            .before(rangeEnd));
        }

        if (onlyAvailable) {
            basedExpression = basedExpression
                    .and(QEvent.event.confirmedRequests
                            .lt(QEvent.event.participantLimit));
        }

        String sortColumn;

        if (SortValues.EVENT_DATE.toString().equals(sort)) {
            sortColumn = "eventDate";
        } else {
            sortColumn = "views";
        }

        List<EventShortDto> events = eventRepository
                .findAll(basedExpression, PageRequest.of(from, size, Sort.by(sortColumn).descending()))
                .stream()
                .map(event -> mapEventToShortDto(event))
                .collect(Collectors.toList());

        if (events.isEmpty()) {
            throw new NotFoundException("Не найдено подходящих событий");
        }

        client.post("GET EVENTS", request.getRemoteAddr(), request.getRequestURI());

        return events;
    }

    @Override
    public EventFullDto publicGetEvent(Long id, HttpServletRequest request) {
        if (!eventRepository.existsById(id)) {
            throw new NotFoundException("Не найдено события");
        }

        BooleanExpression onlyPublicEvents = QEvent.event.state.eq(State.PUBLISHED);

        Optional<Event> optionalEvent = eventRepository.findOne(onlyPublicEvents.and(QEvent.event.id.eq(id)));

        if (optionalEvent.isEmpty()) {
            throw new NotFoundException("Ничего не найдено по запросу");
        }

        Event event = optionalEvent.get();
        Long views = event.getViews();
        event.setViews(++views);

        EventFullDto fullDto = mapEventToFullDto(eventRepository.save(event));

        client.post("GET EVENT BY ID", request.getRemoteAddr(), request.getRequestURI());

        return fullDto;
    }

    @Override
    public List<EventFullDto> adminGetEvents(List<Long> users, List<String> states,
                                             List<Long> categories, LocalDateTime rangeStart,
                                             LocalDateTime rangeEnd, String locationName, Integer from, Integer size) {
        BooleanExpression basedExpression = QEvent.event.paid.eq(true).or(QEvent.event.paid.eq(false));

        if (StringUtils.hasText(locationName)) {
            if (!specificLocationRepository.existsByName(locationName)) {
                throw new NotFoundException("Не найдена локация");
            }

            SpecificLocation specificLocation = specificLocationRepository.findSpecificLocationByName(locationName);

            if (!specificLocation.getStatus().equals(Status.APPROVED)) {
                throw new ValidationException("Недопустимая локация");
            }

            List<Long> ids = eventRepository.findAllIdsByLocation(specificLocation.getLon(),
                    specificLocation.getLat(),
                    specificLocation.getRadius());

            basedExpression = basedExpression.and(QEvent.event.id.in(ids));
        }

        if (rangeStart != null) {
            basedExpression = basedExpression.and(QEvent.event.eventDate
                    .after(rangeStart));
        }

        if (states != null && !states.isEmpty()) {
            List<State> stateList;

            try {
                stateList = states
                        .stream()
                        .map(state -> State.valueOf(state))
                        .collect(Collectors.toList());
            } catch (IllegalArgumentException e) {
                throw new ValidationException("Некорректное состояние");
            }

            basedExpression = basedExpression.and(QEvent.event.state.in(stateList));
        }

        if (users != null && !users.isEmpty()) {
            for (Long id : users) {
                if (!userRepository.existsById(id)) {
                    throw new NotFoundException("Пользователь не найден");
                }
            }

            basedExpression = basedExpression.and(QEvent.event.initiator.in(users));
        }

        if (categories != null && !categories.isEmpty()) {
            for (Long id : categories) {
                if (!categoryRepository.existsById(id)) {
                    throw new NotFoundException("Категория не найдена");
                }
            }

            basedExpression = basedExpression.and(QEvent.event.category.in(categories));
        }

        if (rangeEnd != null) {
            basedExpression = basedExpression.and(QEvent.event.eventDate.before(rangeEnd));
        }

        List<EventFullDto> events = eventRepository.findAll(basedExpression, PageRequest.of(from, size))
                .stream()
                .map(
                        event -> mapEventToFullDto(event)
                )
                .collect(Collectors.toList());

        if (events.isEmpty()) {
            throw new NotFoundException("События не найдены");
        }

        return events;
    }

    @Override
    @Transactional
    public EventFullDto adminUpdateEvent(Long eventId, AdminUpdateEventDto eventDto) {
        Event event = eventRepository.getReferenceById(eventId);

        if (StringUtils.hasText(eventDto.getAnnotation())) {
            event.setAnnotation(eventDto.getAnnotation());
        }

        if (eventDto.getCategory() != null) {
            event.setCategory(eventDto.getCategory());
        }

        if (StringUtils.hasText(eventDto.getDescription())) {
            event.setDescription(eventDto.getDescription());
        }

        if (eventDto.getEventDate() != null) {
            event.setEventDate(eventDto.getEventDate());
        }

        if (eventDto.getLocation() != null) {
            if (eventDto.getLocation().getLat() != null) {
                event.setLat(eventDto.getLocation().getLat());
            }

            if (eventDto.getLocation().getLon() != null) {
                event.setLon(eventDto.getLocation().getLon());
            }
        }

        if (eventDto.getPaid() != null) {
            event.setPaid(eventDto.getPaid());
        }

        if (eventDto.getParticipantLimit() != null) {
            event.setParticipantLimit(eventDto.getParticipantLimit());
        }

        if (eventDto.getRequestModeration() != null) {
            event.setRequestModeration(eventDto.getRequestModeration());
        }

        if (eventDto.getTitle() != null) {
            event.setTitle(event.getTitle());
        }

        return mapEventToFullDto(eventRepository.save(event));
    }

    @Override
    @Transactional
    public EventFullDto adminPublish(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException("Событие не найдено");
        }

        Event event = eventRepository.getReferenceById(eventId);

        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(1L))) {
            throw new ValidationException("Начинается менее чем через час от даты публикации");
        }

        if (!event.getState().equals(State.PENDING)) {
            throw new ValidationException("Событие находится не в состоянии ожидания публикации");
        }

        event.setState(State.PUBLISHED);
        event.setPublishedOn(LocalDateTime.now());

        return mapEventToFullDto(eventRepository.save(event));
    }

    @Override
    @Transactional
    public EventFullDto adminReject(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException("Событие не найдено");
        }

        Event event = eventRepository.getReferenceById(eventId);

        if (!event.getState().equals(State.PENDING)) {
            throw new ValidationException("Событие находится не в состоянии ожидания публикации");
        }

        event.setState(State.CANCELED);

        return mapEventToFullDto(eventRepository.save(event));
    }

    private EventFullDto mapEventToFullDto(Event event) {
        Category categoryDto = categoryRepository.findCategoryById(event.getCategory());
        User userDto = userRepository.getReferenceById(event.getInitiator());
        List<String> nearestLocations = specificLocationRepository
                .findNearestLocation(event.getLon(), event.getLat());

        return Mapper.mapEventToFullDto(event, categoryDto, userDto, nearestLocations);
    }

    private EventShortDto mapEventToShortDto(Event event) {
        Category category = categoryRepository.findCategoryById(event.getCategory());
        User user = userRepository.getReferenceById(event.getInitiator());

        return Mapper.mapEventToShortDto(event, category, user);
    }
}
