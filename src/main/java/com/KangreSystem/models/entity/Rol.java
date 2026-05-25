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
@Table(name = "roles")
public class Rol implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "id_rol")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idRol;
	private String rol;
	
	@ManyToOne
	@JoinColumn(name = "id_user")
	private User user;
	
	@Column(name = "fecha_registro")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date fechaRegistro;
	
	@Column(name = "hora_registro")
	@DateTimeFormat(pattern = "hh:mm")
	private Date horaRegistro;
	
	public Rol() {
		
	}

	public Rol(Long idRol, String rol, User user, Date fechaRegistro) {
		this.idRol = idRol;
		this.rol = rol;
		this.user = user;
		this.fechaRegistro = fechaRegistro;
	}

	public Long getIdRol() {
		return idRol;
	}

	public void setIdRol(Long idRol) {
		this.idRol = idRol;
	}

	public String getRol() {
		return rol;
	}

	public void setRol(String rol) {
		this.rol = rol;
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

	public Date getHoraRegistro() {
		return horaRegistro;
	}

	public void setHoraRegistro(Date horaRegistro) {
		this.horaRegistro = horaRegistro;
	}

	@Override
	public String toString() {
		return "Rol [idRol=" + idRol + ", rol=" + rol + ", user=" + user + ", fechaRegistro=" + fechaRegistro
				+ ", horaRegistro=" + horaRegistro + "]";
	}
	
	
}
