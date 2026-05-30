package com.air_ops_system.flights.service;

import com.air_ops_system.flights.domain.FlightLog;
import com.air_ops_system.flights.domain.FlightStatus;
import com.air_ops_system.flights.dto.FlightCreateDTO;
import com.air_ops_system.flights.dto.FlightResponseDTO;
import com.air_ops_system.flights.dto.FlightReviewDTO;
import com.air_ops_system.flights.dto.FlightUpdateDTO;
import com.air_ops_system.flights.repository.FlightLogRepository;
import com.air_ops_system.pilots.domain.Pilot;
import com.air_ops_system.pilots.repository.PilotRepository;
import com.air_ops_system.reports.repository.PerformanceReportRepository;
import com.air_ops_system.users.domain.Role;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FlightService {

  private final FlightLogRepository flightLogRepository;
  private final PilotRepository pilotRepository;
  private final PerformanceReportRepository reportRepository;

  public List<FlightResponseDTO> getAllFlights() {
    return flightLogRepository.findByOrderByCreatedAtDesc().stream()
        .map(this::toDTO)
        .toList();
  }

  public FlightResponseDTO getFlightById(UUID id) {
    FlightLog flight = flightLogRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Voo não encontrado."));
    return toDTO(flight);
  }

  public List<FlightResponseDTO> getFlightsByPilotEmail(String email) {
    Pilot pilot = pilotRepository.findByUserEmail(email)
        .orElseThrow(() -> new RuntimeException("Piloto não encontrado."));
    return flightLogRepository.findByPilot(pilot).stream()
        .map(this::toDTO)
        .toList();
  }

  @Transactional
  public void deleteFlight(UUID id) {
    FlightLog flight = flightLogRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Voo não encontrado."));
    if (flight.getFlightStatus() != FlightStatus.REJECTED) {
      throw new RuntimeException("Apenas protocolos rejeitados podem ser deletados.");
    }
    reportRepository.findByFlightLog(flight).ifPresent(reportRepository::delete);
    flightLogRepository.delete(flight);
  }

  public FlightResponseDTO createFlight(FlightCreateDTO dto) {
    if (dto.startedAt().isAfter(LocalDateTime.now())) {
      throw new RuntimeException("A data e hora de início não podem ser no futuro.");
    }

    Pilot pilot = pilotRepository.findByUserEmail(dto.pilotEmail())
        .orElseThrow(() -> new RuntimeException("Piloto não encontrado."));

    FlightLog flight = FlightLog.builder()
        .pilot(pilot)
        .aircraft(dto.aircraft())
        .flightType(dto.flightType())
        .startAt(dto.startedAt())
        .endAt(dto.endAt())
        .notes(dto.notes())
        .build();

    return toDTO(flightLogRepository.save(flight));
  }

  public FlightResponseDTO updateFlight(UUID id, FlightUpdateDTO dto) {
    FlightLog flight = flightLogRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Voo não encontrado."));

    if (flight.getFlightStatus() != FlightStatus.PENDING) {
      throw new RuntimeException("Voo não pode ser editado.");
    }

    if (dto.startedAt().isAfter(LocalDateTime.now())) {
      throw new RuntimeException("A data e hora de início não podem ser no futuro.");
    }

    flight.setAircraft(dto.aircraft());
    flight.setFlightType(dto.flightType());
    flight.setStartAt(dto.startedAt());
    flight.setEndAt(dto.endAt());
    flight.setNotes(dto.notes());

    return toDTO(flightLogRepository.save(flight));
  }

  public FlightResponseDTO reviewFlight(UUID id, FlightReviewDTO dto) {
    FlightLog flight = flightLogRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Voo não encontrado."));

    if (flight.getFlightStatus() != FlightStatus.PENDING) {
      throw new RuntimeException("Voo não pode ser editado.");
    }

    Pilot approver = pilotRepository.findByUserEmail(dto.approverEmail())
        .orElseThrow(() -> new RuntimeException("Approver não encontrado."));

    if (approver.getUser().getRole() != Role.LEAD && approver.getUser().getRole() != Role.ADM) {
      throw new RuntimeException("Sem permissão para aprovar.");
    }

    if (dto.status() == FlightStatus.APPROVED && flight.getEndAt() != null) {
      long duration = ChronoUnit.MINUTES.between(flight.getStartAt(), flight.getEndAt());
      Pilot pilot = flight.getPilot();
      pilot.setFlightMinutes(pilot.getFlightMinutes() + (int) duration);
      pilotRepository.save(pilot);
    }

    flight.setApprovedBy(approver);
    flight.setFlightStatus(dto.status());

    return toDTO(flightLogRepository.save(flight));
  }

  private FlightResponseDTO toDTO(FlightLog flight) {
    return new FlightResponseDTO(
        flight.getFlightId(),
        flight.getPilot().getFullName(),
        flight.getPilot().getCallsign(),
        flight.getAircraft(),
        flight.getFlightType(),
        flight.getFlightStatus(),
        flight.getStartAt(),
        flight.getEndAt(),
        flight.getApprovedBy() != null ? flight.getApprovedBy().getCallsign() : null,
        flight.getCreatedAt(),
        flight.getNotes()
    );
  }
}
