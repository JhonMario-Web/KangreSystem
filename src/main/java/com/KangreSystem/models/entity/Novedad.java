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
import org.springframework.web.multipart.MultipartFile;

@Entity
@Table(name = "novedades")
public class Novedad implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "id_novedad")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idNovedad;
	
	@ManyToOne
	@JoinColumn(name = "id_user")
	private User user;
	
	private String asunto;
	
	private String mensaje;
	
	private String destinatarios;
	
	/*DATE: Acotara el campo solo a la Fecha, descartando la hora.
	@Temporal(TemporalType.DATE)
	TIME: Acotara el campo solo a la Hora, descartando a la fecha.
	@Temporal(TemporalType.TIME)
	TIMESTAMP: Toma la fecha y hora.
	@Temporal(TemporalType.TIMESTAMP)*/
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date fecha;
	
	@DateTimeFormat(pattern = "hh:mm")
	private Date hora;
	
	@Transient
	private MultipartFile file;
	
	public Novedad() {
		
	}

	public Long getIdNovedad() {
		return idNovedad;
	}

	public void setIdNovedad(Long idNovedad) {
		this.idNovedad = idNovedad;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getAsunto() {
		return asunto;
	}

	public void setAsunto(String asunto) {
		this.asunto = asunto;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	public String getDestinatarios() {
		return destinatarios;
	}

	public void setDestinatarios(String destinatarios) {
		this.destinatarios = destinatarios;
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

	public MultipartFile getFile() {
		return file;
	}

	public void setFile(MultipartFile file) {
		this.file = file;
	}

	@Override
	public String toString() {
		return "Novedad [idNovedad=" + idNovedad + ", user=" + user + ", asunto=" + asunto + ", mensaje=" + mensaje
				+ ", destinatarios=" + destinatarios + ", fecha=" + fecha + ", hora=" + hora + ", file=" + file + "]";
	}

}
