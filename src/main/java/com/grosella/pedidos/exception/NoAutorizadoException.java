package com.grosella.pedidos.exception;

/** Sesión inválida o credenciales incorrectas (HTTP 401). */
public class NoAutorizadoException extends RuntimeException {
    public NoAutorizadoException(String mensaje) {
        super(mensaje);
    }
}
