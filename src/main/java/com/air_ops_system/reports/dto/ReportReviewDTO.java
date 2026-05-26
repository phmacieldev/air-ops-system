package com.air_ops_system.reports.dto;

import jakarta.validation.constraints.NotBlank;

public record ReportReviewDTO(

    @NotBlank(message = "Email do revisor é obrigatório.")
    String reviewerEmail

) {
}
