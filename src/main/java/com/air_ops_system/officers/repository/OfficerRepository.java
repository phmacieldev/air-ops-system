package com.air_ops_system.officers.repository;

import com.air_ops_system.officers.domain.Officer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OfficerRepository extends JpaRepository<Officer, UUID> {
  Optional<Officer> findByUserEmail(String email);
  boolean existsByBadgeNumber(Integer badgeNumber);
}
