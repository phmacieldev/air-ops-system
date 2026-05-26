package com.air_ops_system.documents.dto;

import jakarta.validation.constraints.NotBlank;

public record DocumentCreateDTO(

    @NotBlank(message = "Título é obrigatório.")
    String title,

    @NotBlank(message = "URL é obrigatória.")
    String url,

    @NotBlank(message = "Categoria é obrigatória.")
    String category

) {
}
