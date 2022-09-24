package ru.practicum.explorewithme.handlers;

import lombok.RequiredArgsConstructor;
import ru.practicum.explorewithme.categories.model.Category;
import ru.practicum.explorewithme.categories.storage.CategoryRepository;
import ru.practicum.explorewithme.compilations.model.EventCompilation;
import ru.practicum.explorewithme.events.model.*;
import ru.practicum.explorewithme.users.model.ShortUserDto;
import ru.practicum.explorewithme.users.model.User;
import ru.practicum.explorewithme.users.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class Mapper {
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public static Event mapNewEventDtoToEvent(NewEventDto newEventDto, Long userId) {
        return Event.builder()
                .annotation(newEventDto.getAnnotation())
                .category(newEventDto.getCategory())
                .description(newEventDto.getDescription())
                .eventDate(newEventDto.getEventDate())
                .lon(newEventDto.getLocation().getLon())
                .lat(newEventDto.getLocation().getLat())
                .paid(newEventDto.getPaid())
                .participantLimit(newEventDto.getParticipantLimit())
                .requestModeration(newEventDto.getRequestModeration())
                .title(newEventDto.getTitle())
                .state(State.PENDING)
                .initiator(userId)
                .views(0L)
                .createdOn(LocalDateTime.now())
                .confirmedRequests(0L)
                .build();
    }

    public static ShortUserDto mapUserDtoToShort(User userDto) {
        return ShortUserDto.builder()
                .id(userDto.getId())
                .name(userDto.getName())
                .build();
    }

    public static EventFullDto mapEventToFullDto(Event event, Category categoryDto,
                                                 User userDto, List<String> nearestLocations) {
        return EventFullDto.builder()
                .annotation(event.getAnnotation())
                .category(categoryDto)
                .confirmedRequests(event.getConfirmedRequests())
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .id(event.getId())
                .initiator(mapUserDtoToShort(userDto))
                .location(new Location(event.getLat(), event.getLon()))
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .views(event.getViews())
                .nearestLocations(nearestLocations)
                .build();
    }

    public static EventShortDto mapEventToShortDto(Event event, Category categoryDto, User userDto) {
        return EventShortDto.builder()
                .annotation(event.getAnnotation())
                .category(categoryDto)
                .confirmedRequests(event.getConfirmedRequests())
                .eventDate(event.getEventDate())
                .id(event.getId())
                .initiator(mapUserDtoToShort(userDto))
                .paid(event.getPaid())
                .title(event.getTitle())
                .views(event.getViews())
                .build();
    }

    public static EventCompilation mapEventCompilation(Long eventId, Long compilationId) {
        return EventCompilation.builder()
                .eventId(eventId)
                .compilationId(compilationId)
                .build();
    }
}