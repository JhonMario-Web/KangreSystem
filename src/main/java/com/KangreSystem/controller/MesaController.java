package com.KangreSystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.KangreSystem.models.service.IMesaService;

@Controller
@RequestMapping("/mesa")
public class MesaController {
	
	@Autowired
	private IMesaService mesaService;
	
	@GetMapping("/")
	@Secured({"ROLE_ADMIN", "ROLE_CAJERO", "ROLE_COCINERO"})
	public String mesaIndex(Model model) {
		model.addAttribute("mesas", mesaService.listar());
		return "/Views/SI/Venta/Mesa/mesasIndex";
	}

}
