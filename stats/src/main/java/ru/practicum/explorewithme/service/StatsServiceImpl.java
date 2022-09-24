package ru.practicum.explorewithme.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.model.EndpointHit;
import ru.practicum.explorewithme.model.ViewStats;
import ru.practicum.explorewithme.exception.NotFoundException;
import ru.practicum.explorewithme.storage.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

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

        List<ViewStats> viewStats;

        if (unique == null || !unique) {
            viewStats = statsRepository.getViewStats(ids);
        } else {
            viewStats = statsRepository.getViewStatsWithUniqueIp(ids);
        }

        return viewStats;
    }
}
