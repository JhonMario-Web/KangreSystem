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
@Table(name = "clientes")
public class Cliente implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_cliente")
	private Long idCliente;
	
	@ManyToOne
	@JoinColumn(name = "id_user")
	private User user;
	private Integer kangrepuntos;
	
	public Cliente() {
		
	}

	public Long getIdCliente() {
		return idCliente;
	}

	public void setIdCliente(Long idCliente) {
		this.idCliente = idCliente;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Integer getKangrepuntos() {
		return kangrepuntos;
	}

	public void setKangrepuntos(Integer kangrepuntos) {
		this.kangrepuntos = kangrepuntos;
	}

	@Override
	public String toString() {
		return "Cliente [idCliente=" + idCliente + ", user=" + user + ", kangrepuntos=" + kangrepuntos + "]";
	}
	
}
