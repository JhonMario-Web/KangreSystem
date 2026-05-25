package com.KangreSystem.models.imp;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.KangreSystem.models.entity.Cliente;
import com.KangreSystem.models.entity.PuntoAcumulado;
import com.KangreSystem.models.entity.PuntoTransferido;
import com.KangreSystem.models.repository.PuntoTransferidoRepository;
import com.KangreSystem.models.service.IClienteService;
import com.KangreSystem.models.service.IPuntoAcumuladoService;
import com.KangreSystem.models.service.IPuntoTransferidoService;

@Service
public class PuntoTransferidoServImp implements IPuntoTransferidoService {
	
	@Autowired
	private PuntoTransferidoRepository trxnRepository;
	
	@Autowired
	private IPuntoAcumuladoService acumService;
	
	@Autowired
	private IClienteService clienteService;

	@Override
	public List<PuntoTransferido> listar() {
		return (List<PuntoTransferido>) trxnRepository.findAll();
	}

	@Override
	public void guardar(PuntoTransferido trxn) {
		trxnRepository.save(trxn);
	}

	@Override
	public void eliminar(Long idTrxn) {
		trxnRepository.deleteById(idTrxn);
	}

	@Override
	public PuntoTransferido buscarPorId(Long idTrxn) {
		return trxnRepository.findById(idTrxn).orElse(null);
	}

	@Override
	@Transactional
	public void transferir(PuntoTransferido trxn) {
		PuntoAcumulado acumuladoToSave = new PuntoAcumulado();
		Cliente favorito = trxn.getFavorito().getFavorito();
		Cliente cliente = trxn.getCliente();
		PuntoAcumulado acumulado = trxn.getAcumulado();
		int puntosToTransfer = acumulado.getDisponibles();
		
		acumuladoToSave.setAcumulados(puntosToTransfer);
		acumuladoToSave.setCliente(favorito);
		acumuladoToSave.setFecha(new Date());
		acumuladoToSave.setHora(new Date());
		acumuladoToSave.setPedido(trxn.getAcumulado().getPedido());
		acumuladoToSave.setFechaVencimiento(acumulado.getFechaVencimiento());
		acumuladoToSave.setDisponibles(puntosToTransfer);
		
		cliente.setKangrepuntos(cliente.getKangrepuntos() - puntosToTransfer);
		acumuladoToSave.setTotal(cliente.getKangrepuntos());
		
		trxn.setTransferidos(puntosToTransfer);
		trxn.setTotal(cliente.getKangrepuntos());
		trxn.setFecha(new Date());
		trxn.setHora(new Date());
		
		trxnRepository.save(trxn);
		
		acumulado.setDisponibles(0);
		acumService.guardar(acumulado);
		
		cliente.setKangrepuntos(acumService.calcularTotalPuntos(cliente));
		clienteService.guardar(cliente);
		
		acumService.guardar(acumuladoToSave);
		
		favorito.setKangrepuntos(acumService.calcularTotalPuntos(favorito));
		clienteService.guardar(favorito);
		
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean validTranferencia(PuntoTransferido trxn) {
		List<PuntoTransferido> transferencias = trxnRepository.findByCliente(trxn.getCliente());
		Date fechaHoy = new Date();
		
		int diaHoy = fechaHoy.getDate();
		int mesHoy = fechaHoy.getMonth();
		int anioHoy = fechaHoy.getYear() + 1900;
		
		int diaTransferencia = 0;
		int mesTransferencia = 0;
		int anioTransferencia = 0;
		
		for (PuntoTransferido transferencia : transferencias) {
			diaTransferencia = transferencia.getFecha().getDate();
			mesTransferencia = transferencia.getFecha().getMonth();
			anioTransferencia = transferencia.getFecha().getYear() + 1900;
			
			if (diaHoy == diaTransferencia && mesHoy == mesTransferencia && anioHoy == anioTransferencia) {
				return false;
			}
		}
		return true;
	}

	@Override
	public List<PuntoTransferido> buscarPorCliente(Cliente cliente) {
		return trxnRepository.findByCliente(cliente);
	}

}
