package com.air_ops_system.pilots.dto;

import com.air_ops_system.pilots.domain.PilotStatus;

import java.util.UUID;

public record UpdatePilotDTO(
    String callsign,
    UUID rankId,
    String profileImageUrl,
    PilotStatus status
) {
}
