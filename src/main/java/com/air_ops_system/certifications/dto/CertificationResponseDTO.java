package com.air_ops_system.certifications.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record CertificationResponseDTO(

    UUID id,
    String holderType,
    UUID memberId,
    String memberCallsign,
    String fullName,
    String discordId,
    String externalRank,
    String externalUnit,
    String certificateType,
    String issuedByCallsign,
    LocalDateTime issuedAt,
    String notes

) {
}
