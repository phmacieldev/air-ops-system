package com.air_ops_system.officers.dto;

import com.air_ops_system.officers.domain.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OfficerResponseDTO(
    UUID id,
    String fullName,
    String callsign,
    String profileImageUrl,
    PoliceRank rank,
    List<OfficerUnitDTO> units,
    // kept for backward-compat (plain unit names)
    List<String> unitNames,
    OfficerStatus status,
    Integer badgeNumber,
    String phone,
    String dna,
    String fingerprint,
    String notes,
    String discordId,
    String employer,
    LocalDateTime hiredAt,
    List<OfficerWeaponDTO> weapons,
    AsdProfileDTO asd
) {}
