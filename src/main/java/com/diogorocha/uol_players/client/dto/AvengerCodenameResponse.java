package com.diogorocha.uol_players.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AvengerCodenameResponse(

        @JsonProperty("codinome")
        String codename
) {
}
