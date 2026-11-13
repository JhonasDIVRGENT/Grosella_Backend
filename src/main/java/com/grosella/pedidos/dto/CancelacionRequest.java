package com.grosella.pedidos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CancelacionRequest(
        @NotBlank(message = "Ingrese el motivo de la cancelación") @Size(max = 500) String motivo) {
}
