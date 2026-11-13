package com.grosella.pedidos.service;

import com.grosella.pedidos.dto.DtoMapper;
import com.grosella.pedidos.dto.ProductoResponse;
import com.grosella.pedidos.dto.SedeResponse;
import com.grosella.pedidos.repository.ProductoRepository;
import com.grosella.pedidos.repository.SedeRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CatalogoService {

    private final SedeRepository sedeRepository;
    private final ProductoRepository productoRepository;

    public CatalogoService(SedeRepository sedeRepository, ProductoRepository productoRepository) {
        this.sedeRepository = sedeRepository;
        this.productoRepository = productoRepository;
    }

    public List<SedeResponse> listarSedes() {
        return sedeRepository.findAll(Sort.by("idSede")).stream().map(DtoMapper::sede).toList();
    }

    public List<ProductoResponse> listarProductosActivos() {
        return productoRepository.findByActivoTrueOrderByCategoriaAscNombreAsc().stream()
                .map(DtoMapper::producto).toList();
    }
}
