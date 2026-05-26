package com.air_ops_system.flights.dto;

import com.air_ops_system.flights.domain.Aircraft;
import com.air_ops_system.flights.domain.FlightType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record FlightCreateDTO(

    @NotBlank
    String pilotEmail,

    @NotNull
    Aircraft aircraft,

    @NotNull
    FlightType flightType,

    @NotNull
    LocalDateTime startedAt,

    @NotNull
    LocalDateTime endAt,

    String notes
) {
}
