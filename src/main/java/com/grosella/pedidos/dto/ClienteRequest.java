package com.grosella.pedidos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ClienteRequest(
        @NotBlank(message = "Ingrese los nombres") @Size(max = 80) String nombres,
        @NotBlank(message = "Ingrese los apellidos") @Size(max = 80) String apellidos,
        @NotBlank(message = "Ingrese el DNI")
        @Pattern(regexp = "\\d{8}", message = "El DNI debe tener 8 dígitos") String dni,
        @NotBlank(message = "Ingrese el teléfono")
        @Pattern(regexp = "[0-9 +]{6,15}", message = "El teléfono no es válido") String telefono) {
}
