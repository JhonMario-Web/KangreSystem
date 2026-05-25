package com.KangreSystem.models.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.KangreSystem.models.entity.DetalleLlegadaCompra;

@Repository
public interface DetalleLlegadaCompraRepo extends CrudRepository<DetalleLlegadaCompra, Long> {

}
