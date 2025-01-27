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
import javax.persistence.Transient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.web.multipart.MultipartFile;

@Entity
@Table(name = "ofertas")
public class Oferta implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "id_oferta")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idOferta;
	
	@Column(name = "nombre_oferta")
	private String nombre;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@Column(name = "fecha_inicio")
	private Date fechaInicio;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@Column(name = "fecha_fin")
	private Date fechaFin;
	
//	@Temporal(TemporalType.DATE)
//	@DateTimeFormat(pattern = "yyyy-MM-dd", iso = ISO.DATE)
	
	@DateTimeFormat(pattern = "yyyy-MM-dd", iso = ISO.DATE)
	@Column(name = "fecha_registro")
	private Date fechaRegistro;
	
	private String descripcion;
	
	@ManyToOne
	@JoinColumn(name = "id_admin")
	private Administrador admin;
	
	private boolean enabled;
	
	@Transient
	private MultipartFile file;
	
	public Oferta() {
		
	}

	public Oferta(String nombre, Date fechaInicio, Date fechaFin, String descripcion, Administrador admin,
			boolean enabled) {
		super();
		this.nombre = nombre;
		this.fechaInicio = fechaInicio;
		this.fechaFin = fechaFin;
		this.descripcion = descripcion;
		this.admin = admin;
		this.enabled = enabled;
	}
	
	public Oferta(String nombre, Date fechaInicio, Date fechaFin, String descripcion, Administrador admin,
			boolean enabled, MultipartFile file) {
		super();
		this.nombre = nombre;
		this.fechaInicio = fechaInicio;
		this.fechaFin = fechaFin;
		this.descripcion = descripcion;
		this.admin = admin;
		this.enabled = enabled;
		this.file = file;
	}

	public Long getIdOferta() {
		return idOferta;
	}

	public void setIdOferta(Long idOferta) {
		this.idOferta = idOferta;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Date getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(Date fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public Date getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(Date fechaFin) {
		this.fechaFin = fechaFin;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Administrador getAdmin() {
		return admin;
	}

	public void setAdmin(Administrador admin) {
		this.admin = admin;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public MultipartFile getFile() {
		return file;
	}

	public void setFile(MultipartFile file) {
		this.file = file;
	}

	public Date getFechaRegistro() {
		return fechaRegistro;
	}

	public void setFechaRegistro(Date fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}

	@Override
	public String toString() {
		return "Oferta [idOferta=" + idOferta + ", nombre=" + nombre + ", fechaInicio=" + fechaInicio + ", fechaFin="
				+ fechaFin + ", fechaRegistro=" + fechaRegistro + ", descripcion=" + descripcion + ", admin=" + admin
				+ ", enabled=" + enabled + ", file=" + file + "]";
	}
}
