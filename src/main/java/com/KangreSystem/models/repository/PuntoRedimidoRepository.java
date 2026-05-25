package com.KangreSystem.models.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.KangreSystem.models.entity.Cliente;
import com.KangreSystem.models.entity.PuntoRedimido;

@Repository
public interface PuntoRedimidoRepository extends CrudRepository<PuntoRedimido, Long> {

	public List<PuntoRedimido> findByCliente(Cliente cliente);
}
