package ru.practicum.explorewithme.events.service;

import ru.practicum.explorewithme.events.dto.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    EventFullDto addEvent(Long userId, NewEventDto newEventDto);

    List<EventShortDto> getEvents(Long userId, Integer from, Integer size);

    EventFullDto updateEvent(Long userId, UpdateEventRequest updateEventRequest);

    EventFullDto getEvent(Long userId, Long eventId);

    EventFullDto cancelEvent(Long userId, Long eventId);

    List<EventShortDto> publicGetEvents(String text, List<Long> categories,
                                        Boolean paid, LocalDateTime rangeStart,
                                        LocalDateTime rangeEnd, Boolean onlyAvailable,
                                        String sort, String locationName, Integer from, Integer size,
                                        HttpServletRequest request);

    EventFullDto publicGetEvent(Long id, HttpServletRequest request);

    List<EventFullDto> adminGetEvents(List<Long> users, List<String> states,
                                      List<Long> categories, LocalDateTime rangeStart,
                                      LocalDateTime rangeEnd, String locationName, Integer from, Integer size);

    EventFullDto adminUpdateEvent(Long eventId, AdminUpdateEventDto eventDto);

    EventFullDto adminPublish(Long eventId);

    EventFullDto adminReject(Long eventId);
}
