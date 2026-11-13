package com.grosella.pedidos.dto;

public record LoginResponse(String token, Integer idUsuario, String nombres, String usuario,
                            String rol, Integer idSede, String sede) {
}
