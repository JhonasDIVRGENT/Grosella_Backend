package com.grosella.pedidos.dto;

import java.math.BigDecimal;

public record DetallePedidoResponse(Integer idProducto, String producto, Integer cantidad,
                                    BigDecimal precioUnitario, BigDecimal subtotal) {
}
