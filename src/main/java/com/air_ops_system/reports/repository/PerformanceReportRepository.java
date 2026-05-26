package com.air_ops_system.reports.repository;

import com.air_ops_system.flights.domain.FlightLog;
import com.air_ops_system.pilots.domain.Pilot;
import com.air_ops_system.reports.domain.PerformanceReport;
import com.air_ops_system.reports.domain.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PerformanceReportRepository extends JpaRepository<PerformanceReport, UUID> {

  Optional<PerformanceReport> findByFlightLog(FlightLog flightLog);

  List<PerformanceReport> findByPilot(Pilot pilot);

  // Usada para recalcular o score acumulado após cada aprovação
  List<PerformanceReport> findByPilotAndStatus(Pilot pilot, ReportStatus status);
}
