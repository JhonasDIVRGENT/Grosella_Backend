package com.grosella.pedidos.dto;

import com.grosella.pedidos.model.EstadoPedido;
import jakarta.validation.constraints.NotNull;

public record CambioEstadoRequest(@NotNull(message = "Indique el nuevo estado") EstadoPedido estado) {
}
