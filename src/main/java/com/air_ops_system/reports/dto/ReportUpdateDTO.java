package com.air_ops_system.reports.dto;

import jakarta.validation.constraints.Min;

public record ReportUpdateDTO(

    @Min(value = 0, message = "Apreensões não pode ser negativo.")
    int seizures,

    @Min(value = 0, message = "Perseguições não pode ser negativo.")
    int chases,

    @Min(value = 0, message = "Operações não pode ser negativo.")
    int operations,

    @Min(value = 0, message = "Acidentes não pode ser negativo.")
    int accidents

) {
}
