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
@Table(name = "calificaciones")
public class Calificacion implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "id_calificacion")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idCalificacion;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date fecha;
	
	@DateTimeFormat(pattern = "hh:mm")
	private Date hora;
	
	@ManyToOne
	@JoinColumn(name = "id_cliente")
	private Cliente cliente;
	
	@ManyToOne
	@JoinColumn(name = "id_pedido")
	private Pedido pedido;
	
	private String calificacion;
	
	private String descripcion;
	
	public Calificacion() {
		
	}

	public Long getIdCalificacion() {
		return idCalificacion;
	}

	public void setIdCalificacion(Long idCalificacion) {
		this.idCalificacion = idCalificacion;
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

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public Pedido getPedido() {
		return pedido;
	}

	public void setPedido(Pedido pedido) {
		this.pedido = pedido;
	}

	public String getCalificacion() {
		return calificacion;
	}

	public void setCalificacion(String calificacion) {
		this.calificacion = calificacion;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	@Override
	public String toString() {
		return "Calificacion [idCalificacion=" + idCalificacion + ", fecha=" + fecha + ", hora=" + hora + ", cliente="
				+ cliente + ", pedido=" + pedido + ", calificacion=" + calificacion + ", descripcion=" + descripcion
				+ "]";
	}
	
}
