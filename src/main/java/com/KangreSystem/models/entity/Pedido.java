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
@Table(name = "pedidos")
public class Pedido implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "id_pedido")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idPedido;
	
	@Column(name = "numero_orden")
	private String numeroOrden;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date fecha;
	
	@DateTimeFormat(pattern = "hh:mm")
	private Date hora;
	
	@ManyToOne
	@JoinColumn(name = "id_cliente")
	private Cliente cliente;
	
	@ManyToOne
	@JoinColumn(name = "id_empleado")
	private Empleado empleado;
	
	private Integer kangrepuntos;
	
	private Long subtotal;
	
	private Long total;
	
	private boolean calificado;
	
	private String estado;
	
	@Column(name = "tipo_pago")
	private String tipoPago;
	
	public Pedido() {
		this.kangrepuntos = 0;
		this.subtotal = (long) 0;
		this.total = (long) 0;
		
	}

	public Long getIdPedido() {
		return idPedido;
	}

	public void setIdPedido(Long idPedido) {
		this.idPedido = idPedido;
	}

	public String getNumeroOrden() {
		return numeroOrden;
	}

	public void setNumeroOrden(String numeroOrden) {
		this.numeroOrden = numeroOrden;
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

	public Empleado getEmpleado() {
		return empleado;
	}

	public void setEmpleado(Empleado empleado) {
		this.empleado = empleado;
	}

	public Integer getKangrepuntos() {
		return kangrepuntos;
	}

	public void setKangrepuntos(Integer kangrepuntos) {
		this.kangrepuntos = kangrepuntos;
	}

	public Long getSubtotal() {
		return subtotal;
	}

	public void setSubtotal(Long subtotal) {
		this.subtotal = subtotal;
	}

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

	public boolean isCalificado() {
		return calificado;
	}

	public void setCalificado(boolean calificado) {
		this.calificado = calificado;
	}

	public String getTipoPago() {
		return tipoPago;
	}

	public void setTipoPago(String tipoPago) {
		this.tipoPago = tipoPago;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	@Override
	public String toString() {
		return "Pedido [idPedido=" + idPedido + ", numeroOrden=" + numeroOrden + ", fecha=" + fecha + ", hora=" + hora
				+ ", cliente=" + cliente + ", empleado=" + empleado + ", kangrepuntos=" + kangrepuntos + ", subtotal="
				+ subtotal + ", total=" + total + ", calificado=" + calificado + ", estado=" + estado + ", tipoPago="
				+ tipoPago + "]";
	}

}
