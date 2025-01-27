package com.KangreSystem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.Scheduled;
import com.KangreSystem.models.service.IInventarioService;
import com.KangreSystem.models.service.IOfertaService;
import com.KangreSystem.models.service.IPedidoService;
import com.KangreSystem.models.service.IPuntoAcumuladoService;

@SpringBootApplication
public class KangreSystemSpringbootApplication {
	
	@Autowired
	private IOfertaService ofertaService;
	
	@Autowired
	private IPedidoService pedidoService;
	
	@Autowired
	private IPuntoAcumuladoService acumService;
	
	@Autowired
	private IInventarioService inventarioService;
	
	/*
	@Autowired
	private BCryptPasswordEncoder passEncoder;*/
	
	
	public static void main(String[] args) {
		SpringApplication.run(KangreSystemSpringbootApplication.class, args);
	}

	/*
	@Override
	public void run(String... args) throws Exception {
		String pass = "1019133189";
		System.out.println(passEncoder.encode(pass));
		
	}*/
	
	
	@Scheduled(cron="* 30 1 * * 1-7")
	private void actualizarOfertas() throws InterruptedException {
		ofertaService.actualizarOfertas();
	}
	
	@Scheduled(cron="* 35 1 * * 1-7")
	private void ordenSolicitadaVencida() throws InterruptedException {
		pedidoService.ordenSolicitadaVencida();
	}
	
	@Scheduled(cron="* 40 1 * * 1-7")
	private void actualizarPuntos() throws InterruptedException {
		acumService.actualizarKangrePuntos();
	}
	
	@Scheduled(cron="* 45 1 * * 1-7")
	private void actualizarInventario() throws InterruptedException {
		inventarioService.actualizarInventario();
	}
	
}
