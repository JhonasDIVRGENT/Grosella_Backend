package com.grosella.pedidos.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/** CU08 Registrar pedido (incluye CU09 cuando "personalizacion" no es nulo). */
public record PedidoRequest(
        @NotNull(message = "Busque y seleccione al cliente") Integer idCliente,
        @NotNull(message = "Seleccione la sede de recojo") Integer idSede,
        @NotNull(message = "Ingrese la fecha de recojo") LocalDate fechaRecojo,
        @NotNull(message = "Ingrese la hora de recojo") LocalTime horaRecojo,
        @NotNull(message = "Ingrese el adelanto") @DecimalMin(value = "0.00", message = "El adelanto no puede ser negativo")
        BigDecimal adelanto,
        String observaciones,
        @NotEmpty(message = "Agregue al menos un producto") List<@Valid DetallePedidoRequest> detalles,
        @Valid PersonalizacionRequest personalizacion) {
}
