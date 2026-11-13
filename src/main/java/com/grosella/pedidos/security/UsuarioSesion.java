package com.grosella.pedidos.security;

import java.time.LocalDateTime;

/** Datos del usuario autenticado que se guardan mientras dure la sesión. */
public record UsuarioSesion(Integer idUsuario, String nombres, String usuario, String rol,
                            Integer idSede, String sede, LocalDateTime expira) {
}
