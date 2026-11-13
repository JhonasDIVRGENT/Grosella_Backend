package com.grosella.pedidos.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record DetallePedidoRequest(
        @NotNull(message = "Seleccione el producto") Integer idProducto,
        @NotNull @Min(value = 1, message = "La cantidad debe ser mayor a cero") @Max(999) Integer cantidad) {
}
