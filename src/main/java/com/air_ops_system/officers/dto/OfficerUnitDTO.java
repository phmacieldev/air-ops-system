package com.air_ops_system.officers.dto;

import java.util.UUID;

public record OfficerUnitDTO(
    String unit,
    UUID unitRankId,
    String unitRankName
) {}
