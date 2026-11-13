package com.grosella.pedidos.model;

import com.grosella.pedidos.exception.NegocioException;

/** Convierte entre el id del pedido y su número visible "P-000128". */
public final class NumeroPedido {

    private NumeroPedido() {
    }

    public static String formatear(Integer idPedido) {
        return String.format("P-%06d", idPedido);
    }

    /** Acepta "P-000128", "p-128", "000128" o "128". */
    public static Integer interpretar(String numero) {
        if (numero == null || numero.isBlank()) {
            throw new NegocioException("Ingrese el número del pedido.");
        }
        String limpio = numero.trim().toUpperCase().replaceFirst("^P-?", "");
        if (!limpio.matches("\\d{1,9}")) {
            throw new NegocioException("El número de pedido \"" + numero + "\" no es válido. Ejemplo: P-000128");
        }
        return Integer.valueOf(limpio);
    }
}
