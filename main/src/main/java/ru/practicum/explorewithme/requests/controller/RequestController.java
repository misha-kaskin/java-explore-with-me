package ru.practicum.explorewithme.requests.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.requests.model.Request;
import ru.practicum.explorewithme.requests.service.RequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class RequestController {
    private final RequestService requestService;

    @PostMapping("/users/{userId}/requests")
    public Request addRequest(@PathVariable Long userId,
                              @RequestParam Long eventId) {
        return requestService.addRequest(userId, eventId);
    }

    @GetMapping("/users/{userId}/requests")
    public List<Request> getRequests(@PathVariable Long userId) {
        return requestService.getRequests(userId);
    }

    @PatchMapping("/users/{userId}/requests/{requestId}/cancel")
    public Request cancelRequest(@PathVariable Long userId,
                                 @PathVariable Long requestId) {
        return requestService.cancelRequest(userId, requestId);
    }

    @GetMapping("/users/{userId}/events/{eventId}/requests")
    public List<Request> getRequestByUserEventId(@PathVariable Long userId,
                                                 @PathVariable Long eventId) {
        return requestService.getRequestsByUserEventId(userId, eventId);
    }

    @PatchMapping("/users/{userId}/events/{eventId}/requests/{reqId}/confirm")
    public Request confirmRequest(@PathVariable Long userId,
                                  @PathVariable Long eventId,
                                  @PathVariable Long reqId) {
        return requestService.confirmRequest(userId, eventId, reqId);
    }

    @PatchMapping("/users/{userId}/events/{eventId}/requests/{reqId}/reject")
    public Request rejectRequest(@PathVariable Long userId,
                                 @PathVariable Long eventId,
                                 @PathVariable Long reqId) {
        return requestService.rejectRequest(userId, eventId, reqId);
    }
}
