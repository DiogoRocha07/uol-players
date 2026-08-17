package com.diogorocha.uol_players.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record AvengersResponse(

        @JsonProperty("vingadores")
        List<AvengerCodenameResponse> avengers
) {
}
