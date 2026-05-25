package com.KangreSystem.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import javax.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.KangreSystem.models.entity.Cliente;
import com.KangreSystem.models.entity.DetallePedido;
import com.KangreSystem.models.entity.PagoEfectivo;
import com.KangreSystem.models.entity.Pedido;
import com.KangreSystem.models.entity.PuntoRedimido;
import com.KangreSystem.models.entity.User;
import com.KangreSystem.models.service.IClienteService;
import com.KangreSystem.models.service.IDetallePedidoServ;
import com.KangreSystem.models.service.IPedidoService;
import com.KangreSystem.models.service.IPuntoAcumuladoService;
import com.KangreSystem.models.service.IPuntoRedimidoService;
import com.KangreSystem.models.service.IUserService;

@Controller
@Secured("ROLE_CAJERO")
@RequestMapping("/pedido/pago")
public class PagoPedidoController {

	@Autowired
	private IPedidoService pedidoService;

	@Autowired
	private IDetallePedidoServ detallePedidoServ;

	@Autowired 
	private IPuntoAcumuladoService acumService;

	@Autowired
	private IUserService userService;

	@Autowired
	private IClienteService clienteService;
	
	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	private IPuntoRedimidoService redenService;

	private List<DetallePedido> detalles = new ArrayList<>();

	private List<PuntoRedimido> redimidos = new ArrayList<>();
	
	private Pedido pedido = new Pedido();
	
	private User cliente = new User();
	
	private PagoEfectivo pago = new PagoEfectivo(); 
	
	private int vlrTotalEnPuntos;
	
	private int puntosPendientes;
	
	private long iva;
	
	private long saldoPendiente;

	@GetMapping("/efectivo")
	public String pagoEfectivo() {
		return "/Views/SI/Venta/Pedido/pagarPedidoEfectivo";
	}

	@PostMapping("/efectivo")
	public String pagoEfectivo(@RequestParam("numeroOrden") String numeroOrden, RedirectAttributes attr) {
		this.pedido = pedidoService.buscarPorNumeroOrden(numeroOrden);
		
		if (this.pedido == null) {
			attr.addFlashAttribute("error", "No se ha encontrado ninguna orden con el numero ingresado!");
			return "redirect:/pedido/pago/efectivo";
		} else if (this.pedido.getEstado().equals("COBRADO")) {
			attr.addFlashAttribute("error", "La orden ingresada ya se encuentra cobrada!");
			return "redirect:/pedido/pago/efectivo";
		} else if (this.pedido.getEstado().equals("SOLICITADO")) {
			attr.addFlashAttribute("error", "La orden ingresada no esta preparada aún!");
			return "redirect:/pedido/pago/efectivo";
		}
		
		this.detalles = detallePedidoServ.buscarDetallesPorPedido(this.pedido);
		return "redirect:/pedido/pago/efectivo/trxn";
	}

	@GetMapping("/efectivo/trxn")
	public String pagoEfectivoTrxn(Model model) {
		this.iva = pedidoService.calcularIva(this.pedido.getSubtotal());

		if (this.pedido.getTotal() == 0 || iva == 0) {
			return "redirect:/pedido/pago/efectivo";
		}

		model.addAttribute("iva", this.iva);
		model.addAttribute("pedido", this.pedido);
		model.addAttribute("detalles", this.detalles);
		return "/Views/SI/Venta/Pedido/pagoEfectivo";
	}

	@PostMapping("/agregar-cliente")
	public String agregarCliente(@RequestParam("numeroDoc") String numeroDoc, RedirectAttributes attr,
			Principal principal) {
		User user = userService.buscarPorNumeroDoc(numeroDoc);
		User cajero = userService.buscarPorNumeroDoc(principal.getName());
		Cliente cliente = new Cliente();

		if (user == null) {
			attr.addFlashAttribute("warning", "No se ha encontrado ningun cliente!");
			return "redirect:/pedido/pago/efectivo/trxn";
		}
		
		if (user.getPasswordPuntos() == null) {
			attr.addFlashAttribute("error", "El cliente cliente ingresado no cuenta con la contraseña de kangrepuntos!");
			return "redirect:/pedido/pago/kangrepuntos";
		}

		if (user.getUsername().equals(cajero.getUsername())) {
			attr.addFlashAttribute("error",
					"Esta accion no es permitida, recuerde que esto puede llevar a graves sanciones!");
			return "redirect:/pedido/pago/efectivo/trxn";
		}

		cliente = clienteService.buscarPorUser(user);

		this.pedido.setCliente(cliente);
		attr.addFlashAttribute("success", "Cliente agregado correctamente!");
		return "redirect:/pedido/pago/efectivo/trxn";
	}

	@PostMapping("/efectivo/trxn")
	public String pagoEfectivoTrxn(@RequestParam("efectivo") Long efectivo, RedirectAttributes attr,
			Model model) {
		this.pago = new PagoEfectivo();

		if (efectivo < this.pedido.getTotal()) {
			attr.addFlashAttribute("error", "La cantidad ingresada no es suficiente para cubrir el pago!");
			return "redirect:/pedido/pago/efectivo/trxn";
		} else if (efectivo <= 0) { 
			attr.addFlashAttribute("error", "La cantidad ingresada es incorrecta!");
			return "redirect:/pedido/pago/efectivo/trxn";
		}

		if (this.redimidos.isEmpty()) {
			this.pedido.setTipoPago("EFECTIVO");
		} else {
			this.pedido.setTipoPago("EFECTIVO Y KANGREPUNTOS");
		}
		
		this.pago = pedidoService.pagarPedidoEfectivo(this.pedido, efectivo);
		
		int puntosRedimidos = 0;
		for (PuntoRedimido r : this.redimidos) {
			puntosRedimidos += r.getRedimidos();
		}
		
		if (!this.redimidos.isEmpty()) {
			redenService.redimirPedido(redimidos);
		}

		
		if (this.pedido.getCliente() != null) {

			try {
				MimeMessage message = mailSender.createMimeMessage();
				MimeMessageHelper helper = new MimeMessageHelper(message, true);

				String asunto = "Kangrepuntos 'La Hamburgueseria'";
				String mailContent = "<p><b>ASUNTO: </b>Kangrepuntos acumulados 'La Hamburgueseria'</p>";
				mailContent += "<p><b>TOTAL PEDIDO: </b>$" + this.pedido.getTotal() + "</p>";
				mailContent += "<p><b>PUNTOS ACUMULADOS: </b>" + this.pedido.getKangrepuntos() + "</p>";
				mailContent += "<p><b>PUNTOS REDIMIDOS: </b>" + puntosRedimidos + "</p>";
				mailContent += "<p><b>PUNTOS A VENCER: </b>"
						+ acumService.buscarUltimoVencer(this.pedido.getCliente()).getDisponibles() + "</p>";
				mailContent += "<p><b>FECHA VENCIMIENTO: </b>"
						+ acumService.buscarUltimoVencer(this.pedido.getCliente()).getFechaVencimiento() + "</p>";
				mailContent += "<p><b>TOTAL PUNTOS: </b>" + this.pedido.getCliente().getKangrepuntos() + "</p>";
				mailContent += "<hr><img src='cid:logoImage' width='200'/>";

				helper.setTo(this.pedido.getCliente().getUser().getEmail().toLowerCase());
				helper.setSubject(asunto);
				helper.setText(mailContent, true);

				ClassPathResource resource = new ClassPathResource("/static/img/LogoTexto.png");
				helper.addInline("logoImage", resource);

				mailSender.send(message);
			} catch (Exception e) {
				attr.addFlashAttribute("error", e.getMessage());
				model.addAttribute("iva", this.iva);
				model.addAttribute("pedido", this.pedido);
				model.addAttribute("detalles", this.detalles);
				return "redirect:/pedido/pago/efectivo/trxn";
			}
		}
		return "redirect:/pedido/pago/efectivo/resumen";
	}

	@GetMapping("/efectivo/resumen")
	public String efectivoResumen(Model model) {
		long restante = this.pedido.getTotal();
		long iva = pedidoService.calcularIva(this.pedido.getSubtotal());

		model.addAttribute("restante", restante);
		model.addAttribute("pedido", this.pedido);
		model.addAttribute("iva", iva);
		model.addAttribute("pago", this.pago);

		if (this.pedido.getTotal() == 0) {
			return "redirect:/pedido/";
		}

		if (this.pedido.getCliente() != null) {
			model.addAttribute("porVencer", acumService.buscarUltimoVencer(pedido.getCliente()));

			long puntosRedimidos = 0;

			for (PuntoRedimido redencion : this.redimidos) {
				puntosRedimidos += redencion.getRedimidos();
			}

			model.addAttribute("redimidos", puntosRedimidos);
			model.addAttribute("vlrRedimido", puntosRedimidos * 50);
		} else {
			model.addAttribute("puntosPerdidos", this.pedido.getSubtotal() / 800 + " kangrepuntos ");
		}
		return "/Views/SI/Venta/Pedido/resumenEfectivo";
	}

	@GetMapping("/kangrepuntos")
	public String pagoKangrepuntos() {
		return "/Views/SI/Venta/Pedido/pagarPedidoKangrepuntos";
	}
	
	@PostMapping("/kangrepuntos")
	public String pagoKangrepuntos(@RequestParam("numeroDoc") String numeroDoc, @RequestParam("numeroOrden") String numeroOrden, 
			Principal principal, RedirectAttributes attr) {
		User user = userService.buscarPorNumeroDoc(numeroDoc);
		User cajero = userService.buscarPorNumeroDoc(principal.getName());
	
		this.pedido = pedidoService.buscarPorNumeroOrden(numeroOrden);
		this.cliente = userService.buscarPorNumeroDoc(numeroDoc);
		this.pedido.setCliente(clienteService.buscarPorUser(this.cliente));
		this.detalles = detallePedidoServ.buscarDetallesPorPedido(this.pedido);
		this.redimidos = new ArrayList<>();
		
		if (this.cliente == null) {
			attr.addFlashAttribute("error", "No se encontraron resultados con el numero de documento ingresado!");
			return "redirect:/pedido/pago/kangrepuntos";
		} else if (user.getPasswordPuntos() == null) {
			attr.addFlashAttribute("error", "El cliente cliente ingresado no cuenta con la contraseña de kangrepuntos!");
			return "redirect:/pedido/pago/kangrepuntos";
		} else if (user.getUsername().equals(cajero.getUsername())) {
			attr.addFlashAttribute("error", "Esta accion no es permitida, recuerde que esto puede llevar a graves sanciones!");
			return "redirect:/pedido/pago/kangrepuntos";
		} else if (this.pedido == null) {
			attr.addFlashAttribute("error", "No se ha encontrado ninguna orden con el numero ingresado!");
			return "redirect:/pedido/pago/kangrepuntos";
		} else if (this.pedido.getEstado().equals("COBRADO")) {
			attr.addFlashAttribute("error", "La orden ingresada ya se encuentra cobrada!");
			return "redirect:/pedido/pago/kangrepuntos";
		} else if (this.pedido.getEstado().equals("SOLICITADO")) {
			attr.addFlashAttribute("error", "La orden ingresada no esta preparada aún!");
			return "redirect:/pedido/pago/kangrepuntos";
		} else if (this.pedido.getCliente().getKangrepuntos() == 0) {
			attr.addFlashAttribute("error", "El cliente ingresado no con kangrepuntos disponibles!");
			return "redirect:/pedido/pago/kangrepuntos";
		}
		
		return "redirect:/pedido/pago/kangrepuntos/trxn";
	}
	
	@GetMapping("/kangrepuntos/trxn")
	public String kangrepuntosTrxn(Model model) {
		if (this.pedido.getNumeroOrden() == null) {
			return "redirect:/pedido/pago/kangrepuntos";
		}
		
		this.vlrTotalEnPuntos = (int) (this.pedido.getTotal() / 50);
		
		System.out.println("REDIMIDOS: "+this.redimidos.size());
		model.addAttribute("pedido", this.pedido);
		model.addAttribute("totalKP", this.vlrTotalEnPuntos);
		model.addAttribute("redimidos", this.redimidos);
		return "/Views/SI/Fidelizacion/Kangrepuntos/Redencion/redencionIndex";
	}

	@PostMapping("/kangrepuntos/total")
	public String pagoTotalKangrePuntos(@RequestParam("pass") String pass, RedirectAttributes attr) {
	
		this.iva = pedidoService.calcularIva(this.pedido.getSubtotal());
		this.pedido.setTotal(this.pedido.getSubtotal() + this.iva);
		this.vlrTotalEnPuntos = (int) (this.pedido.getTotal() / 50);

		if (this.pedido.getCliente().getKangrepuntos() < this.vlrTotalEnPuntos) {
			attr.addFlashAttribute("error", "Cantidad de puntos insuficientes!");
			return "redirect:/pedido/pago/kangrepuntos";
		}

		if (!userService.checkPassPuntosMatch(this.pedido.getCliente().getUser(), pass)) {
			attr.addFlashAttribute("error", "Clave incorrecta");
			return "redirect:/pedido/pago/kangrepuntos";
		}
		
		this.redimidos = redenService.redimirTodo(this.pedido);

		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true);

			String asunto = "Kangrepuntos 'La Hamburgueseria'";
			String mailContent = "<p><b>ASUNTO: </b>Kangrepuntos redimidos 'La Hamburgueseria'</p>";
			mailContent += "<p><b>TOTAL PEDIDO: </b>$" + this.pedido.getTotal() + "</p>";
			mailContent += "<p><b>PUNTOS ACUMULADOS: </b>" + this.pedido.getKangrepuntos() + "</p>";
			mailContent += "<p><b>PUNTOS REDIMIDOS: </b>" + this.pedido.getTotal() / 50 + "</p>";
			mailContent += "<p><b>PUNTOS A VENCER: </b>"
					+ acumService.buscarUltimoVencer(this.pedido.getCliente()).getDisponibles() + "</p>";
			mailContent += "<p><b>FECHA VENCIMIENTO: </b>"
					+ acumService.buscarUltimoVencer(this.pedido.getCliente()).getFechaVencimiento() + "</p>";
			mailContent += "<p><b>TOTAL PUNTOS: </b>" + this.pedido.getCliente().getKangrepuntos() + "</p>";
			mailContent += "<hr><img src='cid:logoImage' width='200'/>";

			helper.setTo(this.pedido.getCliente().getUser().getEmail().toLowerCase());
			helper.setSubject(asunto);
			helper.setText(mailContent, true);

			ClassPathResource resource = new ClassPathResource("/static/img/LogoTexto.png");
			helper.addInline("logoImage", resource);

			mailSender.send(message);
		} catch (Exception e) {
			attr.addFlashAttribute("error", e.getMessage());
			return "redirect:/pedido/";
		}
		
		return "redirect:/pedido/pago/kangrepuntos/resumen";
	}

	@GetMapping("/kangrepuntos/cancelar")
	public String cancelarRedencion() {
		this.redimidos = new ArrayList<>();
		//this.pedido.getCliente().setKangrepuntos(acumService.calcularTotalPuntos(this.pedido.getCliente()));
		this.pedido.setSubtotal(pedidoService.calcularSubtotalPedido(this.detalles));
		this.iva = pedidoService.calcularIva(this.pedido.getSubtotal());
		this.pedido.setTotal(this.pedido.getSubtotal() + this.iva);
		this.vlrTotalEnPuntos = (int) (this.pedido.getTotal() / 50);
		return "redirect:/pedido/pago/kangrepuntos";
	}

	@GetMapping("/kangrepuntos/resumen")
	public String resumenKangrepuntos(Model model) {
		long totalRedencion = 0;
		for (PuntoRedimido redencion : this.redimidos) {
			totalRedencion += redencion.getRedimidos();
		}

		model.addAttribute("iva", this.iva);
		model.addAttribute("redimidos", totalRedencion);
		model.addAttribute("pedido", this.pedido);
		model.addAttribute("vlrRedimido", totalRedencion * 50);
		model.addAttribute("porVencer", acumService.buscarUltimoVencer(pedido.getCliente()));
		return "/Views/SI/Fidelizacion/Kangrepuntos/Redencion/resumenRedencion";
	}

	@PostMapping("/kangrepuntos/parcial")
	public String pagoParcialKangrePuntos(@RequestParam("cantidad") int cantidad, @RequestParam("pass") String pass,
			RedirectAttributes attr) {
		this.vlrTotalEnPuntos = (int) (pedido.getTotal() / 50);
		this.puntosPendientes = vlrTotalEnPuntos - cantidad;
		this.saldoPendiente = puntosPendientes * 50;
		this.pedido.setTotal(saldoPendiente);

		if (cantidad > this.pedido.getCliente().getKangrepuntos()) {
			attr.addFlashAttribute("error", "Cantidad de puntos insuficientes!");
			return "redirect:/pedido/pago/kangrepuntos/trxn";
		}

		if (!userService.checkPassPuntosMatch(this.pedido.getCliente().getUser(), pass)) {
			attr.addFlashAttribute("error", "La clave ingresada es incorrecta!");
			return "redirect:/pedido/pago/kangrepuntos/trxn";
		}

		if (cantidad >= this.vlrTotalEnPuntos) {
			return pagoTotalKangrePuntos(pass, attr);
		}
		
		this.redimidos = redenService.redimirParte(this.pedido, cantidad);
		this.pedido.getCliente().setKangrepuntos(this.pedido.getCliente().getKangrepuntos() - cantidad);
		
		attr.addFlashAttribute("success", "Kangrepuntos agregados correctamente a la lista de redencion!");
		return "redirect:/pedido/pago/kangrepuntos/trxn";
	}

}
