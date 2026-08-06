package com.diogorocha.uol_players.client;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(AvengersClient.class)
@TestPropertySource(properties = {
        "codename.sources.avengers-url=https://example.com/vingadores.json"
})
class AvengersClientTest {

    @Autowired
    AvengersClient avengersClient;

    @Autowired
    MockRestServiceServer server;

    @Test
    void shouldFetchAvengersCodenames() {
        String responseBody = """
                {
                    "vingadores": [
                        {
                        "codinome": "Hulk"
                        },
                        {
                        "codinome": "Capitão América"
                        }
                    ]
                }
                """;

        server.expect(requestTo("https://example.com/vingadores.json"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        List<String> codenames = avengersClient.fetchCodenames();

        assertThat(codenames).containsExactly("Hulk", "Capitão América");

        server.verify();
    }
}
