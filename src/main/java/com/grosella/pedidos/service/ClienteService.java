package com.grosella.pedidos.service;

import com.grosella.pedidos.dto.ClienteRequest;
import com.grosella.pedidos.dto.ClienteResponse;
import com.grosella.pedidos.dto.DtoMapper;
import com.grosella.pedidos.dto.HistorialClienteResponse;
import com.grosella.pedidos.exception.RecursoDuplicadoException;
import com.grosella.pedidos.exception.RecursoNoEncontradoException;
import com.grosella.pedidos.model.Cliente;
import com.grosella.pedidos.repository.ClienteRepository;
import com.grosella.pedidos.repository.PedidoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final PedidoRepository pedidoRepository;

    public ClienteService(ClienteRepository clienteRepository, PedidoRepository pedidoRepository) {
        this.clienteRepository = clienteRepository;
        this.pedidoRepository = pedidoRepository;
    }

    /** CU06 Registrar cliente. */
    @Transactional
    public ClienteResponse registrar(ClienteRequest r) {
        clienteRepository.findByDni(r.dni()).ifPresent(existente -> {
            throw new RecursoDuplicadoException("El DNI " + r.dni() + " ya está registrado a nombre de "
                    + existente.getNombreCompleto() + ".");
        });
        Cliente c = new Cliente();
        c.setNombres(r.nombres().trim());
        c.setApellidos(r.apellidos().trim());
        c.setDni(r.dni());
        c.setTelefono(r.telefono().trim());
        return DtoMapper.cliente(clienteRepository.save(c), 0);
    }

    /** CU16 Buscar cliente por DNI o nombre. */
    @Transactional(readOnly = true)
    public List<ClienteResponse> buscar(String texto) {
        String t = texto == null ? "" : texto.trim();
        if (t.length() < 2) {
            return List.of();
        }
        List<Cliente> encontrados = t.matches("\\d{8}")
                ? clienteRepository.findByDni(t).map(List::of).orElse(List.of())
                : clienteRepository.buscarPorNombre(t);
        return encontrados.stream()
                .map(c -> DtoMapper.cliente(c, pedidoRepository.countByClienteIdCliente(c.getIdCliente())))
                .toList();
    }

    @Transactional(readOnly = true)
    public ClienteResponse buscarPorDni(String dni) {
        Cliente c = clienteRepository.findByDni(dni == null ? "" : dni.trim())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un cliente con DNI " + dni + "."));
        return DtoMapper.cliente(c, pedidoRepository.countByClienteIdCliente(c.getIdCliente()));
    }

    /** CU07 Consultar historial de pedidos del cliente. */
    @Transactional(readOnly = true)
    public HistorialClienteResponse obtenerHistorial(Integer idCliente) {
        Cliente c = clienteRepository.findById(idCliente)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado."));
        var pedidos = pedidoRepository.findByClienteIdClienteOrderByFechaRecojoDescIdPedidoDesc(idCliente).stream()
                .map(p -> DtoMapper.resumen(p, true))
                .toList();
        return new HistorialClienteResponse(DtoMapper.cliente(c, pedidos.size()), pedidos);
    }
}
