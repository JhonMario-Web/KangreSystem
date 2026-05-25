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
@Table(name = "puntos_redimidos")
public class PuntoRedimido implements Serializable{
	
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
	
	@ManyToOne
	@JoinColumn(name = "trxn_acum")
	private PuntoAcumulado acumulado;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date fecha;
	
	@DateTimeFormat(pattern = "hh:mm")
	private Date hora;
	
	private Integer redimidos;
	
	private Integer total;
	
	public PuntoRedimido() {
		
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

	public PuntoAcumulado getAcumulado() {
		return acumulado;
	}

	public void setAcumulado(PuntoAcumulado acumulado) {
		this.acumulado = acumulado;
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

	public Integer getRedimidos() {
		return redimidos;
	}

	public void setRedimidos(Integer redimidos) {
		this.redimidos = redimidos;
	}

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	@Override
	public String toString() {
		return "PuntoRedimido [idTrxn=" + idTrxn + ", pedido=" + pedido + ", cliente=" + cliente + ", acumulado="
				+ acumulado + ", fecha=" + fecha + ", hora=" + hora + ", redimidos=" + redimidos + ", total=" + total
				+ "]";
	}

}
