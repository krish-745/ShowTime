package com.bookmyshow.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;

@Service
public class TMDBService {
    @Value("${tmdb.api.host}")
    private String apiHost;

    @Value("${tmdb.api.key}")
    private String apiKey;
    @Autowired
    private RestTemplate restTemplate;

    private int counter = 0;

    @Retryable(
        maxAttempts = 3,          // Retry only 3 times
        backoff = @Backoff(delay = 500)  // 500ms delay between retries
    )
    public ResponseEntity<Map> getNowPlayingMovies(String region, String language) {
        URI uri = UriComponentsBuilder.fromUriString(apiHost)
                .queryParam("api_key", apiKey)
                .queryParam("region", region)
                .queryParam("language", language)
                .build()
                .toUri();
        return restTemplate.getForEntity(uri, Map.class);
    }

}
