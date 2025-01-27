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
@Table(name = "favoritos")
public class Favorito implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "id_fav")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idFavorito;
	
	@ManyToOne
	@JoinColumn(name = "id_cliente")
	private Cliente cliente;
	
	@ManyToOne
	@JoinColumn(name = "id_favorito")
	private Cliente favorito;
	
	public Favorito() {
		
	}

	public Long getIdFavorito() {
		return idFavorito;
	}

	public void setIdFavorito(Long idFavorito) {
		this.idFavorito = idFavorito;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public Cliente getFavorito() {
		return favorito;
	}

	public void setFavorito(Cliente favorito) {
		this.favorito = favorito;
	}

	@Override
	public String toString() {
		return "Favorito [idFavorito=" + idFavorito + ", cliente=" + cliente + ", favorito=" + favorito+"]";
	}

}
