package ru.practicum.explorewithme.requests.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.events.dto.Event;
import ru.practicum.explorewithme.events.dto.State;
import ru.practicum.explorewithme.events.storage.EventRepository;
import ru.practicum.explorewithme.exception.NotFoundException;
import ru.practicum.explorewithme.exception.ValidationException;
import ru.practicum.explorewithme.requests.dto.RequestDto;
import ru.practicum.explorewithme.requests.dto.Status;
import ru.practicum.explorewithme.requests.storage.RequestRepository;
import ru.practicum.explorewithme.users.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public RequestDto addRequest(Long userId, Long eventId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь не найден");
        }

        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException("Событие не найдено");
        }

        if (requestRepository.existsByEventIdAndUserId(userId, eventId)) {
            throw new ValidationException("Запрос уже был отправлен");
        }

        Event event = eventRepository.getReferenceById(eventId);

        if (event.getInitiator().equals(userId)) {
            throw new ValidationException("Инициатор не может добавлять запрос");
        }

        if (!event.getState().equals(State.PUBLISHED)) {
            throw new ValidationException("Нельзя учавствовать в неопубликованном событии");
        }

        if (event.getParticipantLimit() > 0) {
            if (event.getConfirmedRequests().equals(event.getParticipantLimit())) {
                throw new ValidationException("Достигнут лимит по количеству запросов");
            }
        }

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            Long requests = event.getConfirmedRequests();
            event.setConfirmedRequests(++requests);

            eventRepository.save(event);
            return requestRepository.save(RequestDto.builder()
                    .requester(userId)
                    .event(eventId)
                    .created(LocalDateTime.now())
                    .status(Status.APPROVED)
                    .build());
        } else {
            return requestRepository.save(RequestDto.builder()
                    .requester(userId)
                    .event(eventId)
                    .created(LocalDateTime.now())
                    .status(Status.PENDING)
                    .build());
        }
    }

    @Override
    public List<RequestDto> getRequests(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Не найден пользователь");
        }

        List<RequestDto> requestDtoList = requestRepository.getRequestDtoByUserId(userId);

        if (requestDtoList.isEmpty()) {
            throw new NotFoundException("Запросы не найдены");
        }

        return requestDtoList;
    }

    @Override
    public RequestDto cancelRequest(Long userId, Long requestId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь не найден");
        }

        if (!requestRepository.existsById(requestId)) {
            throw new NotFoundException("Запрос не найден");
        }

        RequestDto requestDto = requestRepository.getReferenceById(requestId);

        if (!requestDto.getRequester().equals(userId)) {
            throw new ValidationException("Неверный id пользователя");
        }

        if (requestDto.getStatus().equals(Status.CANCELED)) {
            throw new ValidationException("Повторное удаление события");
        }

        if (requestDto.getStatus().equals(Status.APPROVED)) {
            Event event = eventRepository.getReferenceById(requestDto.getEvent());

            Long requests = event.getConfirmedRequests();
            event.setConfirmedRequests(--requests);

            eventRepository.save(event);
        }

        requestDto.setStatus(Status.CANCELED);
        return requestRepository.save(requestDto);
    }

    @Override
    public List<RequestDto> getRequestsByUserEventId(Long userId, Long eventId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Не найден пользователь");
        }

        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException("Событие не найдено");
        }

        List<RequestDto> requestDtoList = requestRepository.getRequestDtoByUserIdEventId(userId, eventId);

        if (requestDtoList.isEmpty()) {
            throw new NotFoundException("Не найдены запросы");
        }

        return requestDtoList;
    }

    @Override
    public RequestDto confirmRequest(Long userId, Long eventId, Long reqId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Не найден пользователь");
        }

        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException("Не найдено событие");
        }

        if (!requestRepository.existsById(reqId)) {
            throw new NotFoundException("Запрос не найден");
        }

        Event event = eventRepository.getReferenceById(eventId);

        if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
            return requestRepository.getReferenceById(reqId);
        }

        if (event.getConfirmedRequests().equals(event.getParticipantLimit())) {
            throw new ValidationException("Достигнут лимит участников");
        }

        RequestDto requestDto = requestRepository.getReferenceById(reqId);

        if (!requestDto.getStatus().equals(Status.PENDING)) {
            throw new ValidationException("Некорректный статус события");
        }

        requestDto.setStatus(Status.APPROVED);
        requestRepository.save(requestDto);

        Long requests = event.getConfirmedRequests();
        event.setConfirmedRequests(++requests);
        eventRepository.save(event);

        if (requests.equals(event.getParticipantLimit())) {
            List<RequestDto> requestDtoList = requestRepository
                    .getRequestDtoByEventIdAndStatus(eventId, Status.PENDING);

            for (RequestDto request : requestDtoList) {
                request.setStatus(Status.REJECTED);
                requestRepository.save(request);
            }
        }

        return requestDto;
    }

    @Override
    public RequestDto rejectRequest(Long userId, Long eventId, Long reqId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Не найден пользователь");
        }

        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException("Не найдено событие");
        }

        if (!requestRepository.existsById(reqId)) {
            throw new NotFoundException("Запрос не найден");
        }

        RequestDto requestDto = requestRepository.getReferenceById(reqId);

        if (!requestDto.getStatus().equals(Status.PENDING)) {
            throw new ValidationException("Некорректный статус");
        }

        requestDto.setStatus(Status.REJECTED);
        return requestRepository.save(requestDto);
    }
}
