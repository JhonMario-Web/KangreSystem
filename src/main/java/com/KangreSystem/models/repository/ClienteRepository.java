package com.KangreSystem.models.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.KangreSystem.models.entity.Cliente;
import com.KangreSystem.models.entity.User;

@Repository
public interface ClienteRepository extends CrudRepository<Cliente, Long>{
	
	public Cliente findByUser(User user);
}
