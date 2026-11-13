package com.grosella.pedidos.model;

import jakarta.persistence.*;

@Entity
@Table(name = "personalizacion")
public class Personalizacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_personalizacion")
    private Integer idPersonalizacion;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_pedido", unique = true)
    private Pedido pedido;

    @Column(length = 30)
    private String tamano;

    @Column(length = 120)
    private String decoracion;

    @Column(length = 80)
    private String nombres;

    @Column(length = 80)
    private String toppers;

    @Column(length = 40)
    private String velas;

    @Column(columnDefinition = "text")
    private String observaciones;

    /** Texto corto para listados: "Feliz cumpleaños Sofía, decoración rosada". */
    public String resumen() {
        StringBuilder sb = new StringBuilder();
        for (String parte : new String[]{nombres == null ? null : "“" + nombres + "”", decoracion, toppers, velas}) {
            if (parte != null && !parte.isBlank()) {
                if (!sb.isEmpty()) sb.append(", ");
                sb.append(parte);
            }
        }
        return sb.isEmpty() ? null : sb.toString();
    }

    public Integer getIdPersonalizacion() { return idPersonalizacion; }
    public Pedido getPedido() { return pedido; }
    public void setPedido(Pedido pedido) { this.pedido = pedido; }
    public String getTamano() { return tamano; }
    public void setTamano(String tamano) { this.tamano = tamano; }
    public String getDecoracion() { return decoracion; }
    public void setDecoracion(String decoracion) { this.decoracion = decoracion; }
    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }
    public String getToppers() { return toppers; }
    public void setToppers(String toppers) { this.toppers = toppers; }
    public String getVelas() { return velas; }
    public void setVelas(String velas) { this.velas = velas; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
}
