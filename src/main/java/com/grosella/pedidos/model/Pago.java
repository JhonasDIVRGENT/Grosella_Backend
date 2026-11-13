package com.grosella.pedidos.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pago")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago")
    private Integer idPago;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_pedido")
    private Pedido pedido;

    @Column(name = "fecha_pago", nullable = false)
    private LocalDateTime fechaPago;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_pago", nullable = false, length = 10)
    private TipoPago tipoPago;

    protected Pago() {
    }

    public Pago(Pedido pedido, BigDecimal monto, TipoPago tipoPago) {
        this.pedido = pedido;
        this.monto = monto;
        this.tipoPago = tipoPago;
        this.fechaPago = LocalDateTime.now();
    }

    public Integer getIdPago() { return idPago; }
    public Pedido getPedido() { return pedido; }
    public LocalDateTime getFechaPago() { return fechaPago; }
    public BigDecimal getMonto() { return monto; }
    public TipoPago getTipoPago() { return tipoPago; }
}
