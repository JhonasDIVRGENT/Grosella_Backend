package com.grosella.pedidos;

import com.grosella.pedidos.exception.NegocioException;
import com.grosella.pedidos.model.EstadoPago;
import com.grosella.pedidos.model.EstadoPedido;
import com.grosella.pedidos.model.Pedido;
import com.grosella.pedidos.model.Producto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/** Pruebas unitarias de las reglas del pedido (sin base de datos). */
class EstadoPedidoTest {

    private Pedido pedidoDe(String precio, int cantidad) {
        Producto torta = new Producto();
        torta.setNombre("Torta de chocolate");
        torta.setPrecio(new BigDecimal(precio));
        Pedido p = new Pedido();
        p.agregarDetalle(torta, cantidad);
        return p;
    }

    @Test
    void flujoCompletoHastaEntregado() {
        Pedido p = pedidoDe("78.00", 2);                 // total 156.00
        assertEquals(new BigDecimal("156.00"), p.getTotal());
        p.registrarPago(new BigDecimal("78.00"));
        assertEquals(EstadoPago.CON_ADELANTO, p.getEstadoPago());

        p.cambiarEstado(EstadoPedido.CONFIRMADO);
        p.cambiarEstado(EstadoPedido.EN_PREPARACION);
        p.cambiarEstado(EstadoPedido.LISTO_PARA_RECOJO);

        assertThrows(NegocioException.class, p::registrarEntrega, "con saldo no se entrega");
        p.registrarPago(new BigDecimal("78.00"));
        assertEquals(EstadoPago.PAGADO, p.getEstadoPago());
        p.registrarEntrega();
        assertEquals(EstadoPedido.ENTREGADO, p.getEstadoPedido());
    }

    @Test
    void noSePuedeSaltarEstados() {
        Pedido p = pedidoDe("85.00", 1);
        assertThrows(NegocioException.class, () -> p.cambiarEstado(EstadoPedido.LISTO_PARA_RECOJO));
    }

    @Test
    void pagoNoPuedeExcederElSaldo() {
        Pedido p = pedidoDe("85.00", 1);
        assertThrows(NegocioException.class, () -> p.registrarPago(new BigDecimal("90.00")));
        assertThrows(NegocioException.class, () -> p.registrarPago(BigDecimal.ZERO));
    }

    @Test
    void cancelarRequiereMotivoYNoAplicaAEntregados() {
        Pedido p = pedidoDe("95.00", 1);
        assertThrows(NegocioException.class, () -> p.cancelar(" "));
        p.cancelar("El cliente postergó el evento");
        assertEquals(EstadoPedido.CANCELADO, p.getEstadoPedido());
        assertTrue(p.getObservaciones().contains("postergó"));
        assertThrows(NegocioException.class, () -> p.cancelar("otra vez"));
    }
}
