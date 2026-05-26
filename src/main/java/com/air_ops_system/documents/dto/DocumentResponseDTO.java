package com.air_ops_system.documents.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record DocumentResponseDTO(
    UUID id,
    String title,
    String url,
    String category,
    LocalDateTime updatedAt
) {
}
