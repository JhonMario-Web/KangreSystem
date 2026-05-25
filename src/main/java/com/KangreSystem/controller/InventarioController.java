package com.KangreSystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.KangreSystem.models.service.IAveriaService;
import com.KangreSystem.models.service.ICompraService;
import com.KangreSystem.models.service.IEntradaService;
import com.KangreSystem.models.service.IInsumoService;
import com.KangreSystem.models.service.IProductoService;
import com.KangreSystem.models.service.IProveedorService;
import com.KangreSystem.models.service.ISalidaService;
import com.KangreSystem.models.service.IVencimientoLoteServ;

@Controller
@Secured("ROLE_ADMIN")
@RequestMapping("/inventario")
public class InventarioController {
	
	@Autowired
	private IProveedorService proveedorService;
	
	@Autowired
	private IInsumoService insumoService;
	
	@Autowired
	private IProductoService productoService;
	
	@Autowired
	private IEntradaService entradaService;
	
	@Autowired
	private ISalidaService salidaService;
	
	@Autowired
	private IVencimientoLoteServ vencimientoService;
	
	@Autowired
	private ICompraService compraService;
	
	@Autowired
	private IAveriaService averiaService;
	
	@GetMapping("/")
	public String index(Model model) {
		model.addAttribute("totalProv", proveedorService.totalProveedores());
		model.addAttribute("totalIns", insumoService.totalInsumos());
		model.addAttribute("totalProd", productoService.totalProductos());
		model.addAttribute("existencias", insumoService.insumosConExistencia());
		model.addAttribute("entradas", entradaService.entradasMesActual());
		model.addAttribute("salidas", salidaService.salidasMesActual());
		model.addAttribute("vencenEnTreinta", vencimientoService.vencenEnTreintaDias());
		model.addAttribute("compras", compraService.comprasMesActual());
		model.addAttribute("averias", averiaService.averiasMesActual());
		model.addAttribute("vencimientos", vencimientoService.vencimientosDisponibles());
		model.addAttribute("perdidas", averiaService.precioAveriasMesActual());
		return "Views/SI/Inventario/inventarioIndex";
	}

}
