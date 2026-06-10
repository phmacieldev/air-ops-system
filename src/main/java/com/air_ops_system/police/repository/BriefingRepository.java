package com.air_ops_system.police.repository;

import com.air_ops_system.police.domain.Briefing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.UUID;

public interface BriefingRepository extends JpaRepository<Briefing, UUID> {
  List<Briefing> findAllByOrderByCreatedAtDesc(Pageable pageable);
  List<Briefing> findAllByOrderByCreatedAtDesc();
}
