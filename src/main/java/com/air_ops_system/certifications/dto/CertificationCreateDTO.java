package com.air_ops_system.certifications.dto;

import com.air_ops_system.certifications.domain.CertificationType;
import com.air_ops_system.certifications.domain.HolderType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CertificationCreateDTO(

    @NotNull(message = "Tipo de detentor é obrigatório.")
    HolderType holderType,

    UUID memberId,

    @NotBlank(message = "Nome completo é obrigatório.")
    String fullName,

    @NotBlank(message = "Discord ID é obrigatório.")
    String discordId,

    String externalRank,

    String externalUnit,

    @NotNull(message = "Tipo de certificado é obrigatório.")
    CertificationType certificateType,

    @NotBlank(message = "Email do emissor é obrigatório.")
    String issuedByEmail,

    String notes

) {
}
