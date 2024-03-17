package com.jar.jit.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient NbpWebClient(WebClient.Builder webclientBuilder, @Value("${api.nbp.uri}") String uri) {
        return webclientBuilder
                .baseUrl(uri)
                .build();
    }
}
