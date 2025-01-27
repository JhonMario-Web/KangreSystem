package com.KangreSystem.models.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.KangreSystem.models.entity.Pais;

@Repository
public interface PaisRepository extends CrudRepository<Pais, Long> {

}
