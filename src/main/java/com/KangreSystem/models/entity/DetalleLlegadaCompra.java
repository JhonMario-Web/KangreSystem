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
import javax.persistence.Transient;

@Entity
@Table(name = "detalle_llegada_compras")
public class DetalleLlegadaCompra implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "id_detalle")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idDetalle;
	
	@ManyToOne
	@JoinColumn(name = "id_llegada")
	private LlegadaCompra llegada;
	
	@ManyToOne
	@JoinColumn(name = "id_insumo")
	private Insumo insumo;
	
	private Integer cantidad;
	
	private Long subtotal;
	
	@Transient
	private boolean cantEquals;
	
	public DetalleLlegadaCompra() {
		
	}

	public Long getIdDetalle() {
		return idDetalle;
	}

	public void setIdDetalle(Long idDetalle) {
		this.idDetalle = idDetalle;
	}

	public LlegadaCompra getLlegada() {
		return llegada;
	}

	public void setLlegada(LlegadaCompra llegada) {
		this.llegada = llegada;
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

	public boolean isCantEquals() {
		return cantEquals;
	}

	public void setCantEquals(boolean cantEquals) {
		this.cantEquals = cantEquals;
	}

	@Override
	public String toString() {
		return "DetalleLlegadaCompra [idDetalle=" + idDetalle + ", llegada=" + llegada + ", insumo=" + insumo
				+ ", cantidad=" + cantidad + ", subtotal=" + subtotal + ", cantEquals=" + cantEquals + "]";
	}
	
}
