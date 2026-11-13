package com.grosella.pedidos.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "Ingrese su usuario") String usuario,
        @NotBlank(message = "Ingrese su contraseña") String contrasena) {
}
