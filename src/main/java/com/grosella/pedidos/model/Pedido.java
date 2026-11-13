package com.grosella.pedidos.model;

import com.grosella.pedidos.exception.NegocioException;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Pedido del cliente. Contiene las reglas del ciclo de estados y del saldo
 * (operaciones del diagrama de clases: registrar, confirmar, cambiarEstado,
 * cancelar, registrarEntrega y calcularSaldo).
 */
@Entity
@Table(name = "pedido")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pedido")
    private Integer idPedido;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_sede")
    private Sede sede;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;

    @Column(name = "fecha_recojo", nullable = false)
    private LocalDate fechaRecojo;

    @Column(name = "hora_recojo", nullable = false)
    private LocalTime horaRecojo;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal saldo = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_pedido", nullable = false, length = 20)
    private EstadoPedido estadoPedido = EstadoPedido.REGISTRADO;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_pago", nullable = false, length = 20)
    private EstadoPago estadoPago = EstadoPago.PENDIENTE;

    @Column(columnDefinition = "text")
    private String observaciones;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("idDetalle ASC")
    private List<DetallePedido> detalles = new ArrayList<>();

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("fechaPago ASC")
    private List<Pago> pagos = new ArrayList<>();

    @OneToOne(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Personalizacion personalizacion;

    // ------------------------------------------------------------------
    // Reglas de negocio
    // ------------------------------------------------------------------

    public void agregarDetalle(Producto producto, int cantidad) {
        DetallePedido detalle = new DetallePedido(this, producto, cantidad);
        detalles.add(detalle);
        total = detalles.stream().map(DetallePedido::getSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        calcularSaldo();
    }

    public void asignarPersonalizacion(Personalizacion p) {
        p.setPedido(this);
        this.personalizacion = p;
    }

    /** Registra un pago (adelanto o saldo) y recalcula el saldo y el estado de pago. */
    public Pago registrarPago(BigDecimal monto) {
        if (estadoPedido.esFinal()) {
            throw new NegocioException("No se puede registrar pagos en un pedido " + estadoPedido.name().toLowerCase() + ".");
        }
        if (monto == null || monto.signum() <= 0) {
            throw new NegocioException("El monto debe ser mayor a cero.");
        }
        if (monto.compareTo(saldo) > 0) {
            throw new NegocioException("El monto (S/ " + monto + ") excede el saldo pendiente (S/ " + saldo + ").");
        }
        TipoPago tipo = pagos.isEmpty() ? TipoPago.ADELANTO : TipoPago.SALDO;
        Pago pago = new Pago(this, monto, tipo);
        pagos.add(pago);
        calcularSaldo();
        return pago;
    }

    /** saldo = total - suma de pagos; actualiza el estado de pago. */
    public BigDecimal calcularSaldo() {
        BigDecimal pagado = pagos.stream().map(Pago::getMonto).reduce(BigDecimal.ZERO, BigDecimal::add);
        saldo = total.subtract(pagado);
        if (saldo.signum() <= 0) {
            saldo = BigDecimal.ZERO;
            estadoPago = EstadoPago.PAGADO;
        } else if (pagado.signum() > 0) {
            estadoPago = EstadoPago.CON_ADELANTO;
        } else {
            estadoPago = EstadoPago.PENDIENTE;
        }
        return saldo;
    }

    /** Avanza el pedido en el flujo: confirmar, en preparación o listo para recojo. */
    public void cambiarEstado(EstadoPedido nuevo) {
        if (nuevo == EstadoPedido.ENTREGADO || nuevo == EstadoPedido.CANCELADO) {
            throw new NegocioException("Use la opción \"Entregas\" o \"Cancelar pedido\" para ese cambio de estado.");
        }
        validarTransicion(nuevo);
        estadoPedido = nuevo;
    }

    public void registrarEntrega() {
        if (estadoPedido != EstadoPedido.LISTO_PARA_RECOJO) {
            throw new NegocioException("El pedido debe estar \"Listo para recojo\" para registrar la entrega.");
        }
        if (saldo.signum() > 0) {
            throw new NegocioException("El pedido tiene un saldo pendiente de S/ " + saldo
                    + ". Registre el pago antes de la entrega.");
        }
        estadoPedido = EstadoPedido.ENTREGADO;
    }

    public void cancelar(String motivo) {
        if (motivo == null || motivo.isBlank()) {
            throw new NegocioException("Ingrese el motivo de la cancelación.");
        }
        validarTransicion(EstadoPedido.CANCELADO);
        String marca = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        String nota = "Cancelado (" + marca + "): " + motivo.trim();
        observaciones = (observaciones == null || observaciones.isBlank()) ? nota : observaciones + "\n" + nota;
        estadoPedido = EstadoPedido.CANCELADO;
    }

    private void validarTransicion(EstadoPedido nuevo) {
        if (estadoPedido.esFinal()) {
            throw new NegocioException("El pedido ya está " + (estadoPedido == EstadoPedido.ENTREGADO ? "entregado" : "cancelado")
                    + " y no puede cambiar de estado.");
        }
        if (!estadoPedido.puedeCambiarA(nuevo)) {
            throw new NegocioException("No se puede pasar de " + estadoPedido + " a " + nuevo + ".");
        }
    }

    public BigDecimal getAdelanto() {
        return pagos.stream().filter(p -> p.getTipoPago() == TipoPago.ADELANTO)
                .map(Pago::getMonto).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @PrePersist
    void antesDeRegistrar() {
        if (fechaRegistro == null) {
            fechaRegistro = LocalDateTime.now();
        }
    }

    // ------------------------------------------------------------------
    // Getters y setters
    // ------------------------------------------------------------------
    public Integer getIdPedido() { return idPedido; }
    public void setIdPedido(Integer idPedido) { this.idPedido = idPedido; }
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
    public Sede getSede() { return sede; }
    public void setSede(Sede sede) { this.sede = sede; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }
    public LocalDate getFechaRecojo() { return fechaRecojo; }
    public void setFechaRecojo(LocalDate fechaRecojo) { this.fechaRecojo = fechaRecojo; }
    public LocalTime getHoraRecojo() { return horaRecojo; }
    public void setHoraRecojo(LocalTime horaRecojo) { this.horaRecojo = horaRecojo; }
    public BigDecimal getTotal() { return total; }
    public BigDecimal getSaldo() { return saldo; }
    public EstadoPedido getEstadoPedido() { return estadoPedido; }
    public EstadoPago getEstadoPago() { return estadoPago; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public List<DetallePedido> getDetalles() { return detalles; }
    public List<Pago> getPagos() { return pagos; }
    public Personalizacion getPersonalizacion() { return personalizacion; }
}
