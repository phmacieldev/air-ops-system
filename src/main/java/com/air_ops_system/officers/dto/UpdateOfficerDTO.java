package com.air_ops_system.officers.dto;

import com.air_ops_system.officers.domain.OfficerStatus;
import com.air_ops_system.officers.domain.PoliceRank;
import com.air_ops_system.officers.domain.PoliceUnit;
import com.air_ops_system.officers.domain.WeaponClass;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

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
  public record UnitEntry(PoliceUnit unit, UUID unitRankId) {
    @JsonCreator
    public static UnitEntry fromString(String value) {
      return new UnitEntry(PoliceUnit.valueOf(value), null);
    }
    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public static UnitEntry fromObject(
        @JsonProperty("unit") PoliceUnit unit,
        @JsonProperty("unitRankId") UUID unitRankId) {
      return new UnitEntry(unit, unitRankId);
    }
  }
  public record WeaponEntry(WeaponClass weaponClass, String serial) {}
}
