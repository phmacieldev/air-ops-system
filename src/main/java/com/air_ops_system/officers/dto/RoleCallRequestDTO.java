package com.air_ops_system.officers.dto;

import com.air_ops_system.officers.domain.RoleCallStatus;
import com.air_ops_system.officers.domain.RoleCallType;

import java.time.LocalDateTime;
import java.util.UUID;

public record RoleCallRequestDTO(
    UUID id,
    UUID officerId,
    String officerName,
    String officerCallsign,
    String officerProfileImageUrl,
    RoleCallType type,
    String requestedValue,
    String currentValue,
    RoleCallStatus status,
    String reason,
    String reviewNote,
    String reviewedBy,
    LocalDateTime createdAt,
    LocalDateTime reviewedAt
) {}
