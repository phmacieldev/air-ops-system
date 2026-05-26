package com.air_ops_system.pilots.repository;

import com.air_ops_system.pilots.domain.Pilot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PilotRepository extends JpaRepository<Pilot, UUID> {
  Optional<Pilot> findByUserId(UUID userId);

  Optional<Pilot> findByCallsign(String callsign);

  Optional<Pilot> findByUserEmail(String email);

}
