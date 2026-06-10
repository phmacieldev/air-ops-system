package com.air_ops_system.officers.dto;

import com.air_ops_system.officers.domain.OfficerStatus;
import com.air_ops_system.officers.domain.PoliceRank;
import com.air_ops_system.officers.domain.PoliceUnit;
import com.air_ops_system.officers.domain.WeaponClass;

import java.util.List;

public record UpdateOfficerDTO(
    String fullName,
    String callsign,
    String profileImageUrl,
    PoliceRank rank,
    List<PoliceUnit> units,
    OfficerStatus status,
    Integer badgeNumber,
    String phone,
    String dna,
    String fingerprint,
    String notes,
    String discordId,
    List<WeaponEntry> weapons
) {
  public record WeaponEntry(WeaponClass weaponClass, String serial) {}
}
