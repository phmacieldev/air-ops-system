package com.air_ops_system.officers.dto;

import com.air_ops_system.officers.domain.RoleCallType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateRoleCallDTO(
    @NotNull RoleCallType type,
    @NotBlank String requestedValue,
    String reason
) {}
