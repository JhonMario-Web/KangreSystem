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
@Table(name = "detalle_compras")
public class DetalleCompra implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id_detalle")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idDetalle;
	
	@ManyToOne
	@JoinColumn(name = "id_compra")
	private Compra compra;
	
	@ManyToOne
	@JoinColumn(name = "id_insumo")
	private Insumo insumo;
	
	private Integer cantidad;
	
	private Long subtotal;
	
	public DetalleCompra() {
		
	}

	public Long getIdDetalle() {
		return idDetalle;
	}

	public void setIdDetalle(Long idDetalle) {
		this.idDetalle = idDetalle;
	}

	public Compra getCompra() {
		return compra;
	}

	public void setCompra(Compra compra) {
		this.compra = compra;
	}

	public Insumo getInsumo() {
		return insumo;
	}

	public void setInsumo(Insumo insumo) {
		this.insumo = insumo;
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
		return "DetalleCompra [idDetalle=" + idDetalle + ", compra=" + compra + ", insumo=" + insumo + ", cantidad="
				+ cantidad + ", subtotal=" + subtotal + "]";
	}
	
}
