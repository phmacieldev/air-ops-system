package com.air_ops_system.police.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record AvisoDTO(UUID id, String content, String createdBy, LocalDateTime createdAt) {}
