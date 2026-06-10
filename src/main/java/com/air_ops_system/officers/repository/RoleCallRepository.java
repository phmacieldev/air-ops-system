package com.air_ops_system.officers.repository;

import com.air_ops_system.officers.domain.RoleCallRequest;
import com.air_ops_system.officers.domain.RoleCallStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RoleCallRepository extends JpaRepository<RoleCallRequest, UUID> {
  List<RoleCallRequest> findByStatus(RoleCallStatus status);
  List<RoleCallRequest> findByOfficerIdOrderByCreatedAtDesc(UUID officerId);
  long countByStatus(RoleCallStatus status);

  @Query("SELECT u, COUNT(r) FROM RoleCallRequest r JOIN r.officer o JOIN o.units u WHERE r.status = 'PENDING' GROUP BY u")
  List<Object[]> countPendingGroupedByUnit();

  List<RoleCallRequest> findTop20ByStatusNotOrderByReviewedAtDesc(RoleCallStatus status);
}
