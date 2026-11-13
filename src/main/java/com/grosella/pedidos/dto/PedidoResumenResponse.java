package com.grosella.pedidos.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/** Fila de los listados: pedidos por estado, programados, historial e inicio. */
public record PedidoResumenResponse(Integer idPedido, String numero, String cliente, LocalDate fechaRecojo,
                                    LocalTime horaRecojo, String sede, String productos, String personalizacion,
                                    BigDecimal total, BigDecimal saldo, String estado, String estadoPago) {
}
