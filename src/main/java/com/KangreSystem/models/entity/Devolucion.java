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
@Table(name = "devoluciones")
public class Devolucion implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "id_devolucion")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idDevolucion;
	
	@Column(name = "numero_devolucion")
	private String numeroDevolucion;
	
	@ManyToOne
	@JoinColumn(name = "id_proveedor")
	private Proveedor proveedor;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date fecha;
	
	@DateTimeFormat(pattern = "HH:mm")
	private Date hora;

	private Long total;
	
	@ManyToOne
	@JoinColumn(name = "id_admin")
	private Administrador admin;
	
	public Devolucion() {
		
	}

	public Long getIdDevolucion() {
		return idDevolucion;
	}

	public void setIdDevolucion(Long idDevolucion) {
		this.idDevolucion = idDevolucion;
	}

	public String getNumeroDevolucion() {
		return numeroDevolucion;
	}

	public void setNumeroDevolucion(String numeroDevolucion) {
		this.numeroDevolucion = numeroDevolucion;
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

	@Override
	public String toString() {
		return "Devolucion [idDevolucion=" + idDevolucion + ", numeroDevolucion=" + numeroDevolucion + ", proveedor="
				+ proveedor + ", fecha=" + fecha + ", hora=" + hora + ", total=" + total + ", admin=" + admin + "]";
	}
	
}
