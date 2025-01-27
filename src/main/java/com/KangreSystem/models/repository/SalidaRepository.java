package com.KangreSystem.models.repository;

import java.util.Date;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.KangreSystem.models.entity.Averia;
import com.KangreSystem.models.entity.Devolucion;
import com.KangreSystem.models.entity.Insumo;
import com.KangreSystem.models.entity.Pedido;
import com.KangreSystem.models.entity.Salida;

@Repository
public interface SalidaRepository extends CrudRepository<Salida, Long> {
	
	public List<Salida> findByTipoAndFecha(String tipo, Date fecha);
	public List<Salida> findByFecha(Date fecha);
	public List<Salida> findByTipo(String tipo);
	public List<Salida> findByInsumo(Insumo insumo);
	public List<Salida> findByPedido(Pedido pedido);
	public List<Salida> findByAveria(Averia averia);
	public List<Salida> findByDevolucion(Devolucion devolucion);

}
