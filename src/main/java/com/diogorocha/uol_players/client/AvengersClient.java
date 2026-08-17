package com.diogorocha.uol_players.client;

import com.diogorocha.uol_players.client.dto.AvengerCodenameResponse;
import com.diogorocha.uol_players.client.dto.AvengersResponse;
import com.diogorocha.uol_players.exception.CodenameSourceException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

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
        try {
            String jsonResponse = restClient
                    .get()
                    .uri(avengersUrl)
                    .retrieve()
                    .body(String.class);

            if (jsonResponse == null || jsonResponse.isBlank()) {
                throw new CodenameSourceException("A fonte de codinomes dos Vingadores retornou uma resposta vazia");
            }

            AvengersResponse response = objectMapper.readValue(
                    jsonResponse,
                    AvengersResponse.class
            );

            if (response.avengers() == null) {
                throw new CodenameSourceException("A fonte de codinomes dos Vingadores retornou uma resposta inválida");
            }

            return response.avengers()
                    .stream()
                    .map(AvengerCodenameResponse::codename)
                    .toList();
        } catch (RestClientException exception) {
            throw new CodenameSourceException("Não foi possível acessar a fonte de codinomes dos Vingadores", exception);
        } catch (JsonProcessingException exception) {
            throw new CodenameSourceException("Não foi possível interpretar os codinomes dos Vingadores", exception);
        }
    }
}
