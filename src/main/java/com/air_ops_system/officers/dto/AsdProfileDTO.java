package com.air_ops_system.officers.dto;

import java.util.UUID;

public record AsdProfileDTO(
    UUID pilotId,
    String asdRank,
    String asdCallsign,
    Integer flightMinutes,
    Integer accumulatedScore,
    String asdStatus
) {}
