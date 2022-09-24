package ru.practicum.explorewithme.events.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.explorewithme.events.model.StatData;

import java.time.LocalDateTime;

@Service
public class Client {
    private static final RestTemplate restTemplate = new RestTemplate();
    @Value("${stats-server-url}")
    private String url;
    private String api = "/hit";

    public void post(String app, String remoteAddr, String requestURI) {
        HttpEntity<StatData> httpEntity = new HttpEntity<>(StatData.builder()
                .app(app)
                .ip(remoteAddr)
                .uri(requestURI)
                .timestamp(LocalDateTime.now())
                .build());

        restTemplate.postForObject(url + api, httpEntity, StatData.class);
    }
}
