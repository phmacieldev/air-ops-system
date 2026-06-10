package com.air_ops_system.officers.dto;

import com.air_ops_system.officers.domain.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public record OfficerResponseDTO(
    UUID id,
    String fullName,
    String callsign,
    String profileImageUrl,
    PoliceRank rank,
    Set<PoliceUnit> units,
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
    // ASD pilot data (null if not linked)
    AsdProfileDTO asd
) {}
