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
@Table(name = "detalle_pedidos")
public class DetallePedido implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "id_detalle")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idDetalle;
	
	@ManyToOne
	@JoinColumn(name = "id_pedido")
	private Pedido pedido;
	
	@ManyToOne 
	@JoinColumn(name = "id_producto")
	private Producto producto;
	
	private Integer cantidad;
	
	private Long subtotal;
	
	public DetallePedido() {
		
	}

	public Long getIdDetalle() {
		return idDetalle;
	}

	public void setIdDetalle(Long idDetalle) {
		this.idDetalle = idDetalle;
	}

	public Pedido getPedido() {
		return pedido;
	}

	public void setPedido(Pedido pedido) {
		this.pedido = pedido;
	}

	public Producto getProducto() {
		return producto;
	}

	public void setProducto(Producto producto) {
		this.producto = producto;
	}

	public Integer getCantidad() {
		return cantidad;
	}

	public void setCantidad(Integer cantidad) {
		this.cantidad = cantidad;
	}

	public Long getSubtotal() {
		return subtotal;
	}

	public void setSubtotal(Long subtotal) {
		this.subtotal = subtotal;
	}

	@Override
	public String toString() {
		return "DetallePedido [idDetalle=" + idDetalle + ", pedido=" + pedido + ", producto=" + producto + ", cantidad="
				+ cantidad + ", subtotal=" + subtotal + "]";
	}

}
