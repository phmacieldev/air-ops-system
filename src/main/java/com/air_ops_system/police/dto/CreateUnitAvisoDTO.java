package com.air_ops_system.police.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateUnitAvisoDTO(@NotBlank String unit, @NotBlank String content, String createdBy) {}
