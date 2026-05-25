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
@Table(name = "salidas")
public class Salida implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "id_salida")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idSalida;
	
	@ManyToOne
	@JoinColumn(name = "id_insumo")
	private Insumo insumo;
	
	private String tipo;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date fecha;
	
	@DateTimeFormat(pattern = "HH:mm")
	private Date hora;
	
	private Float cantidad;
	
	@ManyToOne
	@JoinColumn(name = "id_pedido")
	private Pedido pedido;
	
	@ManyToOne
	@JoinColumn(name = "id_averia")
	private Averia averia;
	
	@ManyToOne
	@JoinColumn(name = "id_devolucion")
	private Devolucion devolucion;
	
	public Salida() {
		
	}

	public Long getIdSalida() {
		return idSalida;
	}

	public void setIdSalida(Long idSalida) {
		this.idSalida = idSalida;
	}

	public Insumo getInsumo() {
		return insumo;
	}

	public void setInsumo(Insumo insumo) {
		this.insumo = insumo;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
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

	public Float getCantidad() {
		return cantidad;
	}

	public void setCantidad(Float cantidad) {
		this.cantidad = cantidad;
	}

	public Pedido getPedido() {
		return pedido;
	}

	public void setPedido(Pedido pedido) {
		this.pedido = pedido;
	}

	public Averia getAveria() {
		return averia;
	}

	public void setAveria(Averia averia) {
		this.averia = averia;
	}

	public Devolucion getDevolucion() {
		return devolucion;
	}

	public void setDevolucion(Devolucion devolucion) {
		this.devolucion = devolucion;
	}

	@Override
	public String toString() {
		return "Salida [idSalida=" + idSalida + ", insumo=" + insumo + ", tipo=" + tipo + ", fecha=" + fecha + ", hora="
				+ hora + ", cantidad=" + cantidad + ", pedido=" + pedido + ", averia=" + averia + ", devolucion="
				+ devolucion + "]";
	}

}
