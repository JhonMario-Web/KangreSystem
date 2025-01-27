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
@Table(name = "ventas")
public class Venta implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "id_venta")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idVenta;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date fecha;
	
	@DateTimeFormat(pattern = "HH:mm")
	private Date hora;
	
	private Long total;
	
	@ManyToOne
	@JoinColumn(name = "id_pedido")
	private Pedido pedido;
	
	public Venta() {
		
	}

	public Long getIdVenta() {
		return idVenta;
	}

	public void setIdVenta(Long idVenta) {
		this.idVenta = idVenta;
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

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

	public Pedido getPedido() {
		return pedido;
	}

	public void setPedido(Pedido pedido) {
		this.pedido = pedido;
	}

	@Override
	public String toString() {
		return "Venta [idVenta=" + idVenta + ", fecha=" + fecha + ", hora=" + hora + ", total=" + total + ", pedido="
				+ pedido + "]";
	}
	
}
