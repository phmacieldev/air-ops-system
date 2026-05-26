package com.air_ops_system.flights.dto;

import com.air_ops_system.flights.domain.Aircraft;
import com.air_ops_system.flights.domain.FlightStatus;
import com.air_ops_system.flights.domain.FlightType;
import java.time.LocalDateTime;
import java.util.UUID;

public record FlightResponseDTO(

    UUID id,
    String pilotName,
    String pilotCallsign,
    Aircraft aircraft,
    FlightType flightType,
    FlightStatus flightStatus,
    LocalDateTime startedAt,
    LocalDateTime endAt,
    String approvedBy,
    LocalDateTime createdAt,
    String notes
) {
}
