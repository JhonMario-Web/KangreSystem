package com.KangreSystem.models.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.KangreSystem.models.entity.Empleado;
import com.KangreSystem.models.entity.User;

@Repository
public interface EmpleadoRepository extends CrudRepository<Empleado, Long> {
	
	public Empleado findByUser(User user);
	public boolean existsByUser(User user);
}
