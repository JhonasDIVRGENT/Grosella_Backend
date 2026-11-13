package com.grosella.pedidos.dto;

import jakarta.validation.constraints.Size;

public record PersonalizacionRequest(
        @Size(max = 30) String tamano,
        @Size(max = 120) String decoracion,
        @Size(max = 80) String nombres,
        @Size(max = 80) String toppers,
        @Size(max = 40) String velas,
        String observaciones) {
}
