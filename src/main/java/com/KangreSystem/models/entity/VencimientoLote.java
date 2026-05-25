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
@Table(name = "vencimiento_lotes")
public class VencimientoLote implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "id_vencimiento")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idVencimiento;
	
	@ManyToOne
	@JoinColumn(name = "id_llegada")
	private LlegadaCompra llegada;
	
	@ManyToOne
	@JoinColumn(name = "id_insumo")
	private Insumo insumo;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date fecha;
	
	private String lote;
	
	private Integer cantidad;
	
	@Column(name = "cant_disponible")
	private Float cantidadDisponible;
	
	public VencimientoLote() {
		
	}

	public Long getIdVencimiento() {
		return idVencimiento;
	}

	public void setIdVencimiento(Long idVencimiento) {
		this.idVencimiento = idVencimiento;
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

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
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

	public Float getCantidadDisponible() {
		return cantidadDisponible;
	}

	public void setCantidadDisponible(Float cantidadDisponible) {
		this.cantidadDisponible = cantidadDisponible;
	}

	@Override
	public String toString() {
		return "VencimientoLote [idVencimiento=" + idVencimiento + ", llegada=" + llegada + ", insumo=" + insumo
				+ ", fecha=" + fecha + ", lote=" + lote + ", cantidad=" + cantidad + ", cantidadDisponible=" + cantidadDisponible
				+ "]";
	}

}
