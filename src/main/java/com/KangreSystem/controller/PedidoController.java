package com.KangreSystem.controller;

import java.io.IOException;
import java.security.Principal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.document.AbstractXlsxView;
import com.KangreSystem.models.entity.CategoriaProducto;
import com.KangreSystem.models.entity.DetallePedido;
import com.KangreSystem.models.entity.Empleado;
import com.KangreSystem.models.entity.Mesa;
import com.KangreSystem.models.entity.Pedido;
import com.KangreSystem.models.entity.PedidoMesa;
import com.KangreSystem.models.entity.Producto;
import com.KangreSystem.models.entity.User;
import com.KangreSystem.models.service.ICategoriaProductoServ;
import com.KangreSystem.models.service.IDetallePedidoServ;
import com.KangreSystem.models.service.IEmpleadoService;
import com.KangreSystem.models.service.IInventarioService;
import com.KangreSystem.models.service.IMesaService;
import com.KangreSystem.models.service.IPedidoService;
import com.KangreSystem.models.service.IProductoService;
import com.KangreSystem.models.service.IUserService;
import com.KangreSystem.util.PedidoPDFExporter;
import com.lowagie.text.DocumentException;

@Controller
@RequestMapping("/pedido")
@Component("/Views/SI/Venta/Pedido/pedidos.xlsx")
public class PedidoController extends AbstractXlsxView {
	
	private List<Pedido> pedidos;
	
	private List<Pedido> pedidosX;
	
	private List<DetallePedido> detallesAgregados = new ArrayList<>();
	
	private List<DetallePedido> detallesAux;
	
	private List<Producto> productos;
	
	private List<PedidoMesa> mesasAgregadas = new ArrayList<>();
	
	private List<Mesa> mesas = new ArrayList<>();
	
	private CategoriaProducto categoria;
	
	@SuppressWarnings("unused")
	private Principal principal;
	
	private Empleado empleado = new Empleado();

	private Pedido pedido = new Pedido();
	
	private Long iva;
	
	private boolean mode = false;
	
	@Autowired
	private IPedidoService pedidoService;
	
	@Autowired
	private IUserService userService;
	
	@Autowired
	private IMesaService mesaService;
	
	@Autowired
	private IEmpleadoService empleadoService;
	
	@Autowired
	private IProductoService productoService;
	
	@Autowired
	private ICategoriaProductoServ categoriaServ;
	
	@Autowired
	private IDetallePedidoServ detalleService;
	
	@Autowired
	private IInventarioService inventarioService;
	
	@Secured("ROLE_ADMIN")
	@GetMapping("/todos/")
	public String listar(Model model) {
		this.pedidos = pedidoService.listar();
		model.addAttribute("pedidos", this.pedidos);
		model.addAttribute("orden", new Pedido());
		return "/Views/SI/Venta/Pedido/pedidos";
	}
	
	@Secured("ROLE_ADMIN")
	@SuppressWarnings("deprecation")
	@PostMapping("/filtrar")
	public String filtrar(@ModelAttribute Pedido orden, RedirectAttributes attr, Model model) {
		
		if (!pedidoService.filterIsValid(orden)) {
			this.pedidosX = null;
			attr.addFlashAttribute("warning", "El criterio de busqueda es invalido, recuerde seleccionar solo un criterio de busqueda!");
			return "redirect:/pedido/todos/";
		}
		
		if (orden.getNumeroOrden().isEmpty() && orden.getFecha() == null && orden.getEstado().isEmpty()) {
			this.pedidosX = null;
			attr.addFlashAttribute("warning", "No se encontraron criterios de busqueda!");
			return "redirect:/pedido/todos/";
		}
		
		if (!orden.getNumeroOrden().isEmpty() && orden.getFecha() == null && orden.getEstado().isEmpty()) {
			this.pedidos = new ArrayList<>();
			this.pedidos.add(pedidoService.buscarPorNumeroOrden(orden.getNumeroOrden()));
		}
		
		if (orden.getNumeroOrden().isEmpty() && orden.getFecha() != null && orden.getEstado().isEmpty()) {
			
			orden.getFecha().setHours(0);
			orden.getFecha().setMinutes(0);
			orden.getFecha().setSeconds(0);
			
			this.pedidos = pedidoService.buscarPorFecha(orden.getFecha());
		}
		
		if (orden.getNumeroOrden().isEmpty() && orden.getFecha() != null && !orden.getEstado().isEmpty()) {
			
			orden.getFecha().setHours(0);
			orden.getFecha().setMinutes(0);
			orden.getFecha().setSeconds(0);
			
			this.pedidos = pedidoService.buscarPorEstadoFecha(orden.getEstado(), orden.getFecha());
		}
		
		if (orden.getNumeroOrden().isEmpty() && orden.getFecha() == null && !orden.getEstado().isEmpty()) {	
			this.pedidos = pedidoService.buscarPorEstado(orden.getEstado());
		}
		
		if (this.pedidos.isEmpty()) {
			this.pedidosX = null;
			attr.addFlashAttribute("warning", "No se encontraron resultados en la busqueda!");
			return "redirect:/pedido/todos/";
		}
		
		this.pedidosX = this.pedidos;
		model.addAttribute("pedidos", this.pedidos);
		model.addAttribute("orden", new Pedido());
		return "/Views/SI/Venta/Pedido/pedidos";
	}
	
	@Secured("ROLE_ADMIN")
	@GetMapping("/limpiar-filtro")
	public String limpiarFiltro(){
		this.pedidosX = null;
		return "redirect:/pedido/todos/";
	}
	
	@Secured({"ROLE_ADMIN", "ROLE_COCINERO", "ROLE_CAJERO"})
	@GetMapping("/detalles/{id}")
	public String detalles(@PathVariable("id") Long idPedido, Model model) {
		Pedido pedidoAUX = pedidoService.buscarPorId(idPedido);
		model.addAttribute("pedido", pedidoAUX);
		model.addAttribute("detalles", detalleService.buscarDetallesPorPedido(pedidoAUX));
		return "/Views/SI/Venta/Pedido/detallesPedido";
	}
	
	@Secured("ROLE_ADMIN")
	@GetMapping("/cancelar/{id}")
	public String delete(@PathVariable("id") Long idPedido, RedirectAttributes attr) {
		Pedido p = null;
		
		if (idPedido > 0) {
			p = pedidoService.buscarPorId(idPedido);
			
			if (p == null) {
				attr.addFlashAttribute("error", "El ID de la orden que desea cancelar no existe!");
				return "redirect:/pedido/todos/";
			}
		} else {
			attr.addFlashAttribute("error", "El ID de la orden que desea cancelar no existe!");
			return "redirect:/pedido/todos/";
		}
		
		if (p.getEstado().equals("CANCELADO")) {
			attr.addFlashAttribute("error", "La orden que desea cancelar ya se encuentra cancelada!");
			return "redirect:/pedido/todos/";
		} else if (p.getEstado().equals("SOLICITADO") || p.getEstado().equals("PREPARADO")) {
			attr.addFlashAttribute("error", "La orden que desea cancelar no se encuentra disponible, recuerda cobrar la orden y luego volver a intentar.");
			return "redirect:/pedido/todos/";
		}
		
		if (inventarioService.cancelarVenta(p)) {
			inventarioService.cancelarVenta(p);
			
			attr.addFlashAttribute("warning", "Pedido eliminado correctamente!");
			return "redirect:/pedido/todos/";
		}
		
		attr.addFlashAttribute("error", "Ocurrio algun problema al cancelar la orden!");
		return "redirect:/pedido/todos/";
	}
	
	@Secured("ROLE_CAJERO")
	@GetMapping("/preparar-nuevo")
	public String preparar(RedirectAttributes attr) {
		Pedido pedidoAux;
		this.productos = productoService.findAllByViaProd();
		this.iva = (long) 0;
		this.mode = true;
		this.pedido = new Pedido();
		this.mesasAgregadas = new ArrayList<>();
		String numeroPedido;
		
		if (pedidoService.listar().isEmpty()) {
			numeroPedido = "1";
			this.pedido.setNumeroOrden(numeroPedido);
			this.pedido.setTotal((long)0);
			this.pedido.setSubtotal((long)0);
			attr.addFlashAttribute("pedido", this.pedido);
			attr.addFlashAttribute("mode", this.mode);
			return "redirect:/pedido/";
		}
		
		pedidoAux = pedidoService.listar().get(pedidoService.listar().size() - 1);
		Integer ultimoPedido  = Integer.parseInt(pedidoAux.getNumeroOrden());
		ultimoPedido++;
		numeroPedido = Integer.toString(ultimoPedido);
		this.pedido.setNumeroOrden(numeroPedido);
		this.pedido.setSubtotal((long)0);
		this.pedido.setTotal((long)0);
		attr.addFlashAttribute("pedido", this.pedido);
		attr.addFlashAttribute("mode", this.mode);
		attr.addFlashAttribute("iva", this.iva);
		attr.addFlashAttribute("productos", productoService.findAllByViaProd());
		return "redirect:/pedido/";
	}
	
	@Secured("ROLE_CAJERO")
	@GetMapping("/mesa")
	public String agregarMesa(Model model) {
		model.addAttribute("titulo", "Asignar mesa");
		
		if (this.mesasAgregadas.isEmpty()) {
			this.mesas = mesaService.mesasDisponibles();
		} 
		
		long id = (long) 1;
		for (Mesa mesa : mesas) {
			mesa.setIdMesa(id);
			id++;
		}
		
		long id2 = (long) 1;
		for (PedidoMesa pedidoMesa: mesasAgregadas) {
			pedidoMesa.setIdPedidoMesa(id2);
			id2++;
		}
		model.addAttribute("mesas", this.mesas);
		model.addAttribute("agregadas", this.mesasAgregadas);
		return "/Views/SI/Venta/Mesa/asignarMesa";
	}
	
	@Secured("ROLE_CAJERO")
	@GetMapping("/agregar-mesa/{item}/{numeroMesa}")
	public String agregarMesa(@PathVariable("item") int item, @PathVariable("numeroMesa") String numeroMesa) {
		Mesa mesa = mesaService.buscarPorNumeroMesa(numeroMesa);
		PedidoMesa pedidoMesa = new PedidoMesa();
		
		pedidoMesa.setMesa(mesaService.buscarPorId(mesa.getIdMesa()));
		pedidoMesa.getMesa().setEstado("OCUPADA");
		
		this.mesasAgregadas.add(pedidoMesa);
		this.mesas.remove(item);
		return "redirect:/pedido/mesa";
	}
	
	@Secured("ROLE_CAJERO")
	@GetMapping("/quitar-mesa/{item}/{numeroMesa}")
	public String quitarMesa(@PathVariable("item") int item, @PathVariable("numeroMesa") String numeroMesa) {
		Mesa mesa = mesaService.buscarPorNumeroMesa(numeroMesa);
		this.mesasAgregadas.remove(item);
		this.mesas.add(mesa);
		return "redirect:/pedido/mesa";
	}
	
	@Secured("ROLE_CAJERO")
	@GetMapping("/cancelar")
	public String cancelar() {
		this.mode = false;
		return "redirect:/pedido/";
	}
	
	@Secured("ROLE_CAJERO")
	@GetMapping("/")
	public String pedidoIndex(Model model, Principal principal) {
		this.principal = principal;
		User user = userService.buscarPorNumeroDoc(principal.getName());
		this.empleado = empleadoService.buscarPorUser(user);
		this.pedido.setEmpleado(this.empleado);
	
		if (!this.mode) {
			Pedido pedidoAux = new Pedido();
			pedidoAux.setNumeroOrden("0");
			pedidoAux.setTotal((long) 0);
			pedidoAux.setSubtotal((long)0);
			pedidoAux.setEmpleado(this.empleado);
			this.detallesAgregados = new ArrayList<>();
			this.iva = (long) 0;
			model.addAttribute("detalle", new DetallePedido());
			model.addAttribute("agregados", this.detallesAgregados);
			model.addAttribute("pedido", pedidoAux);
			model.addAttribute("mode", this.mode);
			model.addAttribute("iva", this.iva);
			return "/Views/SI/Venta/Pedido/pedidoIndex";
		}
		
		long i = 1;
		
		for (DetallePedido detalle : detallesAgregados) {
			detalle.setIdDetalle(i);
			i++;
		}
		model.addAttribute("agregados", this.detallesAgregados);
		model.addAttribute("detalle", new DetallePedido());
		model.addAttribute("mode", this.mode);
		model.addAttribute("pedido", this.pedido);
		model.addAttribute("iva", this.iva);
		model.addAttribute("productos", this.productos);
		return "/Views/SI/Venta/Pedido/pedidoIndex";
	}
	
	@Secured("ROLE_CAJERO")
	@GetMapping("/producto/{id}")
	public ResponseEntity<Producto> details(@PathVariable("id") Long idProducto) {
		
		try {
			
			return new ResponseEntity<Producto>(productoService.buscarPorId(idProducto), HttpStatus.OK);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return new ResponseEntity<Producto>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@Secured("ROLE_CAJERO")
	@PostMapping("/agregar-item-list")
	public String agregarItemList(@ModelAttribute DetallePedido detalle, @Param("idProductoMC") Long idProductoMC,
			RedirectAttributes attr, Model model) {
		
		if (detalle.getCantidad() <= 0) {
			attr.addFlashAttribute("error", "La cantidad ingresada es incorrecta!");
			return "redirect:/pedido/";
		}
		
		Producto producto = productoService.buscarPorId(idProductoMC);
		detalle.setPedido(this.pedido);
		detalle.setProducto(producto);
		detalle.setSubtotal(detalle.getProducto().getPrecio() * detalle.getCantidad());
		
		if (itemIsAdded(detalle) != null) {
			DetallePedido detalleAux = itemIsAdded(detalle);
			Long indice = detalleAux.getIdDetalle();
			detalleAux.setCantidad(detalle.getCantidad() + detalleAux.getCantidad());
			detalleAux.setSubtotal(detalle.getProducto().getPrecio() * detalleAux.getCantidad());
			this.detallesAgregados.add(detalleAux);
			this.detallesAgregados.remove(Integer.parseInt(indice.toString()) - 1);
			
			this.pedido.setSubtotal(pedidoService.calcularSubtotalPedido(detallesAgregados));
			this.pedido.setKangrepuntos(pedidoService.calcularKangrepuntos(this.pedido.getSubtotal()));
			this.pedido.setTotal(pedidoService.calcularTotalPedido(this.pedido.getSubtotal()));
			this.iva = pedidoService.calcularIva(this.pedido.getSubtotal());
			return "redirect:/pedido/";
			
		}
		this.detallesAgregados.add(detalle);
		
		this.pedido.setSubtotal(pedidoService.calcularSubtotalPedido(detallesAgregados));
		this.pedido.setKangrepuntos(pedidoService.calcularKangrepuntos(this.pedido.getSubtotal()));
		this.pedido.setTotal(pedidoService.calcularTotalPedido(this.pedido.getSubtotal()));
		this.iva = pedidoService.calcularIva(this.pedido.getSubtotal());
		return "redirect:/pedido/";
	}
	
	@Secured("ROLE_CAJERO")
	@PostMapping("/agregar-item-cod")
	public String agregarItemCod(@RequestParam("idProductoCod") Long idProductoCod, Model model, RedirectAttributes attr) {
		Producto producto = productoService.buscarPorId(idProductoCod);
		
		if (producto == null) {
			attr.addFlashAttribute("warning", "No se encontro ningun resultado!");
			return "redirect:/pedido/";
		}
		
		this.productos = new ArrayList<>();
		this.productos.add(producto);
		return "redirect:/pedido/";
	}
	
	@Secured("ROLE_CAJERO")
	@GetMapping("/limpiar-filtro-categorias")
	public String limpiarFiltroCategorias() {
		this.productos = productoService.findAllByViaProd();
		return "redirect:/pedido/";
	}
	
	@Secured("ROLE_CAJERO")
	@GetMapping("/quitar-item/{id}")
	public String quitarItem(@PathVariable("id") int idDetalle, RedirectAttributes attr) {
		this.detallesAgregados.remove(idDetalle - 1);
		this.pedido.setSubtotal(pedidoService.calcularSubtotalPedido(detallesAgregados));
		this.pedido.setKangrepuntos(pedidoService.calcularKangrepuntos(this.pedido.getSubtotal()));
		this.pedido.setTotal(pedidoService.calcularTotalPedido(this.pedido.getSubtotal()));
		this.iva = pedidoService.calcularIva(this.pedido.getSubtotal());
		attr.addFlashAttribute("iva", this.iva);
		attr.addFlashAttribute("productos", productoService.findAllByViaProd());
		return "redirect:/pedido/";
	}
	
	@Secured("ROLE_CAJERO")
	@GetMapping("/vaciar-lista")
	public String vaciarLista() {
		this.detallesAgregados = new ArrayList<>();
		this.pedido.setSubtotal((long)0);
		this.pedido.setTotal((long)0);
		this.pedido.setKangrepuntos(0);
		this.iva = (long) 0;
		return "redirect:/pedido/";
	}
	
	@Secured("ROLE_CAJERO")
	@GetMapping("/tomar-orden")
	public String tomarOrden(RedirectAttributes attr) {
		if (this.detallesAgregados.isEmpty()) {
			attr.addFlashAttribute("error", "No se ha agregado ningun producto a la orden!");
			return "redirect:/pedido/";
		}
		
		pedidoService.tomarOrden(this.pedido, this.detallesAgregados, this.mesasAgregadas);
		this.mode = false;
		return "redirect:/pedido/orden-tomada";
	}
	
	@Secured("ROLE_CAJERO")
	@GetMapping("/orden-tomada")
	public String ordenTomada(Model model) {
		if (this.pedido == null) {
			this.mode = false;
			return "redirect:/pedido/";
		}
		model.addAttribute("pedido", this.pedido);
		model.addAttribute("detalles", this.detallesAgregados);
		return "/Views/SI/Venta/Pedido/ordenTomada";
	}
	
	@Secured("ROLE_CAJERO")
	@GetMapping("/categoria/entradas")
	public String categoriaEntrada(Model model, RedirectAttributes attr) {
		this.categoria = categoriaServ.buscarPorCategoria("ENTRADAS");
		this.productos = new ArrayList<>();
		this.productos = productoService.buscarPorCategoria(this.categoria);
		
		if (this.productos.isEmpty()) {
			this.productos = productoService.findAllByViaProd();
			attr.addFlashAttribute("warning", "No se encontro ningun resultado!");
			return "redirect:/pedido/";
		}
		
		return "redirect:/pedido/";
	}
	
	@Secured("ROLE_CAJERO")
	@GetMapping("/categoria/ensaladas")
	public String categoriaEnsalada(RedirectAttributes attr) {
		this.categoria = categoriaServ.buscarPorCategoria("ENSALADAS");
		this.productos = new ArrayList<>();
		this.productos = productoService.buscarPorCategoria(this.categoria);
		
		if (this.productos.isEmpty()) {
			attr.addFlashAttribute("warning", "No se encontro ningun resultado!");
			return "redirect:/pedido/";
		}
		
		return "redirect:/pedido/";
	}
	
	@Secured("ROLE_CAJERO")
	@GetMapping("/categoria/sopas")
	public String categoriaSopa(RedirectAttributes attr) {
		this.categoria = categoriaServ.buscarPorCategoria("SOPAS");
		this.productos = new ArrayList<>();
		this.productos = productoService.buscarPorCategoria(this.categoria);
		
		if (this.productos.isEmpty()) {
			attr.addFlashAttribute("warning", "No se encontro ningun resultado!");
			return "redirect:/pedido/";
		}
		
		return "redirect:/pedido/";
	}
	
	@Secured("ROLE_CAJERO")
	@GetMapping("/categoria/hamburguesas")
	public String categoriaHamburguesa(RedirectAttributes attr) {
		this.categoria = categoriaServ.buscarPorCategoria("HAMBURGUESAS");
		this.productos = new ArrayList<>();
		this.productos = productoService.buscarPorCategoria(this.categoria);
		
		if (this.productos.isEmpty()) {
			attr.addFlashAttribute("warning", "No se encontro ningun resultado!");
			return "redirect:/pedido/";
		}
		
		return "redirect:/pedido/";
	}
	
	@Secured("ROLE_CAJERO")
	@GetMapping("/categoria/sanduches")
	public String categoriaSanduche(RedirectAttributes attr) {
		this.categoria = categoriaServ.buscarPorCategoria("SANDUCHES");
		this.productos = new ArrayList<>();
		this.productos = productoService.buscarPorCategoria(this.categoria);
		
		if (this.productos.isEmpty()) {
			attr.addFlashAttribute("warning", "No se encontro ningun resultado!");
			return "redirect:/pedido/";
		}
		
		return "redirect:/pedido/";
	}
	
	@Secured("ROLE_CAJERO")
	@GetMapping("/categoria/perros-calientes")
	public String categoriaPerroCaliente(RedirectAttributes attr) {
		this.categoria = categoriaServ.buscarPorCategoria("PERROS CALIENTES");
		this.productos = new ArrayList<>();
		this.productos = productoService.buscarPorCategoria(this.categoria);
		
		if (this.productos.isEmpty()) {
			attr.addFlashAttribute("warning", "No se encontro ningun resultado!");
			return "redirect:/pedido/";
		}
		
		return "redirect:/pedido/";
	}
	
	@Secured("ROLE_CAJERO")
	@GetMapping("/categoria/adiciones")
	public String categoriaAdiciones(RedirectAttributes attr) {
		this.categoria = categoriaServ.buscarPorCategoria("ADICIONES");
		this.productos = new ArrayList<>();
		this.productos = productoService.buscarPorCategoria(this.categoria);
		
		if (this.productos.isEmpty()) {
			attr.addFlashAttribute("warning", "No se encontro ningun resultado!");
			return "redirect:/pedido/";
		}
		
		return "redirect:/pedido/";
	}
	
	@Secured("ROLE_CAJERO")
	@GetMapping("/categoria/acompañamientos")
	public String categoriaAcompañamiento(RedirectAttributes attr) {
		this.categoria = categoriaServ.buscarPorCategoria("ACOMPAÑAMIENTOS");
		this.productos = new ArrayList<>();
		this.productos = productoService.buscarPorCategoria(this.categoria);
		
		if (this.productos.isEmpty()) {
			attr.addFlashAttribute("warning", "No se encontro ningun resultado!");
			return "redirect:/pedido/";
		}
		
		return "redirect:/pedido/";
	}
	
	@Secured("ROLE_CAJERO")
	@GetMapping("/categoria/fuertes")
	public String categoriaFuertes(RedirectAttributes attr) {
		this.categoria = categoriaServ.buscarPorCategoria("FUERTES");
		this.productos = new ArrayList<>();
		this.productos = productoService.buscarPorCategoria(this.categoria);
		
		if (this.productos.isEmpty()) {
			attr.addFlashAttribute("warning", "No se encontro ningun resultado!");
			return "redirect:/pedido/";
		}
		
		return "redirect:/pedido/";
	}
	
	@Secured("ROLE_CAJERO")
	@GetMapping("/categoria/postres")
	public String categoriaPostre(RedirectAttributes attr) {
		this.categoria = categoriaServ.buscarPorCategoria("POSTRES");
		this.productos = new ArrayList<>();
		this.productos = productoService.buscarPorCategoria(this.categoria);
		
		if (this.productos.isEmpty()) {
			attr.addFlashAttribute("warning", "No se encontro ningun resultado!");
			return "redirect:/pedido/";
		}
		
		return "redirect:/pedido/";
	}
	
	@Secured("ROLE_CAJERO")
	@GetMapping("/categoria/bebidas-calientes")
	public String categoriaBebidaCaliente(RedirectAttributes attr) {
		this.categoria = categoriaServ.buscarPorCategoria("BEBIDAS CALIENTES");
		this.productos = new ArrayList<>();
		this.productos = productoService.buscarPorCategoria(this.categoria);
		
		if (this.productos.isEmpty()) {
			attr.addFlashAttribute("warning", "No se encontro ningun resultado!");
			return "redirect:/pedido/";
		}
		
		return "redirect:/pedido/";
	}
	
	@Secured("ROLE_CAJERO")
	@GetMapping("/categoria/bebidas-frias")
	public String categoriaBebidaFria(RedirectAttributes attr) {
		this.categoria = categoriaServ.buscarPorCategoria("BEBIDAS FRIAS");
		this.productos = new ArrayList<>();
		this.productos = productoService.buscarPorCategoria(this.categoria);
		
		if (this.productos.isEmpty()) {
			attr.addFlashAttribute("warning", "No se encontro ningun resultado!");
			return "redirect:/pedido/";
		}
		
		return "redirect:/pedido/";
	}
	
	@Secured("ROLE_CAJERO")
	@GetMapping("/categoria/cervezas")
	public String categoriaCerveza(RedirectAttributes attr) {
		this.categoria = categoriaServ.buscarPorCategoria("CERVEZAS");
		this.productos = new ArrayList<>();
		this.productos = productoService.buscarPorCategoria(this.categoria);
		
		if (this.productos.isEmpty()) {
			attr.addFlashAttribute("warning", "No se encontro ningun resultado!");
			return "redirect:/pedido/";
		}
		
		return "redirect:/pedido/";
	}
	
	@Secured("ROLE_CAJERO")
	@GetMapping("/categoria/cocteles")
	public String categoriaCoctel(RedirectAttributes attr) {
		this.categoria = categoriaServ.buscarPorCategoria("COCTELES");
		this.productos = new ArrayList<>();
		this.productos = productoService.buscarPorCategoria(this.categoria);
		
		if (this.productos.isEmpty()) {
			attr.addFlashAttribute("warning", "No se encontro ningun resultado!");
			return "redirect:/pedido/";
		}
		
		return "redirect:/pedido/";
	}
	
	@Secured("ROLE_CAJERO")
	@GetMapping("/categoria/vinos")
	public String categoriaVinos(RedirectAttributes attr) {
		this.categoria = categoriaServ.buscarPorCategoria("VINOS");
		this.productos = new ArrayList<>();
		this.productos = productoService.buscarPorCategoria(this.categoria);
		
		if (this.productos.isEmpty()) {
			attr.addFlashAttribute("warning", "No se encontro ningun resultado!");
			return "redirect:/pedido/";
		}
		
		return "redirect:/pedido/";
	}
	
	@Secured("ROLE_CAJERO")
	@GetMapping("/categoria/licores")
	public String categoriaLicores(RedirectAttributes attr) {
		this.categoria = categoriaServ.buscarPorCategoria("LICORES");
		this.productos = new ArrayList<>();
		this.productos = productoService.buscarPorCategoria(this.categoria);
		
		if (this.productos.isEmpty()) {
			attr.addFlashAttribute("warning", "No se encontro ningun resultado!");
			return "redirect:/pedido/";
		}
		
		return "redirect:/pedido/";
	}

	@Override
	@Secured("ROLE_ADMIN")
	@SuppressWarnings("deprecation")
	protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setHeader("Content-Disposition", "attachment; filename=\"pedidos.xlsx\"");
		Sheet hoja = workbook.createSheet("Pedidos");
		
		CellStyle style = workbook.createCellStyle();
		XSSFFont font = (XSSFFont) workbook.createFont();
		font.setBold(true);
		font.setFontHeight(16);
		style.setFont(font);

		Row filaTitulo = hoja.createRow(0);
		Cell celda = filaTitulo.createCell(0);
		celda.setCellValue("");
		celda.setCellStyle(style);

		Row filaData = hoja.createRow(0);
		String[] columnas = { "ID", "Numero", "Fecha", "Empleado", "KPuntos", "Subtotal", "Total"};

		for (int i = 0; i < columnas.length; i++) {
			celda = filaData.createCell(i);
			celda.setCellValue(columnas[i]);
			celda.setCellStyle(style);
		}

		int numFila = 1;

		if (this.pedidosX == null) {
			this.pedidosX = pedidoService.listar();
		}

		for (Pedido pedido : this.pedidosX) {
			filaData = hoja.createRow(numFila);

			filaData.createCell(0).setCellValue(pedido.getIdPedido());
			hoja.autoSizeColumn(0);
			filaData.createCell(1).setCellValue(pedido.getNumeroOrden());
			hoja.autoSizeColumn(1);
			
			String fecha = pedido.getFecha().getDate()+"-"+(pedido.getFecha().getMonth()+1)+"-"+(pedido.getFecha().getYear()+1900);
			
			filaData.createCell(2).setCellValue(fecha);
			hoja.autoSizeColumn(2);
			filaData.createCell(3).setCellValue(pedido.getEmpleado().getUser().getNombres() + pedido.getEmpleado().getUser().getApellidos());
			hoja.autoSizeColumn(3);
			filaData.createCell(4).setCellValue(pedido.getKangrepuntos());
			hoja.autoSizeColumn(4);
			filaData.createCell(5).setCellValue(pedido.getSubtotal());
			hoja.autoSizeColumn(5);
			filaData.createCell(6).setCellValue(pedido.getTotal());
			hoja.autoSizeColumn(6);
			numFila++;
		}
		
	}

	public List<DetallePedido> getDetallesAux() {
		return detallesAux;
	}

	public void setDetallesAux(List<DetallePedido> detallesAux) {
		this.detallesAux = detallesAux;
	}
	
	@Secured("ROLE_ADMIN")
	@GetMapping("/pdf")	
	public void exportToPDF(HttpServletResponse response) throws DocumentException, IOException {
		response.setContentType("application/pdf");
		
		DateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
		String currentDateTime = dateFormatter.format(new Date());
		
		String headerKey = "Content-Disposition";
		String headerValue = "attachment; filename=Ordenes_" + currentDateTime +".pdf";
		
		response.setHeader(headerKey, headerValue);
		
		if (pedidosX == null) {
			PedidoPDFExporter exporter = new PedidoPDFExporter(pedidoService.listar());
			exporter.export(response);
		}
		else {
			PedidoPDFExporter exporter = new PedidoPDFExporter(pedidosX);
			exporter.export(response);
		}
		
	}
	
	public DetallePedido itemIsAdded(DetallePedido detalle) {
		
		for (DetallePedido detallePedido : detallesAgregados) {
			if (detallePedido.getProducto().getIdProducto().equals(detalle.getProducto().getIdProducto())) {
				return detallePedido;
			}
		}
		return null;
	}
	
}
