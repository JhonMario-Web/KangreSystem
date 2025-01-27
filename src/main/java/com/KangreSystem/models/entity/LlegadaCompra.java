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
@Table(name = "llegada_compras")
public class LlegadaCompra implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "id_llegada")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idLlegada;
	
	@ManyToOne
	@JoinColumn(name = "id_compra")
	private Compra compra;
	
	@Column(name = "numero_llegada_compra")
	private String numeroLlegada;
	
	@ManyToOne
	@JoinColumn(name = "id_proveedor")
	private Proveedor proveedor;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date fecha;
	
	@DateTimeFormat(pattern = "hh:mm")
	private Date hora;
	
	private Long total;
	
	@ManyToOne
	@JoinColumn(name = "id_admin")
	private Administrador admin;
	
	private String numeroFactura;
	
	private String novedad;
	
	public LlegadaCompra() {
		
	}

	public Long getIdLlegada() {
		return idLlegada;
	}

	public void setIdLlegada(Long idLlegada) {
		this.idLlegada = idLlegada;
	}

	public Compra getCompra() {
		return compra;
	}

	public void setCompra(Compra compra) {
		this.compra = compra;
	}

	public String getNumeroLlegada() {
		return numeroLlegada;
	}

	public void setNumeroLlegada(String numeroLlegada) {
		this.numeroLlegada = numeroLlegada;
	}

	public Proveedor getProveedor() {
		return proveedor;
	}

	public void setProveedor(Proveedor proveedor) {
		this.proveedor = proveedor;
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

	public Administrador getAdmin() {
		return admin;
	}

	public void setAdmin(Administrador admin) {
		this.admin = admin;
	}

	public String getNumeroFactura() {
		return numeroFactura;
	}

	public void setNumeroFactura(String numeroFactura) {
		this.numeroFactura = numeroFactura;
	}

	public String getNovedad() {
		return novedad;
	}

	public void setNovedad(String novedad) {
		this.novedad = novedad;
	}

	@Override
	public String toString() {
		return "LlegadaCompra [idLlegada=" + idLlegada + ", compra=" + compra + ", numeroLlegada=" + numeroLlegada
				+ ", proveedor=" + proveedor + ", fecha=" + fecha + ", hora=" + hora + ", total=" + total + ", admin="
				+ admin + ", numeroFactura=" + numeroFactura + ", novedad=" + novedad + "]";
	}
	
}
