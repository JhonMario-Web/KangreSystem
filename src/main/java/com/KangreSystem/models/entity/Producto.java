package com.KangreSystem.models.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedStoredProcedureQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.springframework.web.multipart.MultipartFile;

@Entity
@NamedStoredProcedureQuery(name="getAllProductos", procedureName = "get_all_productos", resultClasses = {Producto.class})
@Table(name="productos")
public class Producto implements Serializable{
	
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id_producto")
	private Long idProducto;
	
	private String nombre;
	
	private String detalle;
	
	private Long precio;
	
	@Column(name = "cant_vendida")
	private Integer cantVendida;
	
	private String foto;
	
	@Transient
	private MultipartFile file;
	
	@ManyToOne
	@JoinColumn(name="id_categoria")
	private CategoriaProducto categoria;
	
	private String estado;
	
	public Producto() {
		this.estado = "ACTIVO";
	}
	
	public Long getIdProducto() {
		return idProducto;
	}
	public void setIdProducto(Long idProducto) {
		this.idProducto = idProducto;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getDetalle() {
		return detalle;
	}
	public void setDetalle(String detalle) {
		this.detalle = detalle;
	}
	public Long getPrecio() {
		return precio;
	}
	public void setPrecio(Long precio) {
		this.precio = precio;
	}
	public CategoriaProducto getCategoria() {
		return categoria;
	}
	public void setCategoria(CategoriaProducto categoria) {
		this.categoria = categoria;
	}
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
	
	public Integer getCantVendida() {
		return cantVendida;
	}

	public void setCantVendida(Integer cantVendida) {
		this.cantVendida = cantVendida;
	}

	public String getFoto() {
		return foto;
	}

	public void setFoto(String foto) {
		this.foto = foto;
	}

	public MultipartFile getFile() {
		return file;
	}

	public void setFile(MultipartFile file) {
		this.file = file;
	}

	@Override
	public String toString() {
		return "Producto [idProducto=" + idProducto + ", nombre=" + nombre + ", detalle=" + detalle + ", precio="
				+ precio + ", cantVendida=" + cantVendida + ", foto=" + foto + ", file=" + file + ", categoria="
				+ categoria + ", estado=" + estado + "]";
	}

}
