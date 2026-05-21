package com.air_ops_system.common.dto;

import java.time.LocalDateTime;

public record ApiErrorDTO(
    int status,
    String error,
    String message,
    String path,
    LocalDateTime timestamp
) {
}
