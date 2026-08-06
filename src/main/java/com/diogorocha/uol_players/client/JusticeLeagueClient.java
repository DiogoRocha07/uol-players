package com.diogorocha.uol_players.client;

import com.diogorocha.uol_players.client.dto.JusticeLeagueCodenamesResponse;
import com.diogorocha.uol_players.client.dto.JusticeLeagueResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class JusticeLeagueClient {

    private final RestClient restClient;
    private final XmlMapper xmlMapper;
    private final String justiceLeagueUrl;

    public JusticeLeagueClient(
            RestClient.Builder restClientBuilder,
            @Value("${codename.sources.justice-league-url}")
            String justiceLeagueUrl
    ) {
        this.restClient = restClientBuilder.build();
        this.xmlMapper = new XmlMapper();
        this.justiceLeagueUrl = justiceLeagueUrl;
    }

    public List<String> fetchCodenames() {
        String xmlResponse = restClient
                .get()
                .uri(justiceLeagueUrl)
                .retrieve()
                .body(String.class);

        if (xmlResponse == null || xmlResponse.isBlank()) {
            throw new IllegalStateException("Não foi possível obter os codinomes da Liga da Justiça");
        }

        try {
            JusticeLeagueResponse response = xmlMapper.readValue(xmlResponse, JusticeLeagueResponse.class);

            JusticeLeagueCodenamesResponse codenamesResponse = response.codenames();

            if (codenamesResponse == null || codenamesResponse.values() == null) {
                throw new IllegalStateException("Não foi possível obter os codinomes da Liga da Justiça");
            }

            return codenamesResponse.values();
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Não foi possível interpretar os codinomes da Liga da Justiça", exception);
        }
    }
}
