package com.diogorocha.uol_players.service;

import com.diogorocha.uol_players.client.AvengersClient;
import com.diogorocha.uol_players.client.JusticeLeagueClient;
import com.diogorocha.uol_players.entity.Player;
import com.diogorocha.uol_players.enums.CodenameGroup;
import com.diogorocha.uol_players.exception.CodenameUnavailableException;
import com.diogorocha.uol_players.repository.PlayerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CodenameService {

    private final AvengersClient avengersClient;
    private final JusticeLeagueClient justiceLeagueClient;
    private final PlayerRepository playerRepository;

    public CodenameService(
            AvengersClient avengersClient,
            JusticeLeagueClient justiceLeagueClient,
            PlayerRepository playerRepository
    ) {
        this.avengersClient = avengersClient;
        this.justiceLeagueClient = justiceLeagueClient;
        this.playerRepository = playerRepository;
    }

    public String findAvailableCodename(CodenameGroup codenameGroup) {
        List<String> codenames = fetchCodenames(codenameGroup);

        Set<String> usedCodenames = playerRepository
                .findAllByCodenameGroup(codenameGroup)
                .stream()
                .map(Player::getCodename)
                .collect(Collectors.toSet());

        return codenames
                .stream()
                .filter(codename -> !usedCodenames.contains(codename))
                .findFirst()
                .orElseThrow(() -> new CodenameUnavailableException("Não há codinomes disponíveis para o grupo selecionado"));
    }

    private List<String> fetchCodenames(CodenameGroup codenameGroup) {
        return switch (codenameGroup) {
            case AVENGERS -> avengersClient.fetchCodenames();
            case JUSTICE_LEAGUE -> justiceLeagueClient.fetchCodenames();
        };
    }
}
