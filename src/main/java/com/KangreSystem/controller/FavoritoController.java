package com.KangreSystem.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.repository.query.Param;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.KangreSystem.models.entity.Cliente;
import com.KangreSystem.models.entity.Favorito;
import com.KangreSystem.models.entity.PuntoAcumulado;
import com.KangreSystem.models.entity.PuntoTransferido;
import com.KangreSystem.models.entity.User;
import com.KangreSystem.models.service.IClienteService;
import com.KangreSystem.models.service.IFavoritoService;
import com.KangreSystem.models.service.IPuntoAcumuladoService;
import com.KangreSystem.models.service.IPuntoTransferidoService;
import com.KangreSystem.models.service.IUserService;

@Controller
@Secured("ROLE_USER")
@RequestMapping("/favorito")
public class FavoritoController {
	
	private Cliente clienteLocal;
	
	private PuntoTransferido transferencia = new PuntoTransferido();
	
	private List<Favorito> favoritos = new ArrayList<>();
	
	@Autowired
	private IFavoritoService favoritoService;
	
	@Autowired
	private IUserService userService;
	
	@Autowired
	private IClienteService clienteService;
	
	@Autowired
	private IPuntoAcumuladoService acumService;
	
	@Autowired
	private IPuntoTransferidoService transService;
	
	@Autowired
	private JavaMailSender mailSender;
	
	@GetMapping("/")
	public String nuevo(Model model, Principal principal) {
		this.clienteLocal = new Cliente();
		User userLocal = userService.buscarPorNumeroDoc(principal.getName());
		this.clienteLocal = clienteService.buscarPorUser(userLocal);
		this.favoritos = favoritoService.buscarPorCliente(this.clienteLocal);
		model.addAttribute("favoritos", this.favoritos);
		model.addAttribute("size", this.favoritos.size());
		return "Views/SI/Fidelizacion/Favorito/nuevoFavorito";
	}
	
	@PostMapping("/")
	public String nuevo(@RequestParam("numeroDoc") String numeroDoc, RedirectAttributes attr, Principal principal) {
		System.out.println("NUMERO DOC: "+numeroDoc);
		Favorito favorito = new Favorito();
		User user = userService.buscarPorNumeroDoc(numeroDoc);
		
		if (user == null) {
			attr.addFlashAttribute("error", "El usuario que estas intentando agregar como favorito no existe!");
			return "redirect:/favorito/";
		}
		
		Cliente clienteAux = clienteService.buscarPorUser(user);
		favorito.setCliente(clienteService.buscarPorId(this.clienteLocal.getIdCliente()));
		favorito.setFavorito(clienteService.buscarPorId(clienteAux.getIdCliente()));
		
		for (Favorito fav : favoritos) {
			if (fav.getFavorito().getIdCliente().equals(favorito.getFavorito().getIdCliente())) {
				attr.addFlashAttribute("error", "El usuario que estas intentando ya esta agregado!");
				return "redirect:/favorito/";
			}
		}
		favoritoService.guardar(favorito);
		attr.addFlashAttribute("success", "Usuario agregado correctamente!");
		return "redirect:/favorito/";
	}
	
	@GetMapping("/quitar/{favorito}")
	public String quitar(@PathVariable("favorito") long favorito, RedirectAttributes attr) {
		User u = userService.buscarPorId(favorito);
		Cliente c = clienteService.buscarPorUser(u);
		Favorito fav = favoritoService.buscarPorFavorito(c);
		favoritoService.eliminar(fav.getIdFavorito());
		attr.addFlashAttribute("warning", "Usuario removido correctamente!");
		return "redirect:/favorito/";
	}

	@GetMapping("/transferencia/nueva")
	public String nuevaTranferencia(Model model, Principal principal) {
		User user = userService.buscarPorNumeroDoc(principal.getName());
		this.clienteLocal = clienteService.buscarPorUser(user);
		
		List<PuntoAcumulado> acumulados = acumService.buscarPorCliente(this.clienteLocal);
		this.favoritos = favoritoService.buscarPorCliente(this.clienteLocal);
		List<PuntoAcumulado> acumuladosDisponibles = new ArrayList<>();
		
		for (PuntoAcumulado acumulado : acumulados) {
			if (acumulado.getDisponibles() > 0) {
				acumuladosDisponibles.add(acumulado);
			}
		}
		
		model.addAttribute("acumulados", acumuladosDisponibles);
		model.addAttribute("puntoTransferido", new PuntoTransferido());
		model.addAttribute("favoritos", this.favoritos);
		return "Views/SI/Fidelizacion/Kangrepuntos/Transferencia/transferenciaIndex";
	}
	
	@PostMapping("/transferencia/nueva")
	public String nuevaTransferencia(@ModelAttribute PuntoTransferido trxnTransferencia, RedirectAttributes attr,
			@Param("idTrxnAcumMF") Long idTrxnAcumMF) {
		Favorito favorito = favoritoService.buscarPorId(trxnTransferencia.getFavorito().getIdFavorito());
		PuntoAcumulado acumulado = acumService.buscarPorId(idTrxnAcumMF);
		
		this.transferencia.setCliente(this.clienteLocal);
		this.transferencia.setFavorito(favorito);
		this.transferencia.setFecha(new Date());
		this.transferencia.setHora(new Date());
		this.transferencia.setAcumulado(acumulado);	
		
		attr.addFlashAttribute("success", "Favorito agregado correctamente!");
		attr.addFlashAttribute("transferencia", this.transferencia);
		return "redirect:/favorito/transferencia/nueva";
	}
	
	@GetMapping("/transferencia/transferir")
	public String transferir(RedirectAttributes attr) {
		if (this.transferencia.getAcumulado() == null) {
			attr.addFlashAttribute("error", "No hay ninguna transaccion agregada!");
			return "redirect:/favorito/transferencia/nueva";
		}
		
//		if (!transService.validTranferencia(this.transferencia)) {
//			attr.addFlashAttribute("error", "No se puede hacer mas de una transferencia diaria!");
//			return "redirect:/favorito/transferencia/nueva";
//		}
		
		int puntosTransferidos = this.transferencia.getAcumulado().getDisponibles();
		String origen = this.transferencia.getFavorito().getCliente().getUser().getNombres()+" "+this.transferencia.getFavorito().getCliente().getUser().getApellidos();
		String destinatario = this.transferencia.getFavorito().getFavorito().getUser().getNombres()+" "+this.transferencia.getFavorito().getFavorito().getUser().getApellidos();
		String correoDestinatario = this.transferencia.getFavorito().getFavorito().getUser().getEmail().toLowerCase();
		String correoCliente = this.transferencia.getFavorito().getCliente().getUser().getEmail().toLowerCase();
		
		transService.transferir(this.transferencia);

		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true);

			String asunto = "Kangrepuntos 'La Hamburgueseria'";
			String mailContent = "<p><b>ASUNTO: </b>Transferencia de Kangrepuntos existosa</p>";
			mailContent += "<p><b>PUNTOS TRANSFERIDOS: </b>" + puntosTransferidos + "</p>";
			mailContent += "<p><b>DESTINATARIO: </b>" + destinatario + "</p>";
			mailContent += "<hr><img src='cid:logoImage' width='200'/>";

			helper.setTo(correoCliente);
			helper.setSubject(asunto);
			helper.setText(mailContent, true);

			ClassPathResource resource = new ClassPathResource("/static/img/LogoTexto.png");
			helper.addInline("logoImage", resource);

			mailSender.send(message);
			
			MimeMessage message1 = mailSender.createMimeMessage();
			MimeMessageHelper helper1 = new MimeMessageHelper(message1, true);

			String asunto1 = "Kangrepuntos 'La Hamburgueseria'";
			String mailContent1 = "<p><b>ASUNTO: </b>Transferencia de Kangrepuntos existosa</p>";
			mailContent1 += "<p><b>PUNTOS RECIBIDOS: </b>" + puntosTransferidos + "</p>";
			mailContent1 += "<p><b>ORIGEN: </b>" + origen + "</p>";
			mailContent1 += "<hr><img src='cid:logoImage' width='200'/>";

			helper1.setTo(correoDestinatario);
			helper1.setSubject(asunto1);
			helper1.setText(mailContent1, true);
			
			helper1.addInline("logoImage", resource);

			mailSender.send(message1);
		} catch (Exception e) {
			attr.addFlashAttribute("error", e.getMessage());
			return "redirect:/transferencia/nueva";
		}
		
		
		this.transferencia = new PuntoTransferido();
		attr.addFlashAttribute("success", "Favorito agregado correctamente!");
		return "redirect:/favorito/transferencia/transferir/success";
	}
	
	@GetMapping("/transferencia/transferir/success")
	public String transferenciaSuccess() {
		return "Views/SI/Fidelizacion/Kangrepuntos/Transferencia/successTransferencia";
	}
	
	@GetMapping("/transferencia/cancelar")
	public String cancelarTransferencia() {
		this.transferencia = new PuntoTransferido();
		return "redirect:/favorito/transferencia/nueva";
	}
	
}
