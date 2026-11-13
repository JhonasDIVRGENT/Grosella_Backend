package com.grosella.pedidos.service;

import com.grosella.pedidos.dto.DetallePedidoRequest;
import com.grosella.pedidos.dto.DtoMapper;
import com.grosella.pedidos.dto.PedidoDetalleResponse;
import com.grosella.pedidos.dto.PedidoRequest;
import com.grosella.pedidos.dto.PedidoResumenResponse;
import com.grosella.pedidos.dto.PersonalizacionRequest;
import com.grosella.pedidos.dto.ResumenEstadoResponse;
import com.grosella.pedidos.exception.NegocioException;
import com.grosella.pedidos.exception.RecursoNoEncontradoException;
import com.grosella.pedidos.model.*;
import com.grosella.pedidos.model.EstadoPedido;
import com.grosella.pedidos.model.NumeroPedido;
import com.grosella.pedidos.repository.*;
import com.grosella.pedidos.security.Accion;
import com.grosella.pedidos.security.SesionService;
import com.grosella.pedidos.security.UsuarioSesion;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final SedeRepository sedeRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;

    public PedidoService(PedidoRepository pedidoRepository, ClienteRepository clienteRepository,
                         SedeRepository sedeRepository, ProductoRepository productoRepository,
                         UsuarioRepository usuarioRepository) {
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.sedeRepository = sedeRepository;
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // ------------------------------------------------------------------
    // CU08 Registrar pedido
    // ------------------------------------------------------------------
    @Transactional
    public PedidoDetalleResponse registrar(PedidoRequest r, UsuarioSesion sesion) {
        SesionService.verificarPermiso(sesion, Accion.REGISTRAR_PEDIDO);

        // FA3: la fecha y hora de recojo no pueden ser anteriores a la actual
        if (LocalDateTime.of(r.fechaRecojo(), r.horaRecojo()).isBefore(LocalDateTime.now())) {
            throw new NegocioException("La fecha y hora de recojo no pueden ser anteriores a la actual.");
        }

        Cliente cliente = clienteRepository.findById(r.idCliente())
                .orElseThrow(() -> new RecursoNoEncontradoException("El cliente no está registrado."));
        Sede sede = sedeRepository.findById(r.idSede())
                .orElseThrow(() -> new RecursoNoEncontradoException("La sede seleccionada no existe."));
        Usuario usuario = usuarioRepository.getReferenceById(sesion.idUsuario());

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setSede(sede);
        pedido.setUsuario(usuario);
        pedido.setFechaRecojo(r.fechaRecojo());
        pedido.setHoraRecojo(r.horaRecojo());
        pedido.setObservaciones(vacioANull(r.observaciones()));

        // Detalle: el precio se toma del catálogo, no del frontend
        for (DetallePedidoRequest d : agruparProductos(r.detalles())) {
            Producto producto = productoRepository.findById(d.idProducto())
                    .filter(Producto::getActivo)
                    .orElseThrow(() -> new NegocioException("El producto seleccionado no está disponible."));
            pedido.agregarDetalle(producto, d.cantidad());
        }

        // CU09 Registrar personalización (opcional)
        PersonalizacionRequest pr = r.personalizacion();
        if (pr != null && tieneDatos(pr)) {
            Personalizacion p = new Personalizacion();
            p.setTamano(vacioANull(pr.tamano()));
            p.setDecoracion(vacioANull(pr.decoracion()));
            p.setNombres(vacioANull(pr.nombres()));
            p.setToppers(vacioANull(pr.toppers()));
            p.setVelas(vacioANull(pr.velas()));
            p.setObservaciones(vacioANull(pr.observaciones()));
            pedido.asignarPersonalizacion(p);
        }

        // Adelanto (normalmente el 50 % del total)
        if (r.adelanto().compareTo(pedido.getTotal()) > 0) {
            throw new NegocioException("El adelanto (S/ " + r.adelanto() + ") no puede ser mayor al total (S/ "
                    + pedido.getTotal() + ").");
        }
        if (r.adelanto().signum() > 0) {
            pedido.registrarPago(r.adelanto());
        }

        Pedido guardado = pedidoRepository.save(pedido);
        return DtoMapper.detalle(guardado, pedidoRepository.countByClienteIdCliente(cliente.getIdCliente()));
    }

    // ------------------------------------------------------------------
    // Consultas
    // ------------------------------------------------------------------
    /** CU17 Buscar pedido por número (P-000128 o 128). */
    @Transactional(readOnly = true)
    public PedidoDetalleResponse buscarPorNumero(String numero) {
        Pedido p = obtener(NumeroPedido.interpretar(numero));
        return DtoMapper.detalle(p, pedidoRepository.countByClienteIdCliente(p.getCliente().getIdCliente()));
    }

    /** CU02 Consultar pedidos por estado. */
    @Transactional(readOnly = true)
    public List<PedidoResumenResponse> listarPorEstado(EstadoPedido estado, Integer idSede) {
        return pedidoRepository.buscarPorEstado(estado, idSede).stream()
                .map(p -> DtoMapper.resumen(p, true)).toList();
    }

    @Transactional(readOnly = true)
    public List<ResumenEstadoResponse> resumenPorEstado(Integer idSede) {
        Map<String, Long> conteo = new HashMap<>();
        for (Object[] fila : pedidoRepository.contarPorEstado(idSede)) {
            conteo.put(((EstadoPedido) fila[0]).name(), (Long) fila[1]);
        }
        return Arrays.stream(EstadoPedido.values())
                .map(e -> new ResumenEstadoResponse(e.name(), conteo.getOrDefault(e.name(), 0L)))
                .toList();
    }

    /** CU15 Consultar pedidos programados. */
    @Transactional(readOnly = true)
    public List<PedidoResumenResponse> listarProgramados(LocalDate fecha, LocalTime desde, LocalTime hasta, Integer idSede) {
        LocalDate f = fecha != null ? fecha : LocalDate.now();           // FA2: sin fecha se usa la actual
        LocalTime d = desde != null ? desde : LocalTime.MIN;
        LocalTime h = hasta != null ? hasta : LocalTime.of(23, 59, 59);
        if (d.isAfter(h)) {
            throw new NegocioException("La hora \"desde\" no puede ser mayor que la hora \"hasta\".");
        }
        return pedidoRepository.buscarProgramados(f, d, h, idSede).stream()
                .map(p -> DtoMapper.resumen(p, true)).toList();
    }

    @Transactional(readOnly = true)
    public List<PedidoResumenResponse> listarProximos(Integer idSede, int limite) {
        var activos = EnumSet.of(EstadoPedido.REGISTRADO, EstadoPedido.CONFIRMADO,
                EstadoPedido.EN_PREPARACION, EstadoPedido.LISTO_PARA_RECOJO);
        return pedidoRepository.buscarProximos(activos, LocalDate.now(), idSede, PageRequest.of(0, limite)).stream()
                .map(p -> DtoMapper.resumen(p, false)).toList();
    }

    // ------------------------------------------------------------------
    // Cambios de estado, pagos, entrega y cancelación
    // ------------------------------------------------------------------
    /** CU12 Confirmar pedido / CU14 Actualizar estado de preparación. */
    @Transactional
    public PedidoDetalleResponse cambiarEstado(Integer idPedido, EstadoPedido nuevoEstado, UsuarioSesion sesion) {
        SesionService.verificarPermiso(sesion, nuevoEstado == EstadoPedido.CONFIRMADO
                ? Accion.CONFIRMAR_PEDIDO : Accion.ACTUALIZAR_PREPARACION);
        Pedido p = obtener(idPedido);
        p.cambiarEstado(nuevoEstado);
        return respuesta(p);
    }

    /** CU10 Registrar pago del pedido. */
    @Transactional
    public PedidoDetalleResponse registrarPago(Integer idPedido, BigDecimal monto, UsuarioSesion sesion) {
        SesionService.verificarPermiso(sesion, Accion.REGISTRAR_PAGO);
        Pedido p = obtener(idPedido);
        p.registrarPago(monto);
        pedidoRepository.saveAndFlush(p);
        return respuesta(p);
    }

    /** CU11 Registrar entrega del pedido. */
    @Transactional
    public PedidoDetalleResponse registrarEntrega(Integer idPedido, UsuarioSesion sesion) {
        SesionService.verificarPermiso(sesion, Accion.REGISTRAR_ENTREGA);
        Pedido p = obtener(idPedido);
        p.registrarEntrega();
        return respuesta(p);
    }

    /** CU13 Cancelar pedido. */
    @Transactional
    public PedidoDetalleResponse cancelar(Integer idPedido, String motivo, UsuarioSesion sesion) {
        SesionService.verificarPermiso(sesion, Accion.CANCELAR_PEDIDO);
        Pedido p = obtener(idPedido);
        p.cancelar(motivo);
        return respuesta(p);
    }

    // ------------------------------------------------------------------
    private Pedido obtener(Integer idPedido) {
        return pedidoRepository.buscarConDetalle(idPedido)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró el pedido " + NumeroPedido.formatear(idPedido) + "."));
    }

    private PedidoDetalleResponse respuesta(Pedido p) {
        return DtoMapper.detalle(p, pedidoRepository.countByClienteIdCliente(p.getCliente().getIdCliente()));
    }

    /** Si el mismo producto se agregó dos veces, se suman las cantidades. */
    private static Collection<DetallePedidoRequest> agruparProductos(List<DetallePedidoRequest> detalles) {
        Map<Integer, Integer> cantidades = new LinkedHashMap<>();
        detalles.forEach(d -> cantidades.merge(d.idProducto(), d.cantidad(), Integer::sum));
        return cantidades.entrySet().stream().map(e -> new DetallePedidoRequest(e.getKey(), e.getValue())).toList();
    }

    private static boolean tieneDatos(PersonalizacionRequest p) {
        return vacioANull(p.tamano()) != null || vacioANull(p.decoracion()) != null || vacioANull(p.nombres()) != null
                || vacioANull(p.toppers()) != null || vacioANull(p.velas()) != null || vacioANull(p.observaciones()) != null;
    }

    private static String vacioANull(String s) {
        return s == null || s.isBlank() ? null : s.trim();
    }
}
