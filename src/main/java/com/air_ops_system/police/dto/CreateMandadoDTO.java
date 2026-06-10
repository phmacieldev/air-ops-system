package com.air_ops_system.police.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateMandadoDTO(@NotBlank String suspectName, String description, String createdBy) {}
