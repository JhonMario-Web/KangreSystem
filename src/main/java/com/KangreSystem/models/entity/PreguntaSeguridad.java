package com.KangreSystem.models.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "preguntas_seguridad")
public class PreguntaSeguridad implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "id_pregunta")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idPregunta;
	
	private String pregunta;
	
	public PreguntaSeguridad() {
		
	}

	public PreguntaSeguridad(String pregunta) {
		super();
		this.pregunta = pregunta;
	}

	public Long getIdPregunta() {
		return idPregunta;
	}

	public void setIdPregunta(Long idPregunta) {
		this.idPregunta = idPregunta;
	}

	public String getPregunta() {
		return pregunta;
	}

	public void setPregunta(String pregunta) {
		this.pregunta = pregunta;
	}

	@Override
	public String toString() {
		return "PreguntaSeguridad [idPregunta=" + idPregunta + ", pregunta=" + pregunta + "]";
	}
}
