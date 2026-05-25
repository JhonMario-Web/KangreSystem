package com.KangreSystem.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {

	@GetMapping("/login")
	public String login(@RequestParam(value = "error", required = false) String error, Model model,
			Principal principal, RedirectAttributes atribute, @RequestParam(value = "logout", required = false) String logout) {
		model.addAttribute("titulo", "Iniciar sesión");
		if (error != null) {
			model.addAttribute("error", "Usuario y/o contraseña son incorrectos");
		}
		if(principal != null) {
			atribute.addFlashAttribute("warning", "Usted ya ha iniciado sesion con el usuario ");
			return "redirect:/";
		}
		if (logout != null) {
			model.addAttribute("success", "Ha finalizado sesion correctamente");
		}
		return "login";
	}
}
