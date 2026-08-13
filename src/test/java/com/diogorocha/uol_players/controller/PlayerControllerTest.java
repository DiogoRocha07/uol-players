package com.diogorocha.uol_players.controller;

import com.diogorocha.uol_players.dto.CreatePlayerRequest;
import com.diogorocha.uol_players.dto.PlayerResponse;
import com.diogorocha.uol_players.enums.CodenameGroup;
import com.diogorocha.uol_players.service.PlayerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
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
    void shouldReturnBadRequestWhenRequestIsInvalid() throws Exception {
        String requestBody = """
                {
                    "name": "",
                    "email": "email-invalido",
                    "phone": "11999999999",
                    "codenameGroup": "AVENGERS"
                }
                """;

        mockMvc.perform(post("/players")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(playerService);
    }
}
