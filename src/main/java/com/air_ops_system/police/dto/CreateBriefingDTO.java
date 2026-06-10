package com.air_ops_system.police.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public record CreateBriefingDTO(
    @NotNull LocalDate date,
    @NotBlank String startTime,
    @NotBlank String endTime,
    List<String> announcements,
    List<String> topics,
    List<String> notices,
    String createdBy
) {}
