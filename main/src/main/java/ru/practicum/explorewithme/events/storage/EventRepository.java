package ru.practicum.explorewithme.events.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.explorewithme.events.model.Event;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {
    @Query("select case when (count(e)) > 0 " +
            "then true " +
            "else false end " +
            "from Event e where e.category = ?1")
    Boolean existsByCategoryId(Long id);

    Page<Event> findAllByInitiator(Long userId, Pageable pageable);
}
