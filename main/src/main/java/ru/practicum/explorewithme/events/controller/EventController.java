package ru.practicum.explorewithme.events.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.events.model.*;
import ru.practicum.explorewithme.events.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @PostMapping("/users/{userId}/events")
    public EventFullDto addEvent(@PathVariable Long userId,
                                 @RequestBody @Valid NewEventDto newEventDto) {
        return eventService.addEvent(userId, newEventDto);
    }

    @GetMapping("/users/{userId}/events")
    public List<EventShortDto> getEvents(@PathVariable Long userId,
                                         @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                         @RequestParam(defaultValue = "10") @Positive Integer size) {
        return eventService.getEvents(userId, from, size);
    }

    @PatchMapping("/users/{userId}/events")
    public EventFullDto updateEvent(@PathVariable Long userId,
                                    @RequestBody @Valid UpdateEventRequest updateEventRequest) {
        return eventService.updateEvent(userId, updateEventRequest);
    }

    @GetMapping("/users/{userId}/events/{eventId}")
    public EventFullDto getEvent(@PathVariable Long userId,
                                 @PathVariable Long eventId) {
        return eventService.getEvent(userId, eventId);
    }

    @PatchMapping("/users/{userId}/events/{eventId}")
    public EventFullDto cancelEvent(@PathVariable Long userId,
                                    @PathVariable Long eventId) {
        return eventService.cancelEvent(userId, eventId);
    }

    @GetMapping("/events")
    public List<EventShortDto> publicGetEvents(@RequestParam(required = false) String text,
                                               @RequestParam(required = false) List<Long> categories,
                                               @RequestParam(required = false) Boolean paid,
                                               @RequestParam(required = false) @FutureOrPresent LocalDateTime rangeStart,
                                               @RequestParam(required = false) @Future LocalDateTime rangeEnd,
                                               @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                               @RequestParam String sort,
                                               @RequestParam(required = false) String locationName,
                                               @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                               @RequestParam(defaultValue = "10") @Positive Integer size,
                                               HttpServletRequest request) {
        return eventService.publicGetEvents(text, categories, paid, rangeStart,
                rangeEnd, onlyAvailable, sort, locationName, from, size, request);
    }

    @GetMapping("/events/{id}")
    public EventFullDto publicGetEvent(@PathVariable Long id,
                                       HttpServletRequest request) {
        return eventService.publicGetEvent(id, request);
    }

    @GetMapping("/admin/events")
    public List<EventFullDto> adminGetEvents(@RequestParam(required = false) List<Long> users,
                                             @RequestParam(required = false) List<String> states,
                                             @RequestParam(required = false) List<Long> categories,
                                             @RequestParam(required = false) @FutureOrPresent LocalDateTime rangeStart,
                                             @RequestParam(required = false) @Future LocalDateTime rangeEnd,
                                             @RequestParam(required = false) String locationName,
                                             @RequestParam(defaultValue = "0") Integer from,
                                             @RequestParam(defaultValue = "10") Integer size) {
        return eventService.adminGetEvents(users, states, categories, rangeStart, rangeEnd, locationName, from, size);
    }

    @PutMapping("/admin/events/{eventId}")
    public EventFullDto adminUpdateEvent(@PathVariable Long eventId,
                                         @RequestBody @Valid AdminUpdateEventDto eventDto) {
        return eventService.adminUpdateEvent(eventId, eventDto);
    }

    @PatchMapping("/admin/events/{eventId}/publish")
    public EventFullDto adminPublish(@PathVariable Long eventId) {
        return eventService.adminPublish(eventId);
    }

    @PatchMapping("/admin/events/{eventId}/reject")
    public EventFullDto adminReject(@PathVariable Long eventId) {
        return eventService.adminReject(eventId);
    }
}
