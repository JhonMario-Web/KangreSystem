package com.KangreSystem.models.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "mesas")
public class Mesa implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "id_mesa")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idMesa;
	
	@Column(name = "numero_mesa")
	private String numeroMesa;
	
	@Column(name = "cap_personas")
	private Integer capacidad;
	
	private String estado; 
	
	public Mesa() {
		
	}

	public Long getIdMesa() {
		return idMesa;
	}

	public void setIdMesa(Long idMesa) {
		this.idMesa = idMesa;
	}

	public String getNumeroMesa() {
		return numeroMesa;
	}

	public void setNumeroMesa(String numeroMesa) {
		this.numeroMesa = numeroMesa;
	}

	public Integer getCapacidad() {
		return capacidad;
	}

	public void setCapacidad(Integer capacidad) {
		this.capacidad = capacidad;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	@Override
	public String toString() {
		return "Mesa [idMesa=" + idMesa + ", numeroMesa=" + numeroMesa + ", capacidad=" + capacidad + ", estado="
				+ estado + "]";
	}

}
