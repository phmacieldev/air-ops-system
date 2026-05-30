package com.air_ops_system.certifications.repository;

import com.air_ops_system.certifications.domain.Certification;
import com.air_ops_system.certifications.domain.HolderType;
import com.air_ops_system.pilots.domain.Pilot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CertificationRepository extends JpaRepository<Certification, UUID> {

  List<Certification> findByOrderByIssuedAtDesc();

  List<Certification> findByMember(Pilot member);

  List<Certification> findByHolderTypeOrderByIssuedAtDesc(HolderType holderType);
}
