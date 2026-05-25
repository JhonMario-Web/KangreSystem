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
@Table(name = "administradores")
public class Administrador implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "id_admin")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idAdmin;
	
	@ManyToOne
	@JoinColumn(name = "id_user")
	private User user;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@Column(name = "fecha_registro")
	private Date fechaRegistro;
	
	public Administrador() {
		
	}
	
	public Administrador(User user) {
		this.user = user;
	}

	public Long getIdAdmin() {
		return idAdmin;
	}

	public void setIdAdmin(Long idAdmin) {
		this.idAdmin = idAdmin;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Date getFechaRegistro() {
		return fechaRegistro;
	}

	public void setFechaRegistro(Date fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}

	@Override
	public String toString() {
		return "Administrador [idAdmin=" + idAdmin + ", user=" + user + ", fechaRegistro=" + fechaRegistro + "]";
	}
}
