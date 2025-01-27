package com.KangreSystem.models.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "respuestas_seguridad")
public class RespuestaSeguridad implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "id_respuesta")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idRespuesta;
	
	@ManyToOne
	@JoinColumn(name = "id_user")
	private User user;

	@ManyToOne
	@JoinColumn(name = "pregunta_uno")
	private PreguntaSeguridad preguntaUno;
	
	@ManyToOne
	@JoinColumn(name = "pregunta_dos")
	private PreguntaSeguridad preguntaDos;
	
	@ManyToOne
	@JoinColumn(name = "pregunta_tres")
	private PreguntaSeguridad preguntaTres;
	
	@Column(name = "respuesta_uno")
	private String respuestaUno;
	
	@Column(name = "respuesta_dos")
	private String respuestaDos;
	
	@Column(name = "respuesta_tres")
	private String respuestaTres;
	
	public RespuestaSeguridad() {
		
	}

	public RespuestaSeguridad(User user, PreguntaSeguridad preguntaUno, PreguntaSeguridad preguntaDos,
			PreguntaSeguridad preguntaTres, String respuestaUno, String respuestaDos, String respuestaTres) {
		super();
		this.user = user;
		this.preguntaUno = preguntaUno;
		this.preguntaDos = preguntaDos;
		this.preguntaTres = preguntaTres;
		this.respuestaUno = respuestaUno;
		this.respuestaDos = respuestaDos;
		this.respuestaTres = respuestaTres;
	}

	public Long getIdRespuesta() {
		return idRespuesta;
	}

	public void setIdRespuesta(Long idRespuesta) {
		this.idRespuesta = idRespuesta;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public PreguntaSeguridad getPreguntaUno() {
		return preguntaUno;
	}

	public void setPreguntaUno(PreguntaSeguridad preguntaUno) {
		this.preguntaUno = preguntaUno;
	}

	public PreguntaSeguridad getPreguntaDos() {
		return preguntaDos;
	}

	public void setPreguntaDos(PreguntaSeguridad preguntaDos) {
		this.preguntaDos = preguntaDos;
	}

	public PreguntaSeguridad getPreguntaTres() {
		return preguntaTres;
	}

	public void setPreguntaTres(PreguntaSeguridad preguntaTres) {
		this.preguntaTres = preguntaTres;
	}

	public String getRespuestaUno() {
		return respuestaUno;
	}

	public void setRespuestaUno(String respuestaUno) {
		this.respuestaUno = respuestaUno;
	}

	public String getRespuestaDos() {
		return respuestaDos;
	}

	public void setRespuestaDos(String respuestaDos) {
		this.respuestaDos = respuestaDos;
	}

	public String getRespuestaTres() {
		return respuestaTres;
	}

	public void setRespuestaTres(String respuestaTres) {
		this.respuestaTres = respuestaTres;
	}

	@Override
	public String toString() {
		return "RespuestaSeguridad [idRespuesta=" + idRespuesta + ", user=" + user + ", preguntaUno=" + preguntaUno
				+ ", preguntaDos=" + preguntaDos + ", preguntaTres=" + preguntaTres + ", respuestaUno=" + respuestaUno
				+ ", respuestaDos=" + respuestaDos + ", respuestaTres=" + respuestaTres + "]";
	}
	
}
