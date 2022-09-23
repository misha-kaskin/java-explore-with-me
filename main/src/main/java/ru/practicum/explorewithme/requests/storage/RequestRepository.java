package ru.practicum.explorewithme.requests.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.explorewithme.requests.dto.Request;
import ru.practicum.explorewithme.requests.dto.Status;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    @Query("select case when (count(r) > 0) then true else false end " +
            "from Request r " +
            "where r.requester = ?1 and r.event = ?2")
    Boolean existsByEventIdAndUserId(Long userId, Long eventId);

    List<Request> findRequestByRequester(Long userId);

    List<Request> getRequestByEvent(Long eventId);

    List<Request> getRequestByEventAndStatus(Long eventId, Status status);
}
