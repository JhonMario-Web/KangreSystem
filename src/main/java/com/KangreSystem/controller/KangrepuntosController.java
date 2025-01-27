package com.KangreSystem.controller;

import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.KangreSystem.models.entity.Cliente;
import com.KangreSystem.models.entity.User;
import com.KangreSystem.models.service.IClienteService;
import com.KangreSystem.models.service.IFavoritoService;
import com.KangreSystem.models.service.IPuntoAcumuladoService;
import com.KangreSystem.models.service.IPuntoRedimidoService;
import com.KangreSystem.models.service.IPuntoTransferidoService;
import com.KangreSystem.models.service.IUserService;

@Controller
@Secured("ROLE_USER")
@RequestMapping("/kangrepuntos")
public class KangrepuntosController {
	
	private User localUser;
	
	private Cliente localCliente;
	
	@Autowired
	private IUserService userService;
	
	@Autowired
	private IClienteService clienteService;
	
	@Autowired
	private IFavoritoService favoritoService;
	
	@Autowired
	private IPuntoAcumuladoService acumService;
	
	@Autowired
	private IPuntoRedimidoService redenService;
	
	@Autowired
	private IPuntoTransferidoService transfService;
	
	@Autowired
	private BCryptPasswordEncoder passEncoder;

	@GetMapping("/detalles")
	public String detalles(Model model, Principal principal) {
		this.localUser = userService.buscarPorNumeroDoc(principal.getName());
		this.localCliente = clienteService.buscarPorUser(this.localUser);
		
		model.addAttribute("aVencer", acumService.buscarUltimoVencer(this.localCliente));			
		model.addAttribute("cliente", this.localCliente);
		model.addAttribute("favoritos", favoritoService.contarPorCliente(this.localCliente));
		model.addAttribute("acumulados", acumService.buscarPorCliente(this.localCliente));
		model.addAttribute("redenciones", redenService.buscarPorCliente(this.localCliente));
		model.addAttribute("transferencias", transfService.buscarPorCliente(this.localCliente));
		model.addAttribute("disponibles", acumService.buscarTrxnDisponibles(this.localCliente));
		return "Views/SI/Fidelizacion/Kangrepuntos/detallesKangrepuntos";
	}
	
	@GetMapping("/contrasenia")
	public String contrasenia(Model model, Principal principal) {
		User user = userService.buscarPorNumeroDoc(principal.getName());
		
		model.addAttribute("usuario", user);
		return "Views/SI/Fidelizacion/Kangrepuntos/Seguridad/cambiarPasswordKangrepuntos";
	}
	
	@PostMapping("/contrasenia")
	public String contrasenia(@ModelAttribute User user, @RequestParam("actualPassModal") String actualPassModal, RedirectAttributes attr) {
		User userAux = userService.buscarPorNumeroDoc(user.getNumeroDoc());
		
		if (!actualPassModal.isEmpty()) {
			if (!passEncoder.matches(actualPassModal, userAux.getPasswordPuntos()) && !userAux.getPasswordPuntos().isEmpty()) {
				attr.addFlashAttribute("error", "La contraseña ingresada no coincide con la actual registrada!");
				return "redirect:/kangrepuntos/contrasenia";
			}
		}
		
		if (passEncoder.matches(user.getPasswordPuntos(), userAux.getPasswordPuntos())) {
			attr.addFlashAttribute("error", "Las nueva contraseña debe ser diferente a la contraseña actual!");
			return "redirect:/kangrepuntos/contrasenia";
		}
		
		if (user.getPasswordPuntos().isEmpty() || user.getConfirmPasswordPuntos().isEmpty()) {
			attr.addFlashAttribute("error", "Complete el campo nueva contraseña y confirmar contraseña!");
			return "redirect:/kangrepuntos/contrasenia";
		}
		
		if (!user.getPasswordPuntos().equals(user.getConfirmPasswordPuntos())) {
			attr.addFlashAttribute("error", "Las contraseñas ingresadas no coinciden!");
			return "redirect:/kangrepuntos/contrasenia";
		}
		
		String encodedPass = passEncoder.encode(user.getPasswordPuntos());
		user.setPasswordPuntos(encodedPass);
		
		userService.guardar(user);
		attr.addFlashAttribute("success", "Contraseña de kangrepuntos actualizada correctamente!");
		return "redirect:/";
	}
	
	
}
