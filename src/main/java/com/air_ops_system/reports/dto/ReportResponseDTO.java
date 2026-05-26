package com.air_ops_system.reports.dto;

import com.air_ops_system.reports.domain.ReportStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReportResponseDTO(

    UUID id,
    String pilotName,
    String pilotCallsign,
    UUID flightId,
    int seizures,
    int chases,
    int operations,
    int accidents,
    int score,
    ReportStatus status,
    String reviewedBy,
    LocalDateTime createdAt

) {
}
