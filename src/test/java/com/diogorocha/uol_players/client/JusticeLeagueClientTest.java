package com.diogorocha.uol_players.client;

import com.diogorocha.uol_players.exception.CodenameSourceException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(JusticeLeagueClient.class)
@TestPropertySource(properties = {
        "codename.sources.justice-league-url=https://example.com/liga_da_justica.xml"
})
class JusticeLeagueClientTest {
    @Autowired
    JusticeLeagueClient justiceLeagueClient;

    @Autowired
    MockRestServiceServer server;

    @Test
    void shouldFetchJusticeLeagueCodenames() {
        String responseBody = """
                <liga_da_justica>
                    <codinomes>
                        <codinome>Lanterna Verde</codinome>
                        <codinome>Flash</codinome>
                        <codinome>Batman</codinome>
                    </codinomes>
                </liga_da_justica>
                """;

        server.expect(requestTo("https://example.com/liga_da_justica.xml"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(responseBody, MediaType.TEXT_PLAIN));

        List<String> codenames = justiceLeagueClient.fetchCodenames();

        assertThat(codenames).containsExactly("Lanterna Verde", "Flash", "Batman");

        server.verify();
    }

    @Test
    void shouldThrowExceptionWhenJusticeLeagueXmlIsInvalid() {
        String responseBody = """
                    <liga_da_justica>
                        <codinomes>
                """;

        server.expect(requestTo("https://example.com/liga_da_justica.xml"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(responseBody, MediaType.TEXT_PLAIN));

        assertThatThrownBy(() -> justiceLeagueClient.fetchCodenames())
                .isInstanceOf(CodenameSourceException.class)
                .hasMessage("Não foi possível interpretar os codinomes da Liga da Justiça");

        server.verify();
    }

    @Test
    void shouldThrowExceptionWhenJusticeLeagueSourceIsUnavailable() {
        server.expect(requestTo("https://example.com/liga_da_justica.xml"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        assertThatThrownBy(() -> justiceLeagueClient.fetchCodenames())
                .isInstanceOf(CodenameSourceException.class)
                .hasMessage("Não foi possível acessar a fonte de codinomes da Liga da Justiça");

        server.verify();
    }
}
