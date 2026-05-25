package com.KangreSystem.models.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.KangreSystem.models.entity.RespuestaSeguridad;
import com.KangreSystem.models.entity.User;

@Repository
public interface RespuestaSeguridadRepo extends CrudRepository<RespuestaSeguridad, Long> {
	public RespuestaSeguridad findByUser(User user);
} 
