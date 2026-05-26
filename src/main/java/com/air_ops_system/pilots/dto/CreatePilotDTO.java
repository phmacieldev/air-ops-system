package com.air_ops_system.pilots.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreatePilotDTO(

    @NotBlank(message = "Nome é obrigatório.")
    String fullName,

    @NotBlank(message = "Callsign é obrigatório.")
    String callsign,

    @NotBlank(message = "DiscordId é obrigatório.")
    String discordId,

    String profileImageUrl,

    @NotNull
    String userEmail
) {
}
