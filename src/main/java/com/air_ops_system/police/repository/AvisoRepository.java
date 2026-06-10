package com.air_ops_system.police.repository;

import com.air_ops_system.police.domain.Aviso;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface AvisoRepository extends JpaRepository<Aviso, UUID> {}
