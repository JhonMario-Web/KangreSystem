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
@Table(name = "empleados")
public class Empleado implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_empleado")
	private Long idEmpleado;
	
	@ManyToOne
	@JoinColumn(name = "id_user")
	private User user;
	
	private String sueldo;
	
	@Column(name = "fecha_registro")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date fechaRegistro;
	
	public Empleado() {
		
	}

	public Long getIdEmpleado() {
		return idEmpleado;
	}

	public void setIdEmpleado(Long idEmpleado) {
		this.idEmpleado = idEmpleado;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	public String getSueldo() {
		return sueldo;
	}

	public void setSueldo(String sueldo) {
		this.sueldo = sueldo;
	}
	
	public Date getFechaRegistro() {
		return fechaRegistro;
	}

	public void setFechaRegistro(Date fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}

	@Override
	public String toString() {
		return "Empleado [idEmpleado=" + idEmpleado + ", user=" + user + ", sueldo=" + sueldo + ", fechaRegistro="
				+ fechaRegistro + "]";
	}

	
}
