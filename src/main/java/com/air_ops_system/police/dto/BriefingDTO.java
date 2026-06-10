package com.air_ops_system.police.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record BriefingDTO(
    UUID id,
    LocalDate date,
    String startTime,
    String endTime,
    List<String> announcements,
    List<String> topics,
    List<String> notices,
    String createdBy,
    LocalDateTime createdAt
) {}
