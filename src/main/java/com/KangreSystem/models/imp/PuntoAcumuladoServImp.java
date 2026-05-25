package com.KangreSystem.models.imp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.KangreSystem.models.entity.Cliente;
import com.KangreSystem.models.entity.Pedido;
import com.KangreSystem.models.entity.PuntoAcumulado;
import com.KangreSystem.models.repository.PuntoAcumuladoRepository;
import com.KangreSystem.models.service.IClienteService;
import com.KangreSystem.models.service.IPuntoAcumuladoService;

@Service
public class PuntoAcumuladoServImp implements IPuntoAcumuladoService {
	
	@Autowired
	private PuntoAcumuladoRepository acumRepository;
	
	@Autowired
	private IClienteService clienteService;
	
	@Autowired
	private IPuntoAcumuladoService acumService;

	@Override
	public List<PuntoAcumulado> listar() {
		return (List<PuntoAcumulado>) acumRepository.findAll();
	}

	@Override
	public void eliminar(Long idTrxn) {
		acumRepository.deleteById(idTrxn);
	}

	
	@SuppressWarnings("deprecation")
	@Override
	public Date calcularFechaVencimiento(Date fechaTrxn) {
		Date fechaVencimiento = new Date();
		
		int diaTrxn = fechaTrxn.getDate();
		int mesTrxn = fechaTrxn.getMonth();
		int anioTrxn = fechaTrxn.getYear();
		
		fechaVencimiento.setDate(diaTrxn);
		fechaVencimiento.setMonth(mesTrxn);
		fechaVencimiento.setYear(anioTrxn + 1);
		
		return fechaVencimiento;
	}

	@Override
	public PuntoAcumulado buscarPorId(Long idTrxn) {
		return acumRepository.findById(idTrxn).orElse(null);
	}

	@Override
	public List<PuntoAcumulado> buscarPorCliente(Cliente cliente) {
		return acumRepository.findByCliente(cliente);
	}

	@Override
	public int calcularTotalPuntos(Cliente cliente) {
		int total = 0;
		List<PuntoAcumulado> acumulados = acumRepository.findByCliente(cliente);
		for (PuntoAcumulado punto : acumulados) {
			total += punto.getDisponibles();
		}
		
		return total;
	}

	@Override
	public void guardarAcumulados(List<PuntoAcumulado> trxns) {
		acumRepository.saveAll(trxns);
	}

	@Override
	public void guardar(PuntoAcumulado trxn) {
		acumRepository.save(trxn);
	}

	@Override
	public PuntoAcumulado buscarUltimoVencer(Cliente cliente) {
		List<PuntoAcumulado> acumulados = acumRepository.findByCliente(cliente);
		List<PuntoAcumulado> acumuladosAux = new ArrayList<>();
		PuntoAcumulado aVencer = null;
		
		for (PuntoAcumulado punto : acumulados) {
			if (punto.getDisponibles() > 0) {
				acumuladosAux.add(punto);
			}
		}
		
		if (!acumuladosAux.isEmpty()) {
			aVencer = acumuladosAux.get(0);
		}
		
		return aVencer;
	}

	@Override
	public List<PuntoAcumulado> buscarTrxnDisponibles(Cliente cliente) {
		List<PuntoAcumulado> acumulados = acumRepository.findByCliente(cliente);
		List<PuntoAcumulado> disponibles = new ArrayList<>();
		
		if (!acumulados.isEmpty()) {
			for (PuntoAcumulado punto : acumulados) {
				if (punto.getDisponibles() > 0) {
					disponibles.add(punto);
				}
			}
		}
		return disponibles;
	}

	@Override
	@Transactional
	public void acumularPuntos(Pedido pedido) {
		PuntoAcumulado acumulado = new PuntoAcumulado();
		Cliente cliente = pedido.getCliente();
		
		acumulado.setCliente(cliente);
		acumulado.setPedido(pedido);
		acumulado.setFecha(new Date());
		acumulado.setHora(new Date());
		acumulado.setAcumulados(pedido.getKangrepuntos());
		acumulado.setDisponibles(pedido.getKangrepuntos());
		acumulado.setTotal(pedido.getKangrepuntos() + cliente.getKangrepuntos());
		acumulado.setFechaVencimiento(calcularFechaVencimiento(acumulado.getFecha()));
		
		acumRepository.save(acumulado);
		
		int total = calcularTotalPuntos(cliente);
		cliente.setKangrepuntos(total);
		
		clienteService.guardar(cliente);
	}

	@Override
	public List<PuntoAcumulado> buscarPorPedido(Pedido pedido) {
		return acumRepository.findByPedido(pedido);
	}

	@Override
	public void eliminarLista(List<PuntoAcumulado> acumulados) {
		acumRepository.deleteAll(acumulados);
	}

	@Override
	@Transactional
	public void actualizarKangrePuntos() {
		List<Cliente> clientes = clienteService.listar();
		
		for (Cliente cliente : clientes) {
			List<PuntoAcumulado> acumulados = acumService.buscarPorCliente(cliente);
			
			for (PuntoAcumulado acumulado : acumulados) {
				if (acumulado.getDisponibles() > 0) {
					cliente.setKangrepuntos(cliente.getKangrepuntos() + acumulado.getDisponibles());
				}
			}
			
			clienteService.guardar(cliente);
		}
		
	}

}
