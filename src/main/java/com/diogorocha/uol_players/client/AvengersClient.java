package com.diogorocha.uol_players.client;

import com.diogorocha.uol_players.client.dto.AvengerCodenameResponse;
import com.diogorocha.uol_players.client.dto.AvengersResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class AvengersClient {
    private final RestClient restClient;
    private final String avengersUrl;

    public AvengersClient(
            RestClient.Builder restClientBuilder,
            @Value("${codename.sources.avengers-url}") String avengersUrl
    ){
        this.restClient = restClientBuilder.build();
        this.avengersUrl = avengersUrl;
    }

    public List<String> fetchCodenames() {
        AvengersResponse response = restClient.get().uri(avengersUrl).retrieve().body(AvengersResponse.class);

        if (response == null || response.avengers() == null) {
            throw new IllegalStateException("Não foi possível obter os codinomes dos Vingadores");
        }
        return response.avengers().stream().map(AvengerCodenameResponse::codename).toList();

    }
}
