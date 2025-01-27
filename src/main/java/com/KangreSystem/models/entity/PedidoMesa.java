package com.KangreSystem.models.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "pedidos_mesas")
public class PedidoMesa implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "id_pedido_mesa")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idPedidoMesa;
	
	@ManyToOne
	@JoinColumn(name = "id_pedido")
	private Pedido pedido;
	
	@ManyToOne
	@JoinColumn(name = "id_mesa")
	private Mesa mesa;
	
	public PedidoMesa() {
		
	}

	public Long getIdPedidoMesa() {
		return idPedidoMesa;
	}

	public void setIdPedidoMesa(Long idPedidoMesa) {
		this.idPedidoMesa = idPedidoMesa;
	}

	public Pedido getPedido() {
		return pedido;
	}

	public void setPedido(Pedido pedido) {
		this.pedido = pedido;
	}

	public Mesa getMesa() {
		return mesa;
	}

	public void setMesa(Mesa mesa) {
		this.mesa = mesa;
	}

	@Override
	public String toString() {
		return "PedidoMesa [idPedidoMesa=" + idPedidoMesa + ", pedido=" + pedido + ", mesa=" + mesa + "]";
	}
	

}
