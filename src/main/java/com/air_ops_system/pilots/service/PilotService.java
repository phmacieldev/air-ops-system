package com.air_ops_system.pilots.service;

import com.air_ops_system.common.exception.CallsignAlreadyExistsException;
import com.air_ops_system.pilots.domain.Pilot;
import com.air_ops_system.pilots.domain.Rank;
import com.air_ops_system.pilots.dto.CreatePilotDTO;
import com.air_ops_system.pilots.dto.PilotResponseDTO;
import com.air_ops_system.pilots.dto.UpdatePilotDTO;
import com.air_ops_system.pilots.repository.PilotRepository;
import com.air_ops_system.pilots.repository.RankRepository;
import com.air_ops_system.users.domain.User;
import com.air_ops_system.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PilotService {

  private final PilotRepository pilotRepository;
  private final RankRepository rankRepository;
  private final UserRepository userRepository;

  public PilotResponseDTO createPilot(CreatePilotDTO dto) {
    if (pilotRepository.findByCallsign(dto.callsign()).isPresent()) {
      throw new CallsignAlreadyExistsException();
    }

    Rank trainee = rankRepository.findByName("TRAINEE").orElseThrow(() -> new RuntimeException("Rank Trainee não encontrado."));
    User user = userRepository.findByEmail(dto.userEmail()).orElseThrow(() -> new RuntimeException("Email não encontrado."));

    Pilot pilot = Pilot.builder()
        .fullName(dto.fullName())
        .callsign(dto.callsign())
        .discordId(dto.discordId())
        .profileImageUrl(dto.profileImageUrl())
        .rank(trainee)
        .user(user)
        .build();

    return toDTO(pilotRepository.save(pilot));
  }

  public List<PilotResponseDTO> getAllPilots() {
    return pilotRepository.findAll().stream()
        .map(this::toDTO)
        .toList();
  }

  public PilotResponseDTO getPilotById(UUID id) {
    Pilot pilot = pilotRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Piloto não encontrado."));
    return toDTO(pilot);
  }

  public PilotResponseDTO updatePilot(UUID id, UpdatePilotDTO dto) {
    Pilot pilot = pilotRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Piloto não encontrado."));

    if (dto.callsign() != null && !dto.callsign().equals(pilot.getCallsign())) {
      if (pilotRepository.findByCallsign(dto.callsign()).isPresent()) {
        throw new CallsignAlreadyExistsException();
      }
      pilot.setCallsign(dto.callsign());
    }

    if (dto.rankId() != null) {
      Rank rank = rankRepository.findById(dto.rankId())
          .orElseThrow(() -> new RuntimeException("Rank não encontrado."));
      pilot.setRank(rank);
    }

    if (dto.profileImageUrl() != null) {
      pilot.setProfileImageUrl(dto.profileImageUrl());
    }

    if (dto.status() != null) {
      pilot.setStatus(dto.status());
    }

    return toDTO(pilotRepository.save(pilot));
  }

  public void deletePilot(UUID id) {
    Pilot pilot = pilotRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Piloto não encontrado."));
    pilotRepository.delete(pilot);
  }

  private PilotResponseDTO toDTO(Pilot pilot) {
    return new PilotResponseDTO(
        pilot.getId(),
        pilot.getFullName(),
        pilot.getCallsign(),
        pilot.getProfileImageUrl(),
        pilot.getFlightMinutes(),
        pilot.getAccumulatedScore(),
        pilot.getStatus().name(),
        pilot.getDiscordId(),
        pilot.getRank().getName()
    );
  }
}
