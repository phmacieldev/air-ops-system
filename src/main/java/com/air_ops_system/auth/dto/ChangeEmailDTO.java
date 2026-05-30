package com.air_ops_system.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ChangeEmailDTO(

    @NotBlank(message = "Senha atual é obrigatória.")
    String currentPassword,

    @Email(message = "E-mail inválido.")
    @NotBlank(message = "Novo e-mail é obrigatório.")
    String newEmail

) {
}
