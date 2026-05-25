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
@Table(name = "compras")
public class Compra implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id_compra")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idCompra;
	
	@Column(name = "numero_compra")
	private String numeroCompra;
	
	@ManyToOne
	@JoinColumn(name = "id_proveedor")
	private Proveedor proveedor;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date fecha;
	
	@DateTimeFormat(pattern = "hh:mm")
	private Date hora;
	
	private String estado;
	
	private Long total;
	
	@ManyToOne
	@JoinColumn(name = "id_admin")
	private Administrador admin;
	
	public Compra () {
		
	}

	public Long getIdCompra() {
		return idCompra;
	}

	public void setIdCompra(Long idCompra) {
		this.idCompra = idCompra;
	}

	public String getNumeroCompra() {
		return numeroCompra;
	}

	public void setNumeroCompra(String numeroCompra) {
		this.numeroCompra = numeroCompra;
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

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
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

	@Override
	public String toString() {
		return "Compra [idCompra=" + idCompra + ", numeroCompra=" + numeroCompra + ", proveedor=" + proveedor
				+ ", fecha=" + fecha + ", hora=" + hora + ", estado=" + estado + ", total=" + total + ", admin=" + admin
				+ "]";
	}
	
}
