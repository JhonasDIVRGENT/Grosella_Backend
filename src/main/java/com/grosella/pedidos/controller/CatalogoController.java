package com.grosella.pedidos.controller;

import com.grosella.pedidos.dto.ProductoResponse;
import com.grosella.pedidos.dto.SedeResponse;
import com.grosella.pedidos.service.CatalogoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CatalogoController {

    private final CatalogoService catalogoService;

    public CatalogoController(CatalogoService catalogoService) {
        this.catalogoService = catalogoService;
    }

    @GetMapping("/sedes")
    public List<SedeResponse> sedes() {
        return catalogoService.listarSedes();
    }

    @GetMapping("/productos")
    public List<ProductoResponse> productos() {
        return catalogoService.listarProductosActivos();
    }
}
