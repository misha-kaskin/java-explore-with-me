package ru.practicum.explorewithme.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.explorewithme.dto.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<EndpointHit, Long> {
    @Query(nativeQuery = true,
            value = "select e.app, e.uri, count(e.ip) " +
                    "from (" +
                    "   select app, uri, ip " +
                    "   from stats_view " +
                    "   where id in ?1" +
                    "   group by app, uri, ip" +
                    ") as e " +
                    "group by e.app, e.uri")
    List<Object[]> getViewStatsWithUniqueIp(List<Long> ids);

    @Query(nativeQuery = true,
            value = "select app, uri, count(ip) " +
                    "from stats_view " +
                    "where id in ?1 " +
                    "group by app, uri")
    List<Object[]> getViewStats(List<Long> ids);

    @Query("select e.id " +
            "from EndpointHit e " +
            "where e.timestamp > ?1 and e.timestamp < ?2 and e.uri in ?3")
    List<Long> getIdsWithUris(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("select e.id " +
            "from EndpointHit e " +
            "where e.timestamp > ?1 and e.timestamp < ?2")
    List<Long> getIds(LocalDateTime start, LocalDateTime end);
}
