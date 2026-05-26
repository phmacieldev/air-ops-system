package com.air_ops_system.flights.dto;

import com.air_ops_system.flights.domain.FlightStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FlightReviewDTO(
    @NotBlank
    String approverEmail,

    @NotNull
    FlightStatus status
) {
}
