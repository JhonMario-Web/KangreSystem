package com.KangreSystem.models.repository;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.KangreSystem.models.entity.Oferta;

@Repository
public interface OfertaRepository extends CrudRepository<Oferta, Long> {

	//METODO PARA LA BUSQUEDA DINAMICA
	public List<Oferta> findByNombreContaining(String nombre);
	public List<Oferta> findByEnabled(boolean enabled);
	
}
