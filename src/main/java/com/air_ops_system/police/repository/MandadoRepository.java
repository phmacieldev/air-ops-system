package com.air_ops_system.police.repository;

import com.air_ops_system.police.domain.Mandado;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface MandadoRepository extends JpaRepository<Mandado, UUID> {}
