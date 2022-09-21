package ru.practicum.explorewithme.events.service;

import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.explorewithme.events.dto.StatData;

import java.time.LocalDateTime;

public class Client {
    private static String URL = "http://stats-server:9090";
    private static String API = "/hit";
    private final static RestTemplate restTemplate = new RestTemplate();

    public static void post(String app, String remoteAddr, String requestURI) {
        HttpEntity<StatData> httpEntity = new HttpEntity<>(StatData.builder()
                .app(app)
                .ip(remoteAddr)
                .uri(requestURI)
                .timestamp(LocalDateTime.now())
                .build());

        restTemplate.postForObject(URL + API, httpEntity, StatData.class);
    }
}
