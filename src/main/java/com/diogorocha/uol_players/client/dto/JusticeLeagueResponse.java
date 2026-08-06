package com.diogorocha.uol_players.client.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;

@JacksonXmlRootElement(localName = "liga_da_justica")
public record JusticeLeagueResponse(

        @JacksonXmlElementWrapper(localName = "codinomes")
        @JacksonXmlProperty(localName = "codinome")
        List<String> codenames
) {
}
