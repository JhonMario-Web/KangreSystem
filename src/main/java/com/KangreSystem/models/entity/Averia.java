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
@Table(name = "averias")
public class Averia implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "id_averia")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idAveria;
	
	@Column(name = "numero_averia")
	private String numeroAveria;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date fecha;
	
	@DateTimeFormat(pattern = "hh:mm")
	private Date hora;
	
	private Long total;
	
	@ManyToOne
	@JoinColumn(name = "id_admin")
	private Administrador admin;
	
	public Averia() {
		
	}

	public Long getIdAveria() {
		return idAveria;
	}

	public void setIdAveria(Long idAveria) {
		this.idAveria = idAveria;
	}

	public String getNumeroAveria() {
		return numeroAveria;
	}

	public void setNumeroAveria(String numeroAveria) {
		this.numeroAveria = numeroAveria;
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
		return "Averia [idAveria=" + idAveria + ", numeroAveria=" + numeroAveria + ", fecha=" + fecha + ", hora=" + hora
				+ ", total=" + total + ", admin=" + admin + "]";
	}
	
}
