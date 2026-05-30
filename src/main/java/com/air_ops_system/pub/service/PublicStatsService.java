package com.air_ops_system.pub.service;

import com.air_ops_system.pilots.domain.PilotStatus;
import com.air_ops_system.pilots.repository.PilotRepository;
import com.air_ops_system.users.domain.Role;
import com.air_ops_system.pub.dto.PublicStatsDTO;
import com.air_ops_system.reports.domain.ReportStatus;
import com.air_ops_system.reports.repository.PerformanceReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class PublicStatsService {

  private final PilotRepository pilotRepository;
  private final PerformanceReportRepository reportRepository;

  public PublicStatsDTO getStats() {
    var allPilots  = pilotRepository.findAllExcludingRoleSorted(Role.ADM);
    var allReports = reportRepository.findAll();

    int efetivo_total  = allPilots.size();
    int efetivo_ativo  = (int) allPilots.stream()
        .filter(p -> p.getStatus() == PilotStatus.ACTIVE)
        .count();

    var approved = allReports.stream()
        .filter(r -> r.getStatus() == ReportStatus.APPROVED)
        .toList();

    long apreensoes = approved.stream().mapToLong(r -> r.getSeizures()).sum();
    long acidentes  = approved.stream().mapToLong(r -> r.getAccidents()).sum();
    long horas_voo  = allPilots.stream().mapToLong(p -> p.getFlightMinutes()).sum() / 60;

    int total_reports   = allReports.size();
    int taxa_sucesso = total_reports > 0
        ? (int) Math.round((approved.size() * 100.0) / total_reports)
        : 0;

    String mes = Month.from(java.time.LocalDate.now())
        .getDisplayName(TextStyle.SHORT, new Locale("pt", "BR"))
        .toUpperCase();
    String ano  = String.valueOf(java.time.LocalDate.now().getYear());
    String periodo = mes + " " + ano;

    return new PublicStatsDTO(
        periodo,
        efetivo_ativo,
        efetivo_total,
        apreensoes,
        horas_voo,
        acidentes,
        taxa_sucesso,
        Instant.now()
    );
  }
}
