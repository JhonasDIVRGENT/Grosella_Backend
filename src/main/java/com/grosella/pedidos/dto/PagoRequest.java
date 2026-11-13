package com.grosella.pedidos.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PagoRequest(
        @NotNull(message = "Ingrese el monto")
        @DecimalMin(value = "0.01", message = "El monto debe ser mayor a cero") BigDecimal monto) {
}
