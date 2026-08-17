package com.diogorocha.uol_players.controller;

import com.diogorocha.uol_players.dto.CreatePlayerRequest;
import com.diogorocha.uol_players.dto.PlayerResponse;
import com.diogorocha.uol_players.enums.CodenameGroup;
import com.diogorocha.uol_players.exception.CodenameSourceException;
import com.diogorocha.uol_players.exception.CodenameUnavailableException;
import com.diogorocha.uol_players.service.PlayerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PlayerController.class)
public class PlayerControllerTest {

    private final MockMvc mockMvc;

    @MockitoBean
    private PlayerService playerService;

    @Autowired
    PlayerControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Test
    void shouldCreatePlayer() throws Exception {
        PlayerResponse response = new PlayerResponse(
                1L,
                "Diogo",
                "diogo@email.com",
                "11949911010",
                "Hulk",
                CodenameGroup.AVENGERS
        );

        when(playerService.create(any(CreatePlayerRequest.class))).thenReturn(response);

        String requestBody = """
                {
                    "name": "Diogo",
                    "email": "diogo@email.com",
                    "phone": "11949911010",
                    "codenameGroup": "AVENGERS"
                }
                """;

        mockMvc.perform(post("/players")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Diogo"))
                .andExpect(jsonPath("$.email").value("diogo@email.com"))
                .andExpect(jsonPath("$.phone").value("11949911010"))
                .andExpect(jsonPath("$.codename").value("Hulk"))
                .andExpect(jsonPath("$.codenameGroup").value("AVENGERS"));
    }

    @Test
    void shouldReturnValidationErrorsWhenRequestIsInvalid() throws Exception {
        String requestBody = """
                {
                    "name": "",
                    "email": "email-invalido",
                    "phone": "11999999999",
                    "codenameGroup": "AVENGERS"
                }
                """;

        mockMvc.perform(
                        post("/players")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(
                        jsonPath("$.errors.name")
                                .value("Nome é obrigatório")
                )
                .andExpect(
                        jsonPath("$.errors.email")
                                .value("E-mail deve possuir um formato válido")
                )
                .andExpect(jsonPath("$.path").value("/players"));

        verifyNoInteractions(playerService);
    }

    @Test
    void shouldReturnAllPlayers() throws Exception {
        PlayerResponse firstPlayer = new PlayerResponse(
                1L,
                "Diogo",
                "diogo@email.com",
                "11999999999",
                "Hulk",
                CodenameGroup.AVENGERS
        );

        PlayerResponse secondPlayer = new PlayerResponse(
                2L,
                "Maria",
                "maria@email.com",
                null,
                "Flash",
                CodenameGroup.JUSTICE_LEAGUE
        );

        when(playerService.findAll()).thenReturn(List.of(firstPlayer, secondPlayer));

        mockMvc.perform(get("/players"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Diogo"))
                .andExpect(jsonPath("$[0].codename").value("Hulk"))
                .andExpect(jsonPath("$[0].codenameGroup").value("AVENGERS"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Maria"))
                .andExpect(jsonPath("$[1].codename").value("Flash"))
                .andExpect(jsonPath("$[1].codenameGroup").value("JUSTICE_LEAGUE")
                );
    }

    @Test
    void shouldReturnConflictWhenNoCodenameIsAvailable() throws Exception {
        doThrow(
                new CodenameUnavailableException(
                        "Não há codinomes disponíveis para o grupo selecionado"
                )
        )
                .when(playerService)
                .create(any(CreatePlayerRequest.class));

        String requestBody = """
                {
                    "name": "Diogo",
                    "email": "diogo@email.com",
                    "phone": "11999999999",
                    "codenameGroup": "AVENGERS"
                }
                """;

        mockMvc.perform(post("/players").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").value("Não há codinomes disponíveis para o grupo selecionado"))
                .andExpect(jsonPath("$.path").value("/players"));
    }

    @Test
    void shouldReturnBadGatewayWhenCodenameSourceFails() throws Exception {
        when(playerService.create(any(CreatePlayerRequest.class)))
                .thenThrow(new CodenameSourceException("Não foi possível acessar a fonte de codinomes dos Vingadores"));

        String requestBody = """
                {
                    "name": "Diogo",
                    "email": "diogo@email.com",
                    "phone": "11999999999",
                    "codenameGroup": "AVENGERS"
                }
                """;

        mockMvc.perform(post("/players")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.status").value(502))
                .andExpect(jsonPath("$.error").value("Bad Gateway"))
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "Não foi possível acessar a fonte de codinomes dos Vingadores"
                                )
                )
                .andExpect(jsonPath("$.path").value("/players"));
    }
}
