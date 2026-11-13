package com.grosella.pedidos.exception;

/** El dato ya existe, por ejemplo un DNI registrado (HTTP 409). */
public class RecursoDuplicadoException extends RuntimeException {
    public RecursoDuplicadoException(String mensaje) {
        super(mensaje);
    }
}
