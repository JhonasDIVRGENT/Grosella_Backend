package com.grosella.pedidos.controller;

import com.grosella.pedidos.dto.ClienteRequest;
import com.grosella.pedidos.dto.ClienteResponse;
import com.grosella.pedidos.dto.HistorialClienteResponse;
import com.grosella.pedidos.security.Accion;
import com.grosella.pedidos.security.SesionInterceptor;
import com.grosella.pedidos.security.SesionService;
import com.grosella.pedidos.security.UsuarioSesion;
import com.grosella.pedidos.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    /** CU16 Buscar cliente: GET /api/clientes?q=45872136  o  ?q=maria */
    @GetMapping
    public List<ClienteResponse> buscar(@RequestParam(name = "q", required = false) String q) {
        return clienteService.buscar(q);
    }

    @GetMapping("/dni/{dni}")
    public ClienteResponse buscarPorDni(@PathVariable String dni) {
        return clienteService.buscarPorDni(dni);
    }

    /** CU06 Registrar cliente */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClienteResponse registrar(@Valid @RequestBody ClienteRequest request,
                                     @RequestAttribute(SesionInterceptor.ATRIBUTO_SESION) UsuarioSesion sesion) {
        SesionService.verificarPermiso(sesion, Accion.REGISTRAR_CLIENTE);
        return clienteService.registrar(request);
    }

    /** CU07 Consultar historial de pedidos del cliente */
    @GetMapping("/{idCliente}/pedidos")
    public HistorialClienteResponse historial(@PathVariable Integer idCliente) {
        return clienteService.obtenerHistorial(idCliente);
    }
}
