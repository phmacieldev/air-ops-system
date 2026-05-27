package com.air_ops_system.pilots.dto;

import java.util.UUID;

public record RankResponseDTO(
    UUID id,
    String name,
    int hierarchyLevel,
    String description
) {}
