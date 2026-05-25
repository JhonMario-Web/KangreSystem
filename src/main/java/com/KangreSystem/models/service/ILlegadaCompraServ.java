package com.KangreSystem.models.service;

import java.util.List;
import com.KangreSystem.models.entity.Compra;
import com.KangreSystem.models.entity.DetalleLlegadaCompra;
import com.KangreSystem.models.entity.Entrada;
import com.KangreSystem.models.entity.LlegadaCompra;
import com.KangreSystem.models.entity.Proveedor;

public interface ILlegadaCompraServ {
	
	//METODOS CRUD
	public List<LlegadaCompra> listar();
	public void guardar(LlegadaCompra llegada);
	public void eliminar(Long idLlegada);
	
	//METODOS AUXILIARES
	public LlegadaCompra buscarPorId(Long idLlegada);
	public boolean existePorNumeroFactura(String numeroFactura);
	public List<LlegadaCompra> buscarPorProveedor(Proveedor proveedor);
	public LlegadaCompra buscarPorNumeroFactura(String numeroFactura);
	
	//METODOS LOGICOS DEL SISTEMA
	public void recibirCompraEnCero(Compra compra);
	public String numeroLlegadaCompra();
	public boolean insumosFaltantes(LlegadaCompra llegada, List<DetalleLlegadaCompra> aRecibir);
	public boolean cantidadesIguales(List<DetalleLlegadaCompra> aRecibir);
	public boolean preciosIguales(LlegadaCompra llegada, List<DetalleLlegadaCompra> aRecibir);
	public List<Entrada> agregarUnidades(List<DetalleLlegadaCompra> detalles);

}
