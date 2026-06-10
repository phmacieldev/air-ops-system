package com.air_ops_system.officers.dto;

import com.air_ops_system.officers.domain.OfficerStatus;
import com.air_ops_system.officers.domain.PoliceRank;
import com.air_ops_system.officers.domain.PoliceUnit;
import com.air_ops_system.officers.domain.WeaponClass;

import java.util.List;
import java.util.UUID;

public record UpdateOfficerDTO(
    String fullName,
    String callsign,
    String profileImageUrl,
    PoliceRank rank,
    List<UnitEntry> units,
    OfficerStatus status,
    Integer badgeNumber,
    String phone,
    String dna,
    String fingerprint,
    String notes,
    String discordId,
    List<WeaponEntry> weapons
) {
  public record UnitEntry(PoliceUnit unit, UUID unitRankId) {}
  public record WeaponEntry(WeaponClass weaponClass, String serial) {}
}
