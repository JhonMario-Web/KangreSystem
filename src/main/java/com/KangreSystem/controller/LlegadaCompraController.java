package com.KangreSystem.controller;

import java.security.Principal;
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
import com.KangreSystem.models.entity.Administrador;
import com.KangreSystem.models.entity.Compra;
import com.KangreSystem.models.entity.DetalleCompra;
import com.KangreSystem.models.entity.DetalleLlegadaCompra;
import com.KangreSystem.models.entity.Insumo;
import com.KangreSystem.models.entity.LlegadaCompra;
import com.KangreSystem.models.entity.Proveedor;
import com.KangreSystem.models.entity.VencimientoLote;
import com.KangreSystem.models.service.IAdminService;
import com.KangreSystem.models.service.ICompraService;
import com.KangreSystem.models.service.IDetalleCompraService;
import com.KangreSystem.models.service.IInsumoService;
import com.KangreSystem.models.service.IInventarioService;
import com.KangreSystem.models.service.ILlegadaCompraServ;
import com.KangreSystem.models.service.IProveedorService;
import com.KangreSystem.models.service.IUserService;

@Controller
@Secured("ROLE_ADMIN")
@Component("/Views/SI/Compra/Llegada/comprasRecibidas.xlsx")
@RequestMapping("/proveedor/servicios/compra/recibir")
public class LlegadaCompraController extends AbstractXlsxView{
	
	@Autowired
	private ICompraService compraService;
	
	@Autowired
	private IDetalleCompraService detalleCompraService;
	
	@Autowired
	private ILlegadaCompraServ llegadaService;
	
	@Autowired
	private IUserService userService;
	
	@Autowired
	private IAdminService adminService;
	
	@Autowired
	private IInsumoService insumoService;
	
	@Autowired
	private IInventarioService inventarioService;
	
	@Autowired
	private IProveedorService proveedorService;
	
	private Compra compra = new Compra();
	
	private LlegadaCompra llegada = new LlegadaCompra();
	
	private Administrador admin = new Administrador();
	
	List<DetalleLlegadaCompra> aRecibir = new ArrayList<>();
	
	List<DetalleCompra> solicitados = new ArrayList<>();
	
	List<VencimientoLote> vencimientos = new ArrayList<>();
	
	List<LlegadaCompra> llegadas = new ArrayList<>();
	
	List<LlegadaCompra> llegadasX;

	@GetMapping("/")
	public String comprasRecibidas(Model model) {
		this.llegadas = llegadaService.listar();
		model.addAttribute("llegadas", this.llegadas);
		model.addAttribute("proveedores", proveedorService.listar());
		return "Views/SI/Compra/Llegada/comprasRecibidas";
	}
	
	@PostMapping("/")
	public String filtrar(@Param("idLlegadaModal") Long idLlegadaModal, 
			@Param("idProveedorModal") Long idProveedorModal, Model model) {
		Proveedor proveedor = null;
		
		if (idLlegadaModal == null && idProveedorModal == null) {
			model.addAttribute("warning", "No se encontraron criterios de busqueda!");
			return "Views/SI/Compra/Llegada/comprasRecibidas";
		} else if (idLlegadaModal != null && idProveedorModal == null) {
			this.llegadas = new ArrayList<>();
			this.llegadas.add(llegadaService.buscarPorId(idLlegadaModal));
			
			if (this.llegadas.isEmpty()) {
				this.llegadasX = null;
				model.addAttribute("llegadas", llegadaService.listar());
				model.addAttribute("proveedores", proveedorService.listar());
				model.addAttribute("error", "No se encontraron resultados!");
				return "Views/SI/Compra/Llegada/comprasRecibidas";
			}
			
			this.llegadasX = this.llegadas;
			
			model.addAttribute("llegadas", this.llegadas);
			model.addAttribute("proveedores", proveedorService.listar());
			return "Views/SI/Compra/Llegada/comprasRecibidas";
		} else if (idLlegadaModal == null && idProveedorModal != null) {
			proveedor = proveedorService.buscarPorId(idProveedorModal);
			this.llegadas = llegadaService.buscarPorProveedor(proveedor);
			
			if (this.llegadas.isEmpty()) {
				this.llegadasX = null;
				model.addAttribute("llegadas", llegadaService.listar());
				model.addAttribute("proveedores", proveedorService.listar());
				model.addAttribute("error", "No se encontraron resultados!");
				return "Views/SI/Compra/Llegada/comprasRecibidas";
			}
			
			this.llegadasX = this.llegadas;
			
			model.addAttribute("llegadas", this.llegadas);
			model.addAttribute("proveedores", proveedorService.listar());
			return "Views/SI/Compra/Llegada/comprasRecibidas";
		} else if (idLlegadaModal != null && idProveedorModal != null) {
			model.addAttribute("error", "No se puede filtrar por ambos criterios de busqueda!");
			return "Views/SI/Compra/Llegada/comprasRecibidas";
		}
		
		model.addAttribute("llegadas", this.llegadas);
		model.addAttribute("proveedores", proveedorService.listar());
		return "Views/SI/Compra/Llegada/comprasRecibidas";
	}
	
	@GetMapping("/limpiar-filtro")
	public String limpiarFiltro() {
		this.llegadasX = null;
		return "redirect:/proveedor/servicios/compra/recibir/";
	}
	
	@GetMapping("/novedad/{id}")
	public ResponseEntity<LlegadaCompra> novedadLlegada(@PathVariable("id") Long idLlegada){
		try {
			return new ResponseEntity<LlegadaCompra>(llegadaService.buscarPorId(idLlegada), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<LlegadaCompra>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping("/nueva")
	public String recibirNueva(Principal principal) {
		this.compra = new Compra();
		this.llegada = new LlegadaCompra();
		this.admin = new Administrador();
		this.aRecibir = new ArrayList<>();
		this.solicitados = new ArrayList<>();
		this.vencimientos = new ArrayList<>();
		this.admin = adminService.buscarPorUser(userService.buscarPorNumeroDoc(principal.getName()));
		this.llegada.setAdmin(this.admin);
		return "Views/SI/Compra/Llegada/recibirCompra";
	}
	
	@PostMapping("/buscar-compra")
	public String buscarCompra(@RequestParam("numeroCompra") String numeroCompra, RedirectAttributes attr,
			@RequestParam("numeroFactura") String numeroFactura) {
		this.llegada.setNumeroFactura(numeroFactura.toUpperCase());
		Compra compra = compraService.buscarPorNumeroCompra(numeroCompra);
		
		if (compra == null) {
			attr.addFlashAttribute("error", "No se ha encontrado ninguna compra!");
			return "redirect:/proveedor/servicios/compra/recibir/nueva";
		} else if (compra.getEstado().equals("RECIBIDA")) {
			attr.addFlashAttribute("error", "La compra ingresada ya se encuentra recibida!");
			return "redirect:/proveedor/servicios/compra/recibir/nueva";
		} else if (llegadaService.existePorNumeroFactura(numeroFactura)) {
			attr.addFlashAttribute("error", "La compra ingresada ya se encuentra recibida!");
			return "redirect:/proveedor/servicios/compra/recibir/nueva";
		}
		
		this.compra = compra;
		this.llegada.setCompra(compra);
		this.llegada.setProveedor(this.compra.getProveedor());
		this.llegada.setNumeroLlegada(llegadaService.numeroLlegadaCompra());
		return "redirect:/proveedor/servicios/compra/recibir/verificar-insumos";
	}
	
	@GetMapping("/verificar-insumos")
	public String verificarInsumos(Model model) {
		if (this.compra.getIdCompra() == null) {
			return "redirect:/proveedor/servicios/compra/recibir/nueva";
		}
		this.solicitados = detalleCompraService.buscarDetallesPorCompra(this.compra);
		
		long i = 1;
		for (DetalleLlegadaCompra detalle : this.aRecibir) {
			detalle.setIdDetalle(i);
			i++;
		}
		
		model.addAttribute("solicitados", solicitados);
		model.addAttribute("aRecibir", this.aRecibir);
		model.addAttribute("compra", this.compra);
		model.addAttribute("llegada", this.llegada);
		model.addAttribute("detalle", new DetalleLlegadaCompra());
		model.addAttribute("cantidadIgual", llegadaService.cantidadesIguales(this.aRecibir));
		model.addAttribute("precioIgual", llegadaService.preciosIguales(this.llegada, this.aRecibir));
		model.addAttribute("insumosFaltantes", llegadaService.insumosFaltantes(this.llegada, this.aRecibir));
		return "Views/SI/Compra/Llegada/recibirCompraInsumos";
	}
	
	@GetMapping("/insumo-details/{id}")
	public ResponseEntity<Insumo> insumoDetails(@PathVariable("id") Long idInsumo) {
		try {
			return new ResponseEntity<Insumo>(insumoService.buscarPorId(idInsumo), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Insumo>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping("/recibir-insumo")
	public String recibirInsumo(@ModelAttribute DetalleLlegadaCompra detalle, RedirectAttributes attr) {
		Insumo insumo = insumoService.buscarPorId(detalle.getInsumo().getIdInsumo());
		
		if (detalle.getCantidad() < 0 || detalle.getCantidad() > 500) {
			attr.addFlashAttribute("error", "La cantidad ingresada no es permitida");
			return "redirect:/proveedor/servicios/compra/recibir/verificar-insumos";
		}
		
		insumo.setPrecio(detalle.getInsumo().getPrecio());
		detalle.setCantEquals(false);
		detalle.setInsumo(insumo);
		detalle.setLlegada(this.llegada);
		
		if (itemIsAdded(detalle) != null) {
			DetalleLlegadaCompra detalleAux = itemIsAdded(detalle);
			
			if (detalle.getInsumo().getPrecio() != null) {
				detalleAux.getInsumo().setPrecio(detalle.getInsumo().getPrecio());
			} 
			
			Long indice = detalleAux.getIdDetalle();
			detalleAux.setCantidad(detalleAux.getCantidad() + detalle.getCantidad());
			detalleAux.setSubtotal((long) (detalleAux.getCantidad() * detalleAux.getInsumo().getPrecio()));
			
			this.aRecibir.add(detalleAux);
			this.aRecibir.remove(Integer.parseInt(indice.toString()) - 1);
			
			for (DetalleCompra detalleCompra : this.solicitados) {
				if (detalleCompra.getInsumo().getIdInsumo().equals(insumo.getIdInsumo())) {
					if (detalleCompra.getCantidad().equals(detalleAux.getCantidad())) {
						detalleAux.setCantEquals(true);
					}
				}
			}
			
			return "redirect:/proveedor/servicios/compra/recibir/verificar-insumos";
		}
		
		for (DetalleCompra detalleCompra : this.solicitados) {
			if (detalleCompra.getInsumo().getIdInsumo().equals(insumo.getIdInsumo())) {
				if (detalleCompra.getCantidad().equals(detalle.getCantidad())) {
					detalle.setCantEquals(true);
				}
			}
		}
		detalle.setSubtotal((long) (detalle.getCantidad() * detalle.getInsumo().getPrecio()));
		this.aRecibir.add(detalle);
		return "redirect:/proveedor/servicios/compra/recibir/verificar-insumos";
	}
	
	@GetMapping("/quitar-insumo/{item}")
	public String quitarInsumo(@PathVariable("item") Long item) {
		this.aRecibir.remove(Integer.parseInt(item.toString()));
		return "redirect:/proveedor/servicios/compra/recibir/verificar-insumos";
	}
	
	@PostMapping("/insumos-recibidos")
	public String recibirCompra(@ModelAttribute DetalleLlegadaCompra recibir, RedirectAttributes attr,
			Model model, @ModelAttribute LlegadaCompra llegada) {
		if (!llegadaService.insumosFaltantes(this.llegada, this.aRecibir)) {
			attr.addFlashAttribute("error", "No se ha completado de recibir los insumos, recuerde, si el insumo no llega, se debe colocar el cantidad recibida en 0.");
			return "redirect:/proveedor/servicios/compra/recibir/verificar-insumos";
		}
			
		if (llegada.getNovedad() == null) {
			this.llegada.setNovedad("SIN NOVEDADES");
			return "redirect:/proveedor/servicios/compra/recibir/lotes-vencimientos";
		}
		
		this.llegada.setNovedad(llegada.getNovedad());
		return "redirect:/proveedor/servicios/compra/recibir/lotes-vencimientos";
	}
	
	@GetMapping("/lotes-vencimientos")
	public String lotesVencimientos(Model model) {
		if (this.compra.getIdCompra() == null) {
			return "redirect:/proveedor/servicios/compra/recibir/nueva";
		}
		
		if (allInsumoHasZero(this.aRecibir)) {
			llegadaService.recibirCompraEnCero(this.llegada.getCompra());
			return "redirect:/proveedor/servicios/compra/recibir/success";
		}
		
		long i = 1;
		for (VencimientoLote vencimiento : this.vencimientos) {
			vencimiento.setIdVencimiento(i);
			i++;
		}
		
		model.addAttribute("agregados", this.aRecibir);
		model.addAttribute("llegada", this.llegada);
		model.addAttribute("vencimiento", new VencimientoLote());
		model.addAttribute("vencimientos", this.vencimientos);
		return "Views/SI/Compra/Llegada/loteVencimientoInsumo";
	}
	
	@SuppressWarnings("deprecation")
	@PostMapping("/agregar-vencimiento")
	public String agregarVencimiento(@ModelAttribute VencimientoLote vencimiento, RedirectAttributes attr) {
		Insumo insumo = insumoService.buscarPorId(vencimiento.getInsumo().getIdInsumo());
		
		vencimiento.setInsumo(insumo);
		vencimiento.getFecha().setDate(vencimiento.getFecha().getDate() + 1);
		vencimiento.setCantidadDisponible(Float.parseFloat(vencimiento.getCantidad().toString()));
		vencimiento.setLlegada(this.llegada);
		vencimiento.setLote(vencimiento.getLote().toUpperCase());
		
		if (!validCantInsVencimiento(vencimiento, this.aRecibir, this.vencimientos)) {
			attr.addFlashAttribute("error", "Cantidad incorrecta!, recuerde que la cantidad no debe ser mayor a la recibida.");
			return "redirect:/proveedor/servicios/compra/recibir/lotes-vencimientos";
		} else if (!validFechaActual(vencimiento)) {
			attr.addFlashAttribute("error", "La fecha de vencimiento debe ser igual o superior a la actual!");
			return "redirect:/proveedor/servicios/compra/recibir/lotes-vencimientos";
		} else if (!validFechaVencimiento(vencimiento, this.vencimientos)) {
			attr.addFlashAttribute("error", "La fecha a ingresar ya se encuentra agregada!");
			return "redirect:/proveedor/servicios/compra/recibir/lotes-vencimientos";
		} else if (!validLoteVencimiento(vencimiento, this.vencimientos)) {
			attr.addFlashAttribute("error", "El lote a ingresar ya se encuentra agregado!");
			return "redirect:/proveedor/servicios/compra/recibir/lotes-vencimientos";
		}
		
		this.vencimientos.add(vencimiento);
		return "redirect:/proveedor/servicios/compra/recibir/lotes-vencimientos";
	}
	
	@GetMapping("/success")
	public String success() {
		return "Views/SI/Compra/Llegada/llegadaSuccess";
	}
	
	@GetMapping("/quitar-vencimiento/{item}")
	public String quitarVencimiento(@PathVariable("item") int item) {
		this.vencimientos.remove(item);
		return "redirect:/proveedor/servicios/compra/recibir/lotes-vencimientos";
	}
	
	@GetMapping("/recibir-compra")
	public String recibirCompra(RedirectAttributes attr) {
		if (!validVencimientoCantFull(this.aRecibir, this.vencimientos)) {
			attr.addFlashAttribute("error", "Faltan unidades");
			return "redirect:/proveedor/servicios/compra/recibir/lotes-vencimientos";
		}
		
		long total = 0;
		for (DetalleLlegadaCompra detalleLlegadaCompra : aRecibir) {
			total += detalleLlegadaCompra.getSubtotal();
		}
		
		for (VencimientoLote v : this.vencimientos) {
			v.setIdVencimiento(null);
		}
		
		this.llegada.setTotal(total);
		this.llegada.setFecha(new Date());
		this.llegada.setHora(new Date());
		
		inventarioService.recibirCompra(this.llegada, this.aRecibir, this.vencimientos);
		return "redirect:/proveedor/servicios/compra/recibir/success";
	}
	
	@GetMapping("/cancelar/{id}")
	public String cancelarLlegada(@PathVariable("id") Long idLlegada, RedirectAttributes attr) {
		LlegadaCompra recibo = null;
		
		if (idLlegada > 0) {
			recibo = llegadaService.buscarPorId(idLlegada);
			
			if (recibo == null) {
				attr.addFlashAttribute("error", "El ID de la llegada no existe!");
				return "redirect:/proveedor/servicios/compra/recibir/";
			}
		} else {
			attr.addFlashAttribute("error", "El ID de la llegada no existe!");
			return "redirect:/proveedor/servicios/compra/recibir/";
		}
		
		if (!availableToCancel(recibo)) {
			attr.addFlashAttribute("error", "El recido de la compra debe cancelarse el mismo dia que se recibe!");
			return "redirect:/proveedor/servicios/compra/recibir/";
		}
		
		inventarioService.cancelarLlegada(recibo);
		
		attr.addFlashAttribute("warning", "La llegada se ha cancelado correctamente!");
		return "redirect:/proveedor/servicios/compra/recibir/";
	}

	@GetMapping("/en-ceros/{id}")
	public String recibirCompraEnCeros(@PathVariable("id") Long idCompra, RedirectAttributes attr) {
		Compra compra = null;
		
		if (idCompra > 0) {
			compra = compraService.buscarPorId(idCompra);
			
			if (compra == null) {
				attr.addFlashAttribute("error", "El ID de la compra que desea recibir no existe!");
				return "redirect:/proveedor/servicios/compra/";
			}
		} else {
			attr.addFlashAttribute("error", "El ID de la compra que desea recibir no existe!");
			return "redirect:/proveedor/servicios/compra/";
		}
		
		if (compra.getEstado().equals("RECIBIDA")) {
			attr.addFlashAttribute("error", "La compra que desea recibir ya se encuentra recibida!");
			return "redirect:/proveedor/servicios/compra/";
		}
		
		llegadaService.recibirCompraEnCero(compra);
		attr.addFlashAttribute("warning", "La compra se ha recibido en ceros exitosamente!");
		return "redirect:/proveedor/servicios/compra/";
	}
	
	@SuppressWarnings("deprecation")
	public boolean availableToCancel(LlegadaCompra llegada) {
		Date fechaActual = new Date();
		
		fechaActual.setHours(0);
		fechaActual.setMinutes(0);
		fechaActual.setSeconds(0);
		
		llegada.getFecha().setHours(0);
		llegada.getFecha().setMinutes(0);
		llegada.getFecha().setSeconds(0);
		
		int diaActual = fechaActual.getDate();
		int mesActual = fechaActual.getMonth();
		int anioActual = fechaActual.getYear();
		
		int diaLlegada = llegada.getFecha().getDate();
		int mesLlegada = llegada.getFecha().getMonth();
		int anioLlegada = llegada.getFecha().getYear();
		
		if (diaActual == diaLlegada && mesActual == mesLlegada && anioActual == anioLlegada) {
			return true;
		}
		
		return false;
	}

	public boolean validCantInsVencimiento(VencimientoLote vencimiento, List<DetalleLlegadaCompra> agregados,
			List<VencimientoLote> vencimientos) {
		for (DetalleLlegadaCompra detalleLlegada : agregados) {
			if (vencimiento.getInsumo().getIdInsumo().equals(detalleLlegada.getInsumo().getIdInsumo())) {
				if (vencimiento.getCantidad() > detalleLlegada.getCantidad()) {
					return false;
				}
				
				int total = 0;
				for (VencimientoLote vencimientoLote : vencimientos) {
					if (vencimiento.getInsumo().getIdInsumo().equals(vencimientoLote.getInsumo().getIdInsumo())) {
						total += vencimientoLote.getCantidad();
					}
				}
				
				if (total + vencimiento.getCantidad() > detalleLlegada.getCantidad()) {
					return false;
				}
			}
		}
		return true;
	}
	
	public DetalleLlegadaCompra itemIsAdded(DetalleLlegadaCompra detalle) {
		
		for (DetalleLlegadaCompra detalleLlegada : this.aRecibir) {
			if (detalleLlegada.getInsumo().getIdInsumo().equals(detalle.getInsumo().getIdInsumo())) {
				return detalleLlegada;
			}
		}
		return null;
	}
	
	public boolean validFechaVencimiento(VencimientoLote vencimiento, List<VencimientoLote> vencimientos) {
		for (VencimientoLote vencimientoLote : vencimientos) {
			if (vencimiento.getLote().toLowerCase().equals(vencimientoLote.getLote().toLowerCase())
					&& vencimiento.getFecha().equals(vencimientoLote.getFecha())
					&& vencimiento.getInsumo().getIdInsumo().equals(vencimientoLote.getInsumo().getIdInsumo())) {
				return false;
			}
		}
		return true;
	}
	
	@SuppressWarnings("deprecation")
	public boolean validFechaActual(VencimientoLote vencimiento) {
		Date fechaActual = new Date();
		
		fechaActual.setHours(0);
		fechaActual.setMinutes(0);
		fechaActual.setSeconds(0);
		
		vencimiento.getFecha().setHours(0);
		vencimiento.getFecha().setMinutes(0);
		vencimiento.getFecha().setSeconds(0);
		
		if (vencimiento.getFecha().after(fechaActual)) {
			return true;
		}
		
		return false;
	}
	
	public boolean validLoteVencimiento(VencimientoLote vencimiento, List<VencimientoLote> vencimientos) {
		for (VencimientoLote vencimientoLote : vencimientos) {
			if (vencimiento.getLote().toLowerCase().equals(vencimientoLote.getLote().toLowerCase())
					&& vencimiento.getInsumo().getIdInsumo().equals(vencimientoLote.getInsumo().getIdInsumo())) {
				return false;
			}
		}
		return true;
	}
	
	public boolean allInsumoHasZero(List<DetalleLlegadaCompra> detalles) {
		boolean hasZero = true;
		for (DetalleLlegadaCompra detalleLlegadaCompra : detalles) {
			if (detalleLlegadaCompra.getCantidad() == 0) {
				hasZero = true;
			} else {
				hasZero = false;
			}
		}
		return hasZero;
	}

	public boolean validVencimientoCantFull(List<DetalleLlegadaCompra> recibidos, List<VencimientoLote> vencimientos) {	
		List<VencimientoLote> vencimientosInsumo = new ArrayList<>();
		int total = 0;
		int cant = 0;
		boolean flag = true;
		
		for (DetalleLlegadaCompra detalleLlegada : recibidos) {
			cant = detalleLlegada.getCantidad();
			total = 0;
			vencimientosInsumo = new ArrayList<>();
			
			for (VencimientoLote vencimientoLote : vencimientos) {
				if (detalleLlegada.getInsumo().getIdInsumo().equals(vencimientoLote.getInsumo().getIdInsumo())) {
					vencimientosInsumo.add(vencimientoLote);
				}
			}
			
			for (VencimientoLote vencimientoLote : vencimientosInsumo) {
				total += vencimientoLote.getCantidad();
			}
			
			if (cant != total) {
				flag = false;
			}
			
		}
		
		return flag;
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setHeader("Content-Disposition", "attachment; filename=\"compras_recibidas.xlsx\"");
		Sheet hoja = workbook.createSheet("Compras recibidas");

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
		String[] columnas = { "ID", "Numero", "Fecha", "Total", "Proveedor", "Novedad"};

		for (int i = 0; i < columnas.length; i++) {
			celda = filaData.createCell(i);
			celda.setCellValue(columnas[i]);
			celda.setCellStyle(style);
		}

		int numFila = 1;

		if (this.llegadasX == null) {
			this.llegadasX = llegadaService.listar();
		}

		for (LlegadaCompra llegada : this.llegadasX) {
			filaData = hoja.createRow(numFila);

			filaData.createCell(0).setCellValue(llegada.getIdLlegada());
			hoja.autoSizeColumn(0);
			filaData.createCell(1).setCellValue(llegada.getNumeroLlegada());
			hoja.autoSizeColumn(1);
			
			String fecha = llegada.getFecha().getDate()+"-"+(llegada.getFecha().getMonth()+1)+"-"+(llegada.getFecha().getYear()+1900);
			
			filaData.createCell(2).setCellValue(fecha);
			hoja.autoSizeColumn(2);
			filaData.createCell(3).setCellValue("$"+llegada.getTotal());
			hoja.autoSizeColumn(3);
			filaData.createCell(4).setCellValue(llegada.getProveedor().getNombre());
			hoja.autoSizeColumn(4);
			filaData.createCell(5).setCellValue(llegada.getNovedad());
			hoja.autoSizeColumn(5);
			numFila++;
		}
		
	}
	
}
