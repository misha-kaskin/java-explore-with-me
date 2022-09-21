package ru.practicum.explorewithme.events.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.explorewithme.events.dto.Event;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {
    @Query("select case when (count(e)) > 0 " +
            "then true " +
            "else false end " +
            "from Event e where e.category = ?1")
    Boolean existsByCategoryId(Long id);

    @Query("select e from Event e where e.initiator = ?1")
    Page<Event> findAllByUserId(Long userId, Pageable pageable);

    @Query("select e from Event e where e.id in ?1")
    List<Event> findAllById(List<Long> id);

    @Query("select case when (count(l) > 0) then true else false end " +
            "from SpecificLocation l " +
            "where ((l.lon - ?1) * (l.lon - ?1) + (l.lat - ?2) * (l.lat - ?2)) < (l.radius * l.radius)")
    Boolean belongsLocation(Float lon, Float lat);

    @Query("select e.id " +
            "from Event e " +
            "where ((e.lon - ?1) * (e.lon - ?1) + (e.lat - ?2) * (e.lat - ?2)) < (?3 * ?3)")
    List<Long> findAllIdsByLocation(Float lon, Float lat, Float radius);
}
