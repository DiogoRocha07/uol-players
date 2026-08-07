package com.diogorocha.uol_players.service;

import com.diogorocha.uol_players.client.AvengersClient;
import com.diogorocha.uol_players.client.JusticeLeagueClient;
import com.diogorocha.uol_players.entity.Player;
import com.diogorocha.uol_players.enums.CodenameGroup;
import com.diogorocha.uol_players.exception.CodenameUnavailableException;
import com.diogorocha.uol_players.repository.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CodenameServiceTest {

    @Mock
    private AvengersClient avengersClient;

    @Mock
    private JusticeLeagueClient justiceLeagueClient;

    @Mock
    private PlayerRepository playerRepository;

    private CodenameService codenameService;

    @BeforeEach
    void setUp() {
        codenameService = new CodenameService(avengersClient, justiceLeagueClient, playerRepository);
    }

    @Test
    void shouldReturnFirstAvailableAvengersCodename() {
        when(avengersClient.fetchCodenames()).thenReturn(List.of("Hulk", "Thor"));

        when(playerRepository.findAllByCodenameGroup(CodenameGroup.AVENGERS)).thenReturn(List.of());

        String codename = codenameService.findAvailableCodename(CodenameGroup.AVENGERS);

        assertThat(codename).isEqualTo("Hulk");

        verify(avengersClient).fetchCodenames();
        verify(justiceLeagueClient, never()).fetchCodenames();
    }

    @Test
    void shouldReturnFirstAvailableJusticeLeagueCodename() {
        when(justiceLeagueClient.fetchCodenames()).thenReturn(List.of("Batman", "Flash"));

        when(playerRepository.findAllByCodenameGroup(CodenameGroup.JUSTICE_LEAGUE)).thenReturn(List.of());

        String codename = codenameService.findAvailableCodename(CodenameGroup.JUSTICE_LEAGUE);

        assertThat(codename).isEqualTo("Batman");

        verify(justiceLeagueClient).fetchCodenames();
        verify(avengersClient, never()).fetchCodenames();
    }

    @Test
    void shouldIgnoreAlreadyUserCodename() {
        Player existingPlayer = new Player(
                "João",
                "joao@email.com",
                null,
                "Hulk",
                CodenameGroup.AVENGERS
        );

        when(avengersClient.fetchCodenames()).thenReturn(List.of("Hulk", "Thor"));

        when(playerRepository.findAllByCodenameGroup(CodenameGroup.AVENGERS)).thenReturn(List.of(existingPlayer));

        String codename = codenameService.findAvailableCodename(CodenameGroup.AVENGERS);

        assertThat(codename).isEqualTo("Thor");
    }

    @Test
    void shouldThrowExceptionWhenAllCodenamesAreUser() {
        Player firstPlayer = new Player(
                "João",
                "joao@email.com",
                null,
                "Hulk",
                CodenameGroup.AVENGERS
        );

        Player secondPlayer = new Player(
                "Maria",
                "maria@email.com",
                null,
                "Thor",
                CodenameGroup.AVENGERS
        );

        when(avengersClient.fetchCodenames()).thenReturn(List.of("Hulk", "Thor"));

        when(playerRepository.findAllByCodenameGroup(CodenameGroup.AVENGERS)).thenReturn(List.of(firstPlayer, secondPlayer));

        assertThatThrownBy(() -> codenameService.findAvailableCodename(CodenameGroup.AVENGERS))
                .isInstanceOf(CodenameUnavailableException.class)
                .hasMessage("Não há codinomes disponíveis para o grupo selecionado");
    }
}
