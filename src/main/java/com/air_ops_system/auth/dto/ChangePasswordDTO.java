package com.air_ops_system.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordDTO(

    @NotBlank(message = "Senha atual é obrigatória.")
    String currentPassword,

    @NotBlank(message = "Nova senha é obrigatória.")
    @Size(min = 8, message = "A nova senha deve ter pelo menos 8 caracteres.")
    String newPassword

) {
}
