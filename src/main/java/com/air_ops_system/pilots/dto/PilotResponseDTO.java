package com.air_ops_system.pilots.dto;

import java.util.UUID;

public record PilotResponseDTO(

    UUID id,
    String fullName,
    String callsign,
    String profileImageUrl,
    Integer flightMinutes,
    Integer accumulatedScore,
    String status,
    String discordId,
    String rankName

) {
}
