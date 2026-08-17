package com.diogorocha.uol_players.mapper;

import com.diogorocha.uol_players.dto.PlayerResponse;
import com.diogorocha.uol_players.entity.Player;

public final class PlayerMapper {

    private PlayerMapper(){
    }

    public static PlayerResponse toResponse(Player player) {
        return new PlayerResponse(
                player.getId(),
                player.getName(),
                player.getEmail(),
                player.getPhone(),
                player.getCodename(),
                player.getCodenameGroup()
        );
    }
}
