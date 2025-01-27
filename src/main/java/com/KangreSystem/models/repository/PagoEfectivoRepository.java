package com.KangreSystem.models.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.KangreSystem.models.entity.PagoEfectivo;

@Repository
public interface PagoEfectivoRepository extends CrudRepository<PagoEfectivo, Long> {

}
