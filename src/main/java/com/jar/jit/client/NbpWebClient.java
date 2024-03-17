package com.jar.jit.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Currency;

@Slf4j
@Component
@RequiredArgsConstructor
public class NbpWebClient {

    private final WebClient nbpWebclient;

    public RateResponse getRate(Currency currency) {
        try {
            return nbpWebclient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path("exchangerates/rates/A")
                            .pathSegment(currency.getCurrencyCode())
                            .build())
                    .retrieve()
                    .toEntity(RateResponse.class)
                    .mapNotNull(HttpEntity::getBody)
                    .block();
        } catch (Exception e) {
            log.error("Request failed", e);
            throw new IllegalArgumentException("Couldn't fetch rate");
        }
    }
}
