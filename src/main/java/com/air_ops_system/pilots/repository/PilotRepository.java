package com.air_ops_system.pilots.repository;

import com.air_ops_system.pilots.domain.Pilot;
import com.air_ops_system.users.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PilotRepository extends JpaRepository<Pilot, UUID> {
  Optional<Pilot> findByUserId(UUID userId);

  Optional<Pilot> findByCallsign(String callsign);

  Optional<Pilot> findByUserEmail(String email);

  // Exclui ADM e ordena: score desc → hierarchyLevel desc → callsign asc
  @Query("SELECT p FROM Pilot p WHERE p.user.role <> :role " +
         "ORDER BY p.accumulatedScore DESC, p.rank.hierarchyLevel DESC, p.callsign ASC")
  List<Pilot> findAllExcludingRoleSorted(@Param("role") Role role);

  List<Pilot> findByUserRoleNot(Role role);

}
