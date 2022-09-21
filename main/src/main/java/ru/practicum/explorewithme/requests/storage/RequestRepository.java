package ru.practicum.explorewithme.requests.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.explorewithme.requests.dto.RequestDto;
import ru.practicum.explorewithme.requests.dto.Status;

import java.util.List;

public interface RequestRepository extends JpaRepository<RequestDto, Long> {
    @Query("select case when (count(r) > 0) then true else false end " +
            "from RequestDto r " +
            "where r.requester = ?1 and r.event = ?2")
    Boolean existsByEventIdAndUserId(Long userId, Long eventId);

    @Query("select r from RequestDto r where r.requester = ?1")
    List<RequestDto> getRequestDtoByUserId(Long userId);

    @Query("select r from RequestDto r where r.requester = ?1 and r.event = ?2")
    List<RequestDto> getRequestDtoByUserIdEventId(Long userId, Long eventId);

    @Query("select r from RequestDto r where r.event = ?1 and r.status = ?2")
    List<RequestDto> getRequestDtoByEventIdAndStatus(Long eventId, Status status);
}
