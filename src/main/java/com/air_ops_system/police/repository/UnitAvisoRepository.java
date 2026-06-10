package com.air_ops_system.police.repository;

import com.air_ops_system.police.domain.UnitAviso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UnitAvisoRepository extends JpaRepository<UnitAviso, UUID> {
  List<UnitAviso> findAllByUnitOrderByCreatedAtDesc(String unit);
}
