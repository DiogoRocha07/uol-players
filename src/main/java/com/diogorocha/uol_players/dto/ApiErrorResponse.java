package com.diogorocha.uol_players.dto;

import java.time.Instant;

public record ApiErrorResponse(
        Instant timestamp,
        int Status,
        String error,
        String message,
        String path

) {
}
