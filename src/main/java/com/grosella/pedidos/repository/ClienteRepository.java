package com.grosella.pedidos.repository;

import com.grosella.pedidos.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

    Optional<Cliente> findByDni(String dni);

    boolean existsByDni(String dni);

    @Query("""
            select c from Cliente c
            where lower(concat(c.nombres, ' ', c.apellidos)) like lower(concat('%', :texto, '%'))
            order by c.apellidos, c.nombres
            """)
    List<Cliente> buscarPorNombre(@Param("texto") String texto);
}
