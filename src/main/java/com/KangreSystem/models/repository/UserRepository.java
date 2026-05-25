package com.KangreSystem.models.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.KangreSystem.models.entity.User;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
	
	//METODOS PARA BUSQUEDA DINAMICA
	public List<User> findByUsernameContaining(String username);
	public List<User> findByGenero(Character genero);
	public List<User> findByCiudad(String ciudad);
	public List<User> findByEnabled(boolean enabled);
	public List<User> findByGeneroAndCiudad(Character genero, String ciudad);
	public List<User> findByGeneroAndEnabled(Character genero, boolean enabled);
	public List<User> findByCiudadAndEnabled(String ciudad, boolean enabled);
	public List<User> findByGeneroAndCiudadAndEnabled(Character genero, String ciudad, boolean enabled);
	
	//METODOS AUXILIARES
	public User findByNumeroDoc(String numeroDoc);
	public boolean existsByNumeroDoc(String numeroDoc);
	public boolean existsByEmail(String email);
	
}
