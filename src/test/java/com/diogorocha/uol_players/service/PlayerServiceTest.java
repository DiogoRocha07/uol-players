package com.diogorocha.uol_players.service;

import com.diogorocha.uol_players.dto.CreatePlayerRequest;
import com.diogorocha.uol_players.dto.PlayerResponse;
import com.diogorocha.uol_players.entity.Player;
import com.diogorocha.uol_players.enums.CodenameGroup;
import com.diogorocha.uol_players.repository.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlayerServiceTest {

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private CodenameService codenameService;

    private PlayerService playerService;

    @BeforeEach
    void setUp() {
        playerService = new PlayerService(
                playerRepository,
                codenameService
        );
    }

    @Test
    void shouldCreatePlayerWithAvailableCodename() {
        CreatePlayerRequest request = new CreatePlayerRequest(
                "Diogo",
                "diogo@email.com",
                "1199999999",
                CodenameGroup.AVENGERS
        );

        when(codenameService.findAvailableCodename(CodenameGroup.AVENGERS)).thenReturn("Hulk");

        when(playerRepository.save(org.mockito.ArgumentMatchers.any(Player.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PlayerResponse response = playerService.create(request);

        assertThat(response.name()).isEqualTo("Diogo");
        assertThat(response.email()).isEqualTo("diogo@email.com");
        assertThat(response.phone()).isEqualTo("1199999999");
        assertThat(response.codename()).isEqualTo("Hulk");
        assertThat(response.codenameGroup()).isEqualTo(CodenameGroup.AVENGERS);

        verify(codenameService).findAvailableCodename(CodenameGroup.AVENGERS);

        verify(playerRepository).save(org.mockito.ArgumentMatchers.any(Player.class));
    }

    @Test
    void shouldSavePlayerWithCorrectData() {
        CreatePlayerRequest request = new CreatePlayerRequest(
                "Maria",
                "maria@email.com",
                null,
                CodenameGroup.JUSTICE_LEAGUE
        );

        when(codenameService.findAvailableCodename(CodenameGroup.JUSTICE_LEAGUE)).thenReturn("Flash");

        when(playerRepository.save(org.mockito.ArgumentMatchers.any(Player.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        playerService.create(request);

        ArgumentCaptor<Player> playerCaptor = ArgumentCaptor.forClass(Player.class);

        verify(playerRepository).save(playerCaptor.capture());

        Player savedPlayer = playerCaptor.getValue();

        assertThat(savedPlayer.getName()).isEqualTo("Maria");
        assertThat(savedPlayer.getEmail()).isEqualTo("maria@email.com");
        assertThat(savedPlayer.getPhone()).isNull();
        assertThat(savedPlayer.getCodename()).isEqualTo("Flash");
        assertThat(savedPlayer.getCodenameGroup()).isEqualTo(CodenameGroup.JUSTICE_LEAGUE);
    }

    @Test
    void shouldReturnAllPlayers() {
        Player firstPlayer = new Player(
                "Diogo",
                "diogo@email.com",
                "11999999999",
                "Hulk",
                CodenameGroup.AVENGERS
        );

        Player secondPlayer = new Player(
                "Maria",
                "maria@email.com",
                null,
                "Flash",
                CodenameGroup.JUSTICE_LEAGUE
        );

        when(playerRepository.findAll()).thenReturn(List.of(firstPlayer, secondPlayer));

        List<PlayerResponse> players = playerService.findAll();

        assertThat(players).hasSize(2);

        assertThat(players.get(0).name()).isEqualTo("Diogo");
        assertThat(players.get(0).email()).isEqualTo("diogo@email.com");
        assertThat(players.get(0).codename()).isEqualTo("Hulk");
        assertThat(players.get(0).codenameGroup()).isEqualTo(CodenameGroup.AVENGERS);

        assertThat(players.get(1).name()).isEqualTo("Maria");
        assertThat(players.get(1).email()).isEqualTo("maria@email.com");
        assertThat(players.get(1).codename()).isEqualTo("Flash");
        assertThat(players.get(1).codenameGroup()).isEqualTo(CodenameGroup.JUSTICE_LEAGUE);

        verify(playerRepository).findAll();
    }
}
