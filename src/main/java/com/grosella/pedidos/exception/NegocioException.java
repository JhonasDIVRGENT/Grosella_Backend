package com.grosella.pedidos.exception;

/** Error por incumplir una regla de negocio (HTTP 400). */
public class NegocioException extends RuntimeException {
    public NegocioException(String mensaje) {
        super(mensaje);
    }
}
