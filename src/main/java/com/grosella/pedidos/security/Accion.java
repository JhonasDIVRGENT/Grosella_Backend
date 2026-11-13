package com.grosella.pedidos.security;

import java.util.Set;

/**
 * Operaciones protegidas y perfiles que pueden realizarlas.
 * Las consultas (pedidos por estado, programados, historial) están permitidas a todos.
 */
public enum Accion {
    REGISTRAR_CLIENTE("ADMINISTRADOR", "PERSONAL_ATENCION", "ENCARGADO_PEDIDOS"),
    REGISTRAR_PEDIDO("ADMINISTRADOR", "PERSONAL_ATENCION", "ENCARGADO_PEDIDOS"),
    REGISTRAR_PAGO("ADMINISTRADOR", "PERSONAL_ATENCION", "ENCARGADO_PEDIDOS"),
    REGISTRAR_ENTREGA("ADMINISTRADOR", "PERSONAL_ATENCION", "ENCARGADO_PEDIDOS"),
    CONFIRMAR_PEDIDO("ADMINISTRADOR", "ENCARGADO_PEDIDOS"),
    ACTUALIZAR_PREPARACION("ADMINISTRADOR", "ENCARGADO_PEDIDOS", "PERSONAL_PREPARACION"),
    CANCELAR_PEDIDO("ADMINISTRADOR", "ENCARGADO_PEDIDOS");

    private final Set<String> roles;

    Accion(String... roles) {
        this.roles = Set.of(roles);
    }

    public boolean permitidaPara(String rol) {
        return roles.contains(rol);
    }
}
