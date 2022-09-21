package ru.practicum.explorewithme.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.dto.EndpointHit;
import ru.practicum.explorewithme.dto.ViewStats;
import ru.practicum.explorewithme.exception.NotFoundException;
import ru.practicum.explorewithme.storage.StatsRepository;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;

    @Override
    public void addStat(EndpointHit endpointHit) {
        statsRepository.save(endpointHit);
    }

    @Override
    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        List<Long> ids;

        if (uris == null || uris.isEmpty()) {
            ids = statsRepository.getIds(start, end);
        } else {
            ids = statsRepository.getIdsWithUris(start, end, uris);
        }

        if (ids.isEmpty()) {
            throw new NotFoundException("Не найдены записи");
        }

        List<Object[]> objects;

        if (unique == null || !unique) {
            objects = statsRepository.getViewStats(ids);
        } else {
            objects = statsRepository.getViewStatsWithUniqueIp(ids);
        }

        List<ViewStats> viewStats = objects.stream()
                .map(obj -> ViewStats.builder()
                        .app(obj[0].toString())
                        .uri(obj[1].toString())
                        .hits((BigInteger) obj[2])
                        .build())
                .collect(Collectors.toList());

        return viewStats;
    }
}
