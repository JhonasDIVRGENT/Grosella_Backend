package com.grosella.pedidos.model;

/**
 * Ciclo de seguimiento del pedido:
 * REGISTRADO -> CONFIRMADO -> EN_PREPARACION -> LISTO_PARA_RECOJO -> ENTREGADO
 * Cualquier estado no final puede pasar a CANCELADO.
 */
public enum EstadoPedido {
    REGISTRADO,
    CONFIRMADO,
    EN_PREPARACION,
    LISTO_PARA_RECOJO,
    ENTREGADO,
    CANCELADO;

    public boolean esFinal() {
        return this == ENTREGADO || this == CANCELADO;
    }

    /** Estado al que avanza el pedido en el flujo normal (null si es final). */
    public EstadoPedido siguiente() {
        return switch (this) {
            case REGISTRADO -> CONFIRMADO;
            case CONFIRMADO -> EN_PREPARACION;
            case EN_PREPARACION -> LISTO_PARA_RECOJO;
            case LISTO_PARA_RECOJO -> ENTREGADO;
            default -> null;
        };
    }

    public boolean puedeCambiarA(EstadoPedido destino) {
        if (esFinal() || destino == null) {
            return false;
        }
        return destino == CANCELADO || destino == siguiente();
    }
}
