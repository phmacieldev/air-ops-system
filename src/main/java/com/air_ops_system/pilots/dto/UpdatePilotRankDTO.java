package com.air_ops_system.pilots.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UpdatePilotRankDTO(

    @NotNull(message = "ID do rank é obrigatório.")
    UUID rankId

) {
}
