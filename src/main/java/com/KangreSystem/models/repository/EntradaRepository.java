package com.KangreSystem.models.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.KangreSystem.models.entity.Entrada;
import com.KangreSystem.models.entity.Insumo;
import com.KangreSystem.models.entity.LlegadaCompra;

@Repository
public interface EntradaRepository extends CrudRepository<Entrada, Long> {
	
	public List<Entrada> findByLlegada(LlegadaCompra llegada);
	public List<Entrada> findByFecha(Date fecha);
	public List<Entrada> findByInsumo(Insumo insumo);
	
}
