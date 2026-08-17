package com.diogorocha.uol_players.controller;

import com.diogorocha.uol_players.dto.CreatePlayerRequest;
import com.diogorocha.uol_players.dto.PlayerResponse;
import com.diogorocha.uol_players.service.PlayerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/players")
public class PlayerController {

    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @PostMapping
    public ResponseEntity<PlayerResponse> create(
            @Valid @RequestBody CreatePlayerRequest request
    ) {
        PlayerResponse response = playerService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<PlayerResponse>> findAll() {
        List<PlayerResponse> players = playerService.findAll();

        return ResponseEntity.ok(players);
    }
}
