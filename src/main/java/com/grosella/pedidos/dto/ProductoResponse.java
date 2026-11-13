package com.grosella.pedidos.dto;

import java.math.BigDecimal;

public record ProductoResponse(Integer idProducto, String nombre, String categoria, BigDecimal precio) {
}
