package com.diogorocha.uol_players.client.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

public record JusticeLeagueCodenamesResponse(

        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "codinome")
        List<String> values
) {
}
