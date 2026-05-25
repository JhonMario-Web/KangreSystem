package com.KangreSystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.KangreSystem.models.service.IVencimientoLoteServ;

@Controller
public class HomeController {
	
	@Autowired
	private IVencimientoLoteServ vencimientoService;
	
	@GetMapping({"/", "/index", "/home"})
	public String index(Model model) {
		model.addAttribute("vencimientos", vencimientoService.vencenEnQuinceDias());
		return "index";
	}
	
	@GetMapping("/quienes-somos")
	public String quienesSomos() {
		return "Views/Index/quienesSomos";
	}

}
