package com.diogorocha.uol_players.client;

import com.diogorocha.uol_players.client.dto.AvengerCodenameResponse;
import com.diogorocha.uol_players.client.dto.AvengersResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class AvengersClient {
    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final String avengersUrl;

    public AvengersClient(
            RestClient.Builder restClientBuilder,
            @Value("${codename.sources.avengers-url}") String avengersUrl
    ) {
        this.restClient = restClientBuilder.build();
        this.objectMapper = new ObjectMapper();
        this.avengersUrl = avengersUrl;
    }

    public List<String> fetchCodenames() {
        String jsonResponse = restClient
                .get()
                .uri(avengersUrl)
                .retrieve()
                .body(String.class);

        if (jsonResponse == null || jsonResponse.isBlank()) {
            throw new IllegalStateException("Não foi possível obter os codinomes dos Vingadores");
        }

        try {
            AvengersResponse response = objectMapper.readValue(
                    jsonResponse,
                    AvengersResponse.class
            );

            if (response.avengers() == null) {
                throw new IllegalStateException("Não foi possível obter os codinomes dos Vingadores");
            }

            return response.avengers()
                    .stream()
                    .map(AvengerCodenameResponse::codename)
                    .toList();
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Não foi possível interpretar os codinomes dos Vingadores", exception);
        }
    }
}
