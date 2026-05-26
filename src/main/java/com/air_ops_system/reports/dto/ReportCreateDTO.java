package com.air_ops_system.reports.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ReportCreateDTO(

    @NotNull(message = "ID do voo é obrigatório.")
    UUID flightId,

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
