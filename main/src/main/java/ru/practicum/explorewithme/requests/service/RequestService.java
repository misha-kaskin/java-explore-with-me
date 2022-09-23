package ru.practicum.explorewithme.requests.service;

import ru.practicum.explorewithme.requests.dto.Request;

import java.util.List;

public interface RequestService {
    Request addRequest(Long userId, Long eventId);

    List<Request> getRequests(Long userId);

    Request cancelRequest(Long userId, Long requestId);

    List<Request> getRequestsByUserEventId(Long userId, Long eventId);

    Request confirmRequest(Long userId, Long eventId, Long reqId);

    Request rejectRequest(Long userId, Long eventId, Long reqId);
}
