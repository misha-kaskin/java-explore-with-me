package ru.practicum.explorewithme.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.dto.EndpointHit;
import ru.practicum.explorewithme.dto.ViewStats;
import ru.practicum.explorewithme.service.StatsServiceImpl;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class StatsController {
    private final StatsServiceImpl statsService;

    @PostMapping("/hit")
    public void addStat(@RequestBody @Valid EndpointHit endpointHit) {
        statsService.addStat(endpointHit);
    }

    @GetMapping("/stats")
    public List<ViewStats> getStats(@RequestParam LocalDateTime start,
                                    @RequestParam LocalDateTime end,
                                    @RequestParam(required = false) List<String> uris,
                                    @RequestParam(required = false) Boolean unique) {
        return statsService.getStats(start, end, uris, unique);
    }
}
