package com.diogorocha.uol_players.dto;

import com.diogorocha.uol_players.enums.CodenameGroup;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreatePlayerRequest(

        @NotBlank(message = "Nome é obrigatório")
        String name,

        @NotBlank(message = "Email é obrigatório")
        @Email(message = "E-mail deve possuir um formato válido")
        String email,

        String phone,

        @NotNull(message = "Grupo de codinomes é obrigatório")
        CodenameGroup codenameGroup
) {
}
