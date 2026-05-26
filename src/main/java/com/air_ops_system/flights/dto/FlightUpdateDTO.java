package com.air_ops_system.flights.dto;

import com.air_ops_system.flights.domain.Aircraft;
import com.air_ops_system.flights.domain.FlightType;
import java.time.LocalDateTime;

public record FlightUpdateDTO(
    Aircraft aircraft,
    FlightType flightType,
    LocalDateTime startedAt,
    LocalDateTime endAt,
    String notes
) {
}
