package com.KangreSystem.models.repository;

import java.util.Date;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.KangreSystem.models.entity.Insumo;
import com.KangreSystem.models.entity.LlegadaCompra;
import com.KangreSystem.models.entity.VencimientoLote;

@Repository
public interface VencimientoLoteRepo extends CrudRepository<VencimientoLote, Long> {
	
	public List<VencimientoLote> findByInsumo(Insumo insumo);
	public VencimientoLote findByInsumoAndFechaAndLote(Insumo insumo, Date fechaVencimiento, String lote);
	public boolean existsByInsumoAndFechaAndLote(Insumo insumo, Date fechaVencimiento, String lote);
	public List<VencimientoLote> findByLlegada(LlegadaCompra llegada);
	public List<VencimientoLote> findByInsumoOrderByFechaAsc(Insumo insumo);
	public List<VencimientoLote> findByFechaAndLote(Date fecha, String lote);
	public List<VencimientoLote> findByLote(String lote);
	public List<VencimientoLote> findByInsumoAndLote(Insumo insumo, String lote);
	public List<VencimientoLote> findByInsumoAndFecha(Insumo insumo, Date fecha);
	public List<VencimientoLote> findByFecha(Date fecha);
	
}
