package com.grosella.pedidos.repository;

import com.grosella.pedidos.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    List<Producto> findByActivoTrueOrderByCategoriaAscNombreAsc();
}
