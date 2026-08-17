package com.diogorocha.uol_players.client.dto;


import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "liga_da_justica")
public record JusticeLeagueResponse(

        @JacksonXmlProperty(localName = "codinomes")
        JusticeLeagueCodenamesResponse codenames
) {
}
