package com.grosella.pedidos.repository;

import com.grosella.pedidos.model.EstadoPedido;
import com.grosella.pedidos.model.Pedido;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PedidoRepository extends JpaRepository<Pedido, Integer> {

    /** CU02: pedidos por estado (sede opcional). */
    @EntityGraph(attributePaths = {"cliente", "sede", "detalles", "detalles.producto"})
    @Query("""
            select p from Pedido p
            where p.estadoPedido = :estado
              and (:idSede is null or p.sede.idSede = :idSede)
            order by p.fechaRecojo desc, p.horaRecojo desc
            """)
    List<Pedido> buscarPorEstado(@Param("estado") EstadoPedido estado, @Param("idSede") Integer idSede);

    /** Cantidad de pedidos agrupados por estado: [estado, cantidad]. */
    @Query("""
            select p.estadoPedido, count(p) from Pedido p
            where (:idSede is null or p.sede.idSede = :idSede)
            group by p.estadoPedido
            """)
    List<Object[]> contarPorEstado(@Param("idSede") Integer idSede);

    /** CU15: pedidos programados por fecha, rango de horas y sede. */
    @EntityGraph(attributePaths = {"cliente", "sede", "detalles", "detalles.producto"})
    @Query("""
            select p from Pedido p
            where p.fechaRecojo = :fecha
              and p.horaRecojo between :desde and :hasta
              and (:idSede is null or p.sede.idSede = :idSede)
              and p.estadoPedido <> com.grosella.pedidos.model.EstadoPedido.CANCELADO
            order by p.horaRecojo asc
            """)
    List<Pedido> buscarProgramados(@Param("fecha") LocalDate fecha, @Param("desde") LocalTime desde,
                                   @Param("hasta") LocalTime hasta, @Param("idSede") Integer idSede);

    /** Próximos recojos (pantalla de inicio). */
    @EntityGraph(attributePaths = {"cliente", "sede"})
    @Query("""
            select p from Pedido p
            where p.estadoPedido in :estados
              and p.fechaRecojo >= :desde
              and (:idSede is null or p.sede.idSede = :idSede)
            order by p.fechaRecojo asc, p.horaRecojo asc
            """)
    List<Pedido> buscarProximos(@Param("estados") Collection<EstadoPedido> estados, @Param("desde") LocalDate desde,
                                @Param("idSede") Integer idSede, Pageable pageable);

    /** CU07: historial del cliente. */
    @EntityGraph(attributePaths = {"sede", "detalles", "detalles.producto"})
    List<Pedido> findByClienteIdClienteOrderByFechaRecojoDescIdPedidoDesc(Integer idCliente);

    long countByClienteIdCliente(Integer idCliente);

    /** CU17: buscar pedido con todo su detalle. */
    @EntityGraph(attributePaths = {"cliente", "sede", "usuario", "detalles", "detalles.producto"})
    @Query("select p from Pedido p where p.idPedido = :id")
    Optional<Pedido> buscarConDetalle(@Param("id") Integer id);
}
