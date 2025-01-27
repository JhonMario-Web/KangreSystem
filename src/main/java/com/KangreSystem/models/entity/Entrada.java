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
@Table(name = "entradas")
public class Entrada implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "id_entrada")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idEntrada;
	
	@ManyToOne
	@JoinColumn(name = "id_insumo")
	private Insumo insumo;
	
	@ManyToOne
	@JoinColumn(name = "id_llegada")
	private LlegadaCompra llegada;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date fecha;
	
	@DateTimeFormat(pattern = "HH:mm")
	private Date hora;
	
	private Integer cantidad;
	
	public Entrada() {
		
	}

	public Long getIdEntrada() {
		return idEntrada;
	}

	public void setIdEntrada(Long idEntrada) {
		this.idEntrada = idEntrada;
	}

	public Insumo getInsumo() {
		return insumo;
	}

	public void setInsumo(Insumo insumo) {
		this.insumo = insumo;
	}

	public LlegadaCompra getLlegada() {
		return llegada;
	}

	public void setLlegada(LlegadaCompra llegada) {
		this.llegada = llegada;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public Date getHora() {
		return hora;
	}

	public void setHora(Date hora) {
		this.hora = hora;
	}

	public Integer getCantidad() {
		return cantidad;
	}

	public void setCantidad(Integer cantidad) {
		this.cantidad = cantidad;
	}

	@Override
	public String toString() {
		return "Entrada [idEntrada=" + idEntrada + ", insumo=" + insumo + ", llegada=" + llegada + ", fecha=" + fecha
				+ ", hora=" + hora + ", cantidad=" + cantidad + "]";
	}

}
