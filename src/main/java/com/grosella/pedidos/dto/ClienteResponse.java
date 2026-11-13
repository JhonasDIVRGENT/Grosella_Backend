package com.grosella.pedidos.dto;

import java.time.LocalDate;

public record ClienteResponse(Integer idCliente, String nombres, String apellidos, String nombreCompleto,
                              String dni, String telefono, LocalDate fechaRegistro, long cantidadPedidos) {
}
