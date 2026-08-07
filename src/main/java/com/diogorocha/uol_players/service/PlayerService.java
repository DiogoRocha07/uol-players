package com.diogorocha.uol_players.service;

import com.diogorocha.uol_players.dto.CreatePlayerRequest;
import com.diogorocha.uol_players.dto.PlayerResponse;
import com.diogorocha.uol_players.entity.Player;
import com.diogorocha.uol_players.mapper.PlayerMapper;
import com.diogorocha.uol_players.repository.PlayerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final CodenameService codenameService;

    public PlayerService(
            PlayerRepository playerRepository,
            CodenameService codenameService
    ) {
        this.playerRepository = playerRepository;
        this.codenameService = codenameService;
    }

    @Transactional
    public PlayerResponse create(CreatePlayerRequest request) {
        String codename = codenameService.findAvailableCodename(
                request.codenameGroup()
        );

        Player player = new Player(
                request.name(),
                request.email(),
                request.phone(),
                codename,
                request.codenameGroup()
        );

        Player savedPlayer = playerRepository.save(player);

        return PlayerMapper.toResponse(savedPlayer);
    }
}
