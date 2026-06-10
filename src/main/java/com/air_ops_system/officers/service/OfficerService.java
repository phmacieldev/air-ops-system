package com.air_ops_system.officers.service;

import com.air_ops_system.officers.domain.*;
import com.air_ops_system.officers.dto.*;
import com.air_ops_system.officers.repository.OfficerRepository;
import com.air_ops_system.pilots.domain.Pilot;
import com.air_ops_system.pilots.repository.PilotRepository;
import com.air_ops_system.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OfficerService {

  private final OfficerRepository officerRepository;
  private final UserRepository userRepository;
  private final PilotRepository pilotRepository;

  @Transactional
  public OfficerResponseDTO create(CreateOfficerDTO dto) {
    Officer officer = Officer.builder()
        .fullName(dto.fullName())
        .callsign(dto.callsign())
        .discordId(dto.discordId())
        .profileImageUrl(dto.profileImageUrl())
        .rank(dto.rank())
        .units(dto.units() != null ? new HashSet<>(dto.units()) : new HashSet<>())
        .employer("Los Santos Police Department")
        .build();

    if (dto.userEmail() != null) {
      userRepository.findByEmail(dto.userEmail()).ifPresent(officer::setUser);
    }

    return toDTO(officerRepository.save(officer));
  }

  public List<OfficerResponseDTO> findAll() {
    return officerRepository.findAll().stream().map(this::toDTO).toList();
  }

  public OfficerResponseDTO findById(UUID id) {
    return toDTO(getOrThrow(id));
  }

  public OfficerResponseDTO findByEmail(String email) {
    return officerRepository.findByUserEmail(email)
        .map(this::toDTO)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Officer not found"));
  }

  @Transactional
  public OfficerResponseDTO patch(UUID id, UpdateOfficerDTO dto) {
    Officer officer = getOrThrow(id);

    if (dto.fullName()       != null) officer.setFullName(dto.fullName());
    if (dto.callsign()       != null) officer.setCallsign(dto.callsign());
    if (dto.profileImageUrl()!= null) officer.setProfileImageUrl(dto.profileImageUrl());
    if (dto.rank()           != null) officer.setRank(dto.rank());
    if (dto.units()          != null) { officer.getUnits().clear(); officer.getUnits().addAll(dto.units()); }
    if (dto.status()         != null) officer.setStatus(dto.status());
    if (dto.phone()          != null) officer.setPhone(dto.phone());
    if (dto.dna()            != null) officer.setDna(dto.dna());
    if (dto.fingerprint()    != null) officer.setFingerprint(dto.fingerprint());
    if (dto.notes()          != null) officer.setNotes(dto.notes());
    if (dto.discordId()      != null) officer.setDiscordId(dto.discordId());

    if (dto.badgeNumber() != null) {
      boolean taken = officerRepository.existsByBadgeNumber(dto.badgeNumber())
          && !dto.badgeNumber().equals(officer.getBadgeNumber());
      if (taken) throw new ResponseStatusException(HttpStatus.CONFLICT, "Badge already taken");
      officer.setBadgeNumber(dto.badgeNumber());
    }

    if (dto.weapons() != null) {
      officer.getWeapons().clear();
      for (UpdateOfficerDTO.WeaponEntry w : dto.weapons()) {
        OfficerWeapon weapon = OfficerWeapon.builder()
            .officer(officer)
            .weaponClass(w.weaponClass())
            .serial(w.serial())
            .build();
        officer.getWeapons().add(weapon);
      }
    }

    return toDTO(officerRepository.save(officer));
  }

  @Transactional
  public void delete(UUID id) {
    officerRepository.delete(getOrThrow(id));
  }

  private Officer getOrThrow(UUID id) {
    return officerRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Officer not found"));
  }

  private OfficerResponseDTO toDTO(Officer o) {
    List<OfficerWeaponDTO> weapons = o.getWeapons().stream()
        .map(w -> new OfficerWeaponDTO(w.getWeaponClass(), w.getSerial()))
        .toList();

    AsdProfileDTO asd = null;
    if (o.getUser() != null) {
      Pilot pilot = pilotRepository.findByUserId(o.getUser().getId()).orElse(null);
      if (pilot != null) {
        asd = new AsdProfileDTO(
            pilot.getId(),
            pilot.getRank() != null ? pilot.getRank().getName() : null,
            pilot.getCallsign(),
            pilot.getFlightMinutes(),
            pilot.getAccumulatedScore(),
            pilot.getStatus() != null ? pilot.getStatus().name() : null
        );
      }
    }

    return new OfficerResponseDTO(
        o.getId(), o.getFullName(), o.getCallsign(), o.getProfileImageUrl(),
        o.getRank(), o.getUnits(), o.getStatus(), o.getBadgeNumber(),
        o.getPhone(), o.getDna(), o.getFingerprint(), o.getNotes(),
        o.getDiscordId(), o.getEmployer(), o.getHiredAt(), weapons, asd
    );
  }
}
