package com.air_ops_system.officers.dto;

import com.air_ops_system.officers.domain.PoliceRank;
import com.air_ops_system.officers.domain.PoliceUnit;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record CreateOfficerDTO(
    @NotBlank String fullName,
    String callsign,
    String discordId,
    String profileImageUrl,
    @NotNull PoliceRank rank,
    List<UnitEntry> units,
    String userEmail
) {
  public record UnitEntry(PoliceUnit unit, UUID unitRankId) {}
}
