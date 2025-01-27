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
import com.KangreSystem.models.entity.User;
import com.KangreSystem.models.service.IPaisService;
import com.KangreSystem.models.service.IUserService;

@Controller
@Secured("ROLE_USER")
@RequestMapping("/mi-perfil")
public class PerfilController {
	
	@Autowired
	private IUserService userService;
	
	@Autowired
	private IPaisService paisService;
	
	@Autowired
	private BCryptPasswordEncoder passEncoder;

	@GetMapping("/datos-basicos")
	public String datosBasicos(Principal principal, Model model) {
		User user = userService.buscarPorNumeroDoc(principal.getName());
		
		model.addAttribute("usuario", user);
		model.addAttribute("paises", paisService.listarPaises());
		return "/Views/SI/Perfil/miPerfil";
	}
	
	@PostMapping("/datos-basicos")
	public String datosBasicos(@ModelAttribute User user, RedirectAttributes attr, Model model) {
		User userAux = userService.buscarPorNumeroDoc(user.getNumeroDoc());
		
		if (userService.existsByEmail(user) && !userAux.getEmail().equals(user.getEmail().toUpperCase())) {
			model.addAttribute("usuario", user);
			model.addAttribute("paises", paisService.listarPaises());
			model.addAttribute("error", "El correo electronico ingresado ya esta siento utilizado por otro usuario!");
			return "/Views/SI/Perfil/miPerfil";
		}
		
		userService.guardar(user);
		attr.addFlashAttribute("success", "Datos actualizados correctamente!");
		return "redirect:/";
	}
	
	@GetMapping("/cambiar-contrasenia")
	public String cambiarContrasenia(Principal principal, Model model) {
		User user = userService.buscarPorNumeroDoc(principal.getName());
		
		model.addAttribute("usuario", user);
		model.addAttribute("paises", paisService.listarPaises());
		return "/Views/SI/Perfil/cambiarPassword";
	}
	
	@PostMapping("/cambiar-contrasenia")
	public String cambiarContrasenia(@ModelAttribute User user, @RequestParam("oldPassModal") String oldPassModal,
			RedirectAttributes attr) {
		User userAux = userService.buscarPorNumeroDoc(user.getNumeroDoc());
		
		if (!passEncoder.matches(oldPassModal, userAux.getPassword())) {
			attr.addFlashAttribute("error", "La contraseña ingresada con coincide con la registrada actualmente!");
			return "redirect:/mi-perfil/cambiar-contrasenia";
		} else if (!user.getPassword().equals(user.getConfirmPassword())) {
			attr.addFlashAttribute("error", "Las contraseñas ingresadas no coinciden!");
			return "redirect:/mi-perfil/cambiar-contrasenia";
		} else if (passEncoder.matches(user.getPassword(), userAux.getPassword())) {
			attr.addFlashAttribute("error", "La nueva contraseña debe ser diferente a la contraseña actual!");
			return "redirect:/mi-perfil/cambiar-contrasenia";
		}
		
		String encodedPass = passEncoder.encode(user.getPassword());
		user.setPassword(encodedPass);
		
		userService.guardar(user);
		attr.addFlashAttribute("success", "Datos actualizados correctamente!");
		return "redirect:/";
	}
	
}
