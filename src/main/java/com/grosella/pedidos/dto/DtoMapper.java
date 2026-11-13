package com.grosella.pedidos.dto;

import com.grosella.pedidos.model.*;
import com.grosella.pedidos.model.EstadoPedido;
import com.grosella.pedidos.model.NumeroPedido;

import java.util.stream.Collectors;

/** Convierte las entidades en objetos de respuesta (DTO) para el frontend. */
public final class DtoMapper {

    private DtoMapper() {
    }

    public static SedeResponse sede(Sede s) {
        return new SedeResponse(s.getIdSede(), s.getNombre(), s.getDireccion());
    }

    public static ProductoResponse producto(Producto p) {
        return new ProductoResponse(p.getIdProducto(), p.getNombre(), p.getCategoria(), p.getPrecio());
    }

    public static ClienteResponse cliente(Cliente c, long cantidadPedidos) {
        return new ClienteResponse(c.getIdCliente(), c.getNombres(), c.getApellidos(), c.getNombreCompleto(),
                c.getDni(), c.getTelefono(), c.getFechaRegistro(), cantidadPedidos);
    }

    /** "Torta de chocolate (20 porciones), Queque de naranja x2" */
    public static String productos(Pedido p) {
        return p.getDetalles().stream()
                .map(d -> d.getProducto().getNombre() + (d.getCantidad() > 1 ? " x" + d.getCantidad() : ""))
                .collect(Collectors.joining(", "));
    }

    public static PedidoResumenResponse resumen(Pedido p, boolean incluirDetalle) {
        String personalizacion = null;
        if (incluirDetalle && p.getPersonalizacion() != null) {
            personalizacion = p.getPersonalizacion().resumen();
        }
        return new PedidoResumenResponse(
                p.getIdPedido(), NumeroPedido.formatear(p.getIdPedido()),
                p.getCliente() != null ? p.getCliente().getNombreCompleto() : null,
                p.getFechaRecojo(), p.getHoraRecojo(), p.getSede().getNombre(),
                incluirDetalle ? productos(p) : null, personalizacion,
                p.getTotal(), p.getSaldo(), p.getEstadoPedido().name(), p.getEstadoPago().name());
    }

    public static PedidoDetalleResponse detalle(Pedido p, long pedidosDelCliente) {
        Personalizacion per = p.getPersonalizacion();
        PersonalizacionResponse perDto = per == null ? null : new PersonalizacionResponse(
                per.getTamano(), per.getDecoracion(), per.getNombres(), per.getToppers(),
                per.getVelas(), per.getObservaciones(), per.resumen());
        EstadoPedido siguiente = p.getEstadoPedido().siguiente();
        return new PedidoDetalleResponse(
                p.getIdPedido(), NumeroPedido.formatear(p.getIdPedido()),
                cliente(p.getCliente(), pedidosDelCliente),
                p.getSede().getIdSede(), p.getSede().getNombre(),
                p.getUsuario().getNombres(), p.getFechaRegistro(), p.getFechaRecojo(), p.getHoraRecojo(),
                p.getTotal(), p.getAdelanto(), p.getSaldo(),
                p.getEstadoPedido().name(), p.getEstadoPago().name(),
                siguiente == null ? null : siguiente.name(), p.getObservaciones(),
                p.getDetalles().stream().map(d -> new DetallePedidoResponse(
                        d.getProducto().getIdProducto(), d.getProducto().getNombre(), d.getCantidad(),
                        d.getPrecioUnitario(), d.getSubtotal())).toList(),
                perDto,
                p.getPagos().stream().map(g -> new PagoResponse(
                        g.getIdPago(), g.getFechaPago(), g.getMonto(), g.getTipoPago().name())).toList());
    }
}
