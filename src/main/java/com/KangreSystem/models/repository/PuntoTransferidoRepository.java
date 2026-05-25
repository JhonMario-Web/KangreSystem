package com.KangreSystem.models.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.KangreSystem.models.entity.Cliente;
import com.KangreSystem.models.entity.PuntoTransferido;

@Repository
public interface PuntoTransferidoRepository extends CrudRepository<PuntoTransferido, Long>{

	public List<PuntoTransferido> findByCliente(Cliente cliente);
	
}
