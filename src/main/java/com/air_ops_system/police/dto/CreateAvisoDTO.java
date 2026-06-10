package com.air_ops_system.police.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateAvisoDTO(@NotBlank String content, String createdBy) {}
