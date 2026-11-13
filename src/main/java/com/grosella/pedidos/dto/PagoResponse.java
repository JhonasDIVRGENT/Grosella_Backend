package com.grosella.pedidos.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PagoResponse(Integer idPago, LocalDateTime fechaPago, BigDecimal monto, String tipoPago) {
}
