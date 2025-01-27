package com.KangreSystem.controller;

import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.KangreSystem.models.entity.Pedido;
import com.KangreSystem.models.service.IDetallePedidoServ;
import com.KangreSystem.models.service.IPedidoService;

@Controller
@RequestMapping("/pedido/orden")
public class OrdenController {
	
	@Autowired
	private IPedidoService pedidoService; 
	
	@Autowired
	private IDetallePedidoServ detallePedidoService;
	
	private Pedido orden = new Pedido();
	
	@Secured("ROLE_CAJERO")
	@GetMapping("/cajero/index")
	public String ordenesSolicitadasCajero(Model model) {
		model.addAttribute("solicitados", pedidoService.buscarPorEstadoFecha("SOLICITADO", new Date()));
		model.addAttribute("preparados", pedidoService.buscarPorEstadoFecha("PREPARADO", new Date()));
		model.addAttribute("cobrados", pedidoService.buscarPorEstadoFecha("COBRADO", new Date()));
		model.addAttribute("cancelados", pedidoService.buscarPorEstadoFecha("CANCELADO", new Date()));
		return "/Views/SI/Venta/Pedido/Ordenes/Cajero/indexOrdenCajero";
	}
	
	@Secured("ROLE_CAJERO")
	@GetMapping("/cajero/detalles/{id}")
	public String detallesOrden(@PathVariable("id") Long idOrden, Model model) {
		Pedido orden = pedidoService.buscarPorId(idOrden); 
		model.addAttribute("detalles", detallePedidoService.buscarDetallesPorPedido(orden));
		model.addAttribute("orden", orden);
		return "/Views/SI/Venta/Pedido/Ordenes/Cajero/detallesOrden";
	}
	
	@Secured("ROLE_COCINERO")
	@GetMapping("/cocinero/index")
	public String ordenesSolicitadasCocinero(Model model) {
		model.addAttribute("solicitados", pedidoService.buscarPorEstadoFecha("SOLICITADO", new Date()));
		model.addAttribute("preparados", pedidoService.buscarPorEstadoFecha("PREPARADO", new Date()));
		model.addAttribute("cobrados", pedidoService.buscarPorEstadoFecha("COBRADO", new Date()));
		model.addAttribute("cancelados", pedidoService.buscarPorEstadoFecha("CANCELADO", new Date()));
		return "/Views/SI/Venta/Pedido/Ordenes/Cocinero/indexOrdenCocinero";
	}
	
	@Secured("ROLE_COCINERO")
	@GetMapping("/cocinero/preparar/{id}")
	public String prepararOrden(@PathVariable("id") Long idOrden, Model model, RedirectAttributes attr) {
		this.orden = null;
		
		if (idOrden > 0) {
			this.orden = pedidoService.buscarPorId(idOrden); 
			
			if (orden == null) {
				attr.addFlashAttribute("error", "El ID de la orden que desea consultar no existe!");
				return "redirect:/pedido/orden/cocinero/index";
			}
		} else {
			attr.addFlashAttribute("error", "El ID de la orden que desea consultar no existe!");
			return "redirect:/pedido/orden/cocinero/index";
		}
		
		model.addAttribute("detalles", detallePedidoService.buscarDetallesPorPedido(orden));
		model.addAttribute("orden", orden);
		return "/Views/SI/Venta/Pedido/Ordenes/Cocinero/prepararOrden";
	}
	
	@Secured("ROLE_COCINERO")
	@GetMapping("/cocinero/despachar")
	public String despacharOrden(RedirectAttributes attr) {
		
		this.orden.setEstado("PREPARADO");
		pedidoService.guardar(this.orden);
		
		attr.addFlashAttribute("success", "Orden despachada correctamente!");
		return "redirect:/pedido/orden/cocinero/index";
	}
	
	@Secured("ROLE_ADMIN")
	@GetMapping("/cancelar")
	public String cancelarOrden() {
		return "/Views/SI/Venta/Pedido/Ordenes/cancelarOrden";
	}
	
	@Secured("ROLE_ADMIN")
	@PostMapping("/cancelar")
	public String cancelarOrden(@RequestParam("numeroOrdenModal") String numeroOrden, RedirectAttributes attr) {
		Pedido o = pedidoService.buscarPorNumeroOrden(numeroOrden);
		
		if (o == null) {
			attr.addFlashAttribute("error", "No se ha encontrado ninguna orden con el numero ingresado!");
			return "redirect:/pedido/orden/cancelar";
		} else if (o.getEstado().equals("COBRADO")) {
			attr.addFlashAttribute("error", "La orden que desea cancelar ya se encuentra cobrada!");
			return "redirect:/pedido/orden/cancelar";
		} else if (o.getEstado().equals("CANCELADO")) {
			attr.addFlashAttribute("error", "La orden que desea cancelar ya se encuentra cancelada!");
			return "redirect:/pedido/orden/cancelar";
		}
		
		o.setEstado("CANCELADO");
		pedidoService.guardar(o);
		
		attr.addFlashAttribute("warning", "Orden cancelada correctamente!");
		return "redirect:/pedido/orden/cancelar";
	}
	
}
