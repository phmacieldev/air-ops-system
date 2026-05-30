package com.air_ops_system.flights.repository;

import com.air_ops_system.flights.domain.FlightLog;
import com.air_ops_system.flights.domain.FlightStatus;
import com.air_ops_system.pilots.domain.Pilot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FlightLogRepository extends JpaRepository<FlightLog, UUID> {
  List<FlightLog> findByOrderByCreatedAtDesc();

  List<FlightLog> findByPilot(Pilot pilot);

  List<FlightLog> findByFlightStatus(FlightStatus status);

  Optional<FlightLog> findById(UUID id);
}
