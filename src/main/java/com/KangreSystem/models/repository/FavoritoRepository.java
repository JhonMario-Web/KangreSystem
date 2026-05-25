package com.KangreSystem.models.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.KangreSystem.models.entity.Cliente;
import com.KangreSystem.models.entity.Favorito;

@Repository
public interface FavoritoRepository extends CrudRepository<Favorito, Long> {
	
	public List<Favorito> findByCliente(Cliente cliente);
	public Favorito findByFavorito(Cliente favorito);
	public Long countByCliente(Cliente cliente);

}
