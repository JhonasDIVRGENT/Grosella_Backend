package com.grosella.pedidos.dto;

import java.util.List;

public record HistorialClienteResponse(ClienteResponse cliente, List<PedidoResumenResponse> pedidos) {
}
