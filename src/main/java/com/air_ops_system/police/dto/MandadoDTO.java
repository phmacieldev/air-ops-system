package com.air_ops_system.police.dto;

import com.air_ops_system.police.domain.MandadoStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record MandadoDTO(UUID id, String suspectName, String description, MandadoStatus status, String createdBy, LocalDateTime createdAt) {}
