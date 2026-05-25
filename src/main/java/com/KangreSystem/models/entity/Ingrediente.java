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
import javax.persistence.Transient;

import org.springframework.web.multipart.MultipartFile;

@Entity
@Table(name = "ingredientes")
public class Ingrediente implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "id_ingrediente")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idIngrediente;
	
	@ManyToOne
	@JoinColumn(name = "id_producto")
	private Producto producto;
	
	@ManyToOne
	@JoinColumn(name = "id_insumo")
	private Insumo insumo;
	
	private Float cantidad;
	
	@Transient
	private MultipartFile file;
	
	public Ingrediente() {
		
	}

	public Ingrediente(Producto producto, Insumo insumo, Float cantidad) {
		super();
		this.producto = producto;
		this.insumo = insumo;
		this.cantidad = cantidad;
	}

	public Long getIdIngrediente() {
		return idIngrediente;
	}

	public void setIdIngrediente(Long idIngrediente) {
		this.idIngrediente = idIngrediente;
	}

	public Producto getProducto() {
		return producto;
	}

	public void setProducto(Producto producto) {
		this.producto = producto;
	}

	public Insumo getInsumo() {
		return insumo;
	}

	public void setInsumo(Insumo insumo) {
		this.insumo = insumo;
	}

	public Float getCantidad() {
		return cantidad;
	}

	public void setCantidad(Float cantidad) {
		this.cantidad = cantidad;
	}

	public MultipartFile getFile() {
		return file;
	}

	public void setFile(MultipartFile file) {
		this.file = file;
	}

	@Override
	public String toString() {
		return "Ingrediente [idIngrediente=" + idIngrediente + ", producto=" + producto + ", insumo=" + insumo
				+ ", cantidad=" + cantidad + "]";
	}
	
}
