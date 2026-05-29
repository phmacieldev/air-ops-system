package com.air_ops_system.reports.service;

import com.air_ops_system.discord.service.DiscordWebhookService;
import com.air_ops_system.flights.domain.FlightLog;
import com.air_ops_system.flights.repository.FlightLogRepository;
import com.air_ops_system.pilots.domain.Pilot;
import com.air_ops_system.pilots.domain.Rank;
import com.air_ops_system.pilots.repository.PilotRepository;
import com.air_ops_system.pilots.repository.RankRepository;
import com.air_ops_system.reports.domain.PerformanceReport;
import com.air_ops_system.reports.domain.ReportStatus;
import com.air_ops_system.reports.dto.ReportCreateDTO;
import com.air_ops_system.reports.dto.ReportResponseDTO;
import com.air_ops_system.reports.dto.ReportUpdateDTO;
import com.air_ops_system.reports.repository.PerformanceReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReportService {

  // hierarchyLevel a partir do qual o rank é manual e imune ao auto-rebaixamento
  private static final int IMMUNE_LEVEL = 5;

  private final PerformanceReportRepository reportRepository;
  private final FlightLogRepository flightLogRepository;
  private final PilotRepository pilotRepository;
  private final RankRepository rankRepository;
  private final DiscordWebhookService discordWebhookService;

  public ReportResponseDTO createReport(ReportCreateDTO dto) {
    FlightLog flight = flightLogRepository.findById(dto.flightId())
        .orElseThrow(() -> new RuntimeException("Voo não encontrado."));

    if (reportRepository.findByFlightLog(flight).isPresent()) {
      throw new RuntimeException("Já existe um relatório para esse voo.");
    }

    PerformanceReport report = PerformanceReport.builder()
        .flightLog(flight)
        .pilot(flight.getPilot())
        .seizures(dto.seizures())
        .chases(dto.chases())
        .operations(dto.operations())
        .accidents(dto.accidents())
        .build();

    return toDTO(reportRepository.save(report));
  }

  public ReportResponseDTO reviewReport(UUID id, String reviewerEmail) {
    PerformanceReport report = reportRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Relatório não encontrado."));

    if (report.getStatus() != ReportStatus.PENDING) {
      throw new RuntimeException("Relatório já foi aprovado.");
    }

    Pilot reviewer = pilotRepository.findByUserEmail(reviewerEmail)
        .orElseThrow(() -> new RuntimeException("Revisor não encontrado."));

    // Calcula score: seizures×5 + chases×3 + operations×3 − accidents×5
    int score = report.getSeizures() * 5
        + report.getChases() * 3
        + report.getOperations() * 3
        - report.getAccidents() * 5;

    report.setScore(score);
    report.setStatus(ReportStatus.APPROVED);
    report.setReviewedBy(reviewer);

    PerformanceReport saved = reportRepository.save(report);

    Pilot pilot = report.getPilot();

    // Score acumulado e auto-promoção só se aplicam a ranks operacionais (abaixo de IMMUNE_LEVEL)
    int accumulatedScore = reportRepository
        .findByPilotAndStatus(pilot, ReportStatus.APPROVED)
        .stream()
        .mapToInt(PerformanceReport::getScore)
        .sum();

    pilot.setAccumulatedScore(accumulatedScore);

    if (pilot.getRank().getHierarchyLevel() < IMMUNE_LEVEL) {
      pilot.setRank(determineRankByScore(accumulatedScore));
    }

    pilotRepository.save(pilot);

    // Dispara webhook Discord
    discordWebhookService.sendReportApproved(saved);

    return toDTO(saved);
  }

  public List<ReportResponseDTO> getAllReports() {
    return reportRepository.findAll().stream().map(this::toDTO).toList();
  }

  public List<ReportResponseDTO> getReportsByPilot(UUID pilotId) {
    Pilot pilot = pilotRepository.findById(pilotId)
        .orElseThrow(() -> new RuntimeException("Piloto não encontrado."));
    return reportRepository.findByPilot(pilot).stream().map(this::toDTO).toList();
  }

  public ReportResponseDTO updateReport(UUID id, ReportUpdateDTO dto) {
    PerformanceReport report = reportRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Relatório não encontrado."));

    if (report.getStatus() != ReportStatus.PENDING) {
      throw new RuntimeException("Apenas relatórios pendentes podem ser editados.");
    }

    report.setSeizures(dto.seizures());
    report.setChases(dto.chases());
    report.setOperations(dto.operations());
    report.setAccidents(dto.accidents());

    return toDTO(reportRepository.save(report));
  }

  public void deleteReport(UUID id) {
    PerformanceReport report = reportRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Relatório não encontrado."));

    Pilot pilot = report.getPilot();
    boolean wasApproved = report.getStatus() == ReportStatus.APPROVED;

    reportRepository.delete(report);

    if (wasApproved) {
      int accumulatedScore = reportRepository
          .findByPilotAndStatus(pilot, ReportStatus.APPROVED)
          .stream()
          .mapToInt(PerformanceReport::getScore)
          .sum();

      pilot.setAccumulatedScore(accumulatedScore);

      if (pilot.getRank().getHierarchyLevel() < IMMUNE_LEVEL) {
        pilot.setRank(determineRankByScore(accumulatedScore));
      }

      pilotRepository.save(pilot);
    }
  }

  private Rank determineRankByScore(int score) {
    String name;
    if (score >= 1000)     name = "PILOT_SENIOR";
    else if (score >= 600) name = "PILOT_PLENO";
    else if (score >= 200) name = "PILOT_STANDARD";
    else                   name = "TRAINEE";

    return rankRepository.findByName(name)
        .orElseThrow(() -> new RuntimeException("Rank não encontrado: " + name));
  }

  private ReportResponseDTO toDTO(PerformanceReport r) {
    return new ReportResponseDTO(
        r.getId(),
        r.getPilot().getFullName(),
        r.getPilot().getCallsign(),
        r.getPilot().getRank().getName(),
        r.getPilot().getAccumulatedScore(),
        r.getFlightLog().getFlightId(),
        r.getSeizures(),
        r.getChases(),
        r.getOperations(),
        r.getAccidents(),
        r.getScore(),
        r.getStatus(),
        r.getReviewedBy() != null ? r.getReviewedBy().getCallsign() : null,
        r.getCreatedAt()
    );
  }
}
