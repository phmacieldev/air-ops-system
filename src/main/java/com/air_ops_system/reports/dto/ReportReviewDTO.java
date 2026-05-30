package com.air_ops_system.reports.dto;

import com.air_ops_system.reports.domain.ReportStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReportReviewDTO(

    @NotBlank(message = "Email do revisor é obrigatório.")
    String reviewerEmail,

    @NotNull(message = "Status é obrigatório.")
    ReportStatus status

) {
}
