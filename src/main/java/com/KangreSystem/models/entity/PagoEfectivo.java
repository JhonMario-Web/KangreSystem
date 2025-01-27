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
@Table(name = "pagos_efectivo")
public class PagoEfectivo implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "id_pago")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idPago;
	
	@ManyToOne
	@JoinColumn(name = "id_pedido")
	private Pedido pedido;
	
	private Long total;
	
	private Long efectivo;
	
	private Long cambio;
	
	public PagoEfectivo() {
		
	}

	public Long getIdPago() {
		return idPago;
	}

	public void setIdPago(Long idPago) {
		this.idPago = idPago;
	}

	public Pedido getPedido() {
		return pedido;
	}

	public void setPedido(Pedido pedido) {
		this.pedido = pedido;
	}

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

	public Long getEfectivo() {
		return efectivo;
	}

	public void setEfectivo(Long efectivo) {
		this.efectivo = efectivo;
	}

	public Long getCambio() {
		return cambio;
	}

	public void setCambio(Long cambio) {
		this.cambio = cambio;
	}

	@Override
	public String toString() {
		return "PagoEfectivo [idPago=" + idPago + ", pedido=" + pedido + ", total=" + total + ", efectivo=" + efectivo
				+ ", cambio=" + cambio + "]";
	}

}
