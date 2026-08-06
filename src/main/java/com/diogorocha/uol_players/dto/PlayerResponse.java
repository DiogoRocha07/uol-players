package com.diogorocha.uol_players.dto;

import com.diogorocha.uol_players.enums.CodenameGroup;

public record PlayerResponse(Long id, String name, String email, String phone, String codename, CodenameGroup codenameGroup) {
}
