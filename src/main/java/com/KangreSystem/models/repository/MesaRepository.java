package com.KangreSystem.models.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.KangreSystem.models.entity.Mesa;

@Repository
public interface MesaRepository extends CrudRepository<Mesa, Long> {
	
	public Mesa findByNumeroMesa(String numeroMesa);

}
