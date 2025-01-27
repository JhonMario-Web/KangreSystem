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
@Table(name = "puntos_acumulados")
public class PuntoAcumulado implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "id_trxn")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idTrxn;
	
	@ManyToOne
	@JoinColumn(name = "id_pedido")
	private Pedido pedido;
	
	@ManyToOne
	@JoinColumn(name = "id_cliente")
	private Cliente cliente;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date fecha;
	
	@DateTimeFormat(pattern = "hh:mm")
	private Date hora;
	
	private Integer acumulados;
	
	private Integer disponibles;
	
	private Integer total;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date fechaVencimiento;
	
	public PuntoAcumulado() {
		
	}

	public Long getIdTrxn() {
		return idTrxn;
	}

	public void setIdTrxn(Long idTrxn) {
		this.idTrxn = idTrxn;
	}

	public Pedido getPedido() {
		return pedido;
	}

	public void setPedido(Pedido pedido) {
		this.pedido = pedido;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
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

	public Integer getAcumulados() {
		return acumulados;
	}

	public void setAcumulados(Integer acumulados) {
		this.acumulados = acumulados;
	}

	public Integer getDisponibles() {
		return disponibles;
	}

	public void setDisponibles(Integer disponibles) {
		this.disponibles = disponibles;
	}

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	public Date getFechaVencimiento() {
		return fechaVencimiento;
	}

	public void setFechaVencimiento(Date fechaVencimiento) {
		this.fechaVencimiento = fechaVencimiento;
	}

	@Override
	public String toString() {
		return "PuntoAcumulado [idTrxn=" + idTrxn + ", pedido=" + pedido + ", cliente=" + cliente + ", fecha=" + fecha
				+ ", hora=" + hora + ", acumulados=" + acumulados + ", disponibles=" + disponibles + ", total=" + total
				+ ", fechaVencimiento=" + fechaVencimiento + "]";
	}

}
