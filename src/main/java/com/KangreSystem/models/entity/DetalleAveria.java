package com.KangreSystem.models.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "detalle_averias")
public class DetalleAveria implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "id_detalle")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idDetalle;
	
	@ManyToOne
	@JoinColumn(name = "id_averia")
	private Averia averia;
	
	@ManyToOne
	@JoinColumn(name = "id_insumo")
	private Insumo insumo;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@Column(name = "fecha_vencimiento")
	private Date fechaVencimiento;
	
	private String lote;
	
	private Integer cantidad;
	
	private String causa;
	
	private Long subtotal;
	
	public DetalleAveria() {
		
	}

	public Long getIdDetalle() {
		return idDetalle;
	}

	public void setIdDetalle(Long idDetalle) {
		this.idDetalle = idDetalle;
	}

	public Averia getAveria() {
		return averia;
	}

	public void setAveria(Averia averia) {
		this.averia = averia;
	}

	public Insumo getInsumo() {
		return insumo;
	}

	public void setInsumo(Insumo insumo) {
		this.insumo = insumo;
	}

	public Date getFechaVencimiento() {
		return fechaVencimiento;
	}

	public void setFechaVencimiento(Date fechaVencimiento) {
		this.fechaVencimiento = fechaVencimiento;
	}

	public String getLote() {
		return lote;
	}

	public void setLote(String lote) {
		this.lote = lote;
	}

	public Integer getCantidad() {
		return cantidad;
	}

	public void setCantidad(Integer cantidad) {
		this.cantidad = cantidad;
	}

	public String getCausa() {
		return causa;
	}

	public void setCausa(String causa) {
		this.causa = causa;
	}

	public Long getSubtotal() {
		return subtotal;
	}

	public void setSubtotal(Long subtotal) {
		this.subtotal = subtotal;
	}

	@Override
	public String toString() {
		return "DetalleAveria [idDetalle=" + idDetalle + ", averia=" + averia + ", insumo=" + insumo
				+ ", fechaVencimiento=" + fechaVencimiento + ", lote=" + lote + ", cantidad=" + cantidad + ", causa="
				+ causa + ", subtotal=" + subtotal + "]";
	}

}
