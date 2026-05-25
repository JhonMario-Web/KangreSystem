package com.KangreSystem.models.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.KangreSystem.models.entity.Rol;
import com.KangreSystem.models.entity.User;

@Repository
public interface RolRepository extends CrudRepository<Rol, Long> {
	public List<Rol> findByRol(String rol);
	public List<Rol> findByUser(User user);
	public Rol findByUserOrRol(User user, String rol);
	public Rol findByUserAndRol(User user, String rol);
	public boolean existsByUser(User user);
	public boolean existsByUserAndRol(User user, String numeroDoc);
	public long countByRol(String rol);
}
