package com.grosella.pedidos.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/** Pedido completo (CU17 Buscar pedido): usado en pagos, entregas y cancelación. */
public record PedidoDetalleResponse(Integer idPedido, String numero, ClienteResponse cliente, Integer idSede, String sede,
                                    String registradoPor, LocalDateTime fechaRegistro, LocalDate fechaRecojo,
                                    LocalTime horaRecojo, BigDecimal total, BigDecimal adelanto, BigDecimal saldo,
                                    String estado, String estadoPago, String siguienteEstado, String observaciones,
                                    List<DetallePedidoResponse> detalles, PersonalizacionResponse personalizacion,
                                    List<PagoResponse> pagos) {
}
