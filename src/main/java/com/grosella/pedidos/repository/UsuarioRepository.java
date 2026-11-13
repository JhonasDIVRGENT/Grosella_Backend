package com.grosella.pedidos.repository;

import com.grosella.pedidos.model.Usuario;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    @Query("select u from Usuario u join fetch u.rol join fetch u.sede where lower(u.usuario) = lower(:usuario)")
    Optional<Usuario> buscarParaLogin(@Param("usuario") String usuario);
}
