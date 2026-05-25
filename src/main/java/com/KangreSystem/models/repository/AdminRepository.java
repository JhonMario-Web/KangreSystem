package com.KangreSystem.models.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.KangreSystem.models.entity.Administrador;
import com.KangreSystem.models.entity.User;

@Repository
public interface AdminRepository extends CrudRepository<Administrador, Long> {

	public boolean existsByUser(User user);
	public Administrador findByUser(User user);
	

}
