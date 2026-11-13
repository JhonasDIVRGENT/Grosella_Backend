package com.grosella.pedidos.exception;

/** El perfil del usuario no permite la operación (HTTP 403). */
public class SinPermisoException extends RuntimeException {
    public SinPermisoException(String mensaje) {
        super(mensaje);
    }
}
