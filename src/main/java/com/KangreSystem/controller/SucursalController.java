package com.KangreSystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.KangreSystem.models.service.IRestauranteService;

@Controller
@RequestMapping("/sucursal")
public class SucursalController {
	
	@Autowired
	private IRestauranteService restauranteServ;
	
	@GetMapping("/")
	public String listar(Model model) {
		model.addAttribute("titulo", "Sucursales de 'LA HAMBURGUESERIA'");
		model.addAttribute("sucursales", restauranteServ.listar());
		return "/Views/Restaurante/sucursales";
	}
	
	@GetMapping("/bogota")
	public String bogota(Model model) {
		model.addAttribute("titulo", "Sucursales de 'LA HAMBURGUESERIA' | Bogotá");
		model.addAttribute("sucursales", restauranteServ.buscarPorCiudad("BOGOTA"));
		return "/Views/Restaurante/sucursales";
	}
	
	@GetMapping("/chia")
	public String chia(Model model) {
		model.addAttribute("titulo", "Sucursales de 'LA HAMBURGUESERIA' | Chía");
		model.addAttribute("sucursales", restauranteServ.buscarPorCiudad("CHIA"));
		return "/Views/Restaurante/sucursales";
	}

}
