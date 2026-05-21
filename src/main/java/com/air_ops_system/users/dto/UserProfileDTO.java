package com.air_ops_system.users.dto;

import com.air_ops_system.users.domain.Role;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserProfileDTO(
    UUID id,
    String name,
    String email,
    Role role,
    LocalDateTime createdAt
) {
}
