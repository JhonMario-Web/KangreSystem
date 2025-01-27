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
@Table(name = "proveedores")
public class Proveedor implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_proveedor")
	private Long idProveedor;
	
	@Column(name = "nit_proveedor", unique = true)
	private String nitProveedor;
	private String nombre;
	private String telefono;
	private String celular;
	private String email;
	private String ciudad;
	private String direccion;
	
	@Column(name = "tipo_archivo")
	private String tipoArchivo;
	
	@Transient
	private MultipartFile file;
	
	@ManyToOne
	@JoinColumn(name = "id_categoria")
	private CategoriaProveedor categoria;
	private boolean enabled;
	private String logo;
	
	public Proveedor() {
		
	}
	
	public Proveedor(Long idProveedor, String nitProveedor, String nombre, String celular, String email,
			String direccion, CategoriaProveedor categoria, boolean enabled) {
		super();
		this.idProveedor = idProveedor;
		this.nitProveedor = nitProveedor;
		this.nombre = nombre;
		this.celular = celular;
		this.email = email;
		this.direccion = direccion;
		this.categoria = categoria;
		this.enabled = enabled;
	}



	public Proveedor(String nitProveedor, String nombre, String telefono, String celular,
			String email, String ciudad, String direccion, String categoria, String tipoArchivo, String logo) {
		super();
		this.nitProveedor = nitProveedor;
		this.nombre = nombre;
		this.telefono = telefono;
		this.celular = celular;
		this.email = email;
		this.ciudad = ciudad;
		this.direccion = direccion;
		this.categoria.setIdCategoria(Long.parseLong(categoria));
		this.tipoArchivo = tipoArchivo;
		this.logo = logo;
	}


	public Long getIdProveedor() {
		return idProveedor;
	}

	public void setIdProveedor(Long idProveedor) {
		this.idProveedor = idProveedor;
	}

	public String getNitProveedor() {
		return nitProveedor;
	}

	public void setNitProveedor(String nitProveedor) {
		this.nitProveedor = nitProveedor;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public String getCelular() {
		return celular;
	}

	public void setCelular(String celular) {
		this.celular = celular;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCiudad() {
		return ciudad;
	}

	public void setCiudad(String ciudad) {
		this.ciudad = ciudad;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public CategoriaProveedor getCategoria() {
		return categoria;
	}

	public void setCategoria(CategoriaProveedor categoria) {
		this.categoria = categoria;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	
	public String getTipoArchivo() {
		return tipoArchivo;
	}

	public void setTipoArchivo(String tipoArchivo) {
		this.tipoArchivo = tipoArchivo;
	}

	public MultipartFile getFile() {
		return file;
	}

	public void setFile(MultipartFile file) {
		this.file = file;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	@Override
	public String toString() {
		return "Proveedor [idProveedor=" + idProveedor + ", nitProveedor=" + nitProveedor + ", nombre=" + nombre
				+ ", telefono=" + telefono + ", celular=" + celular + ", email=" + email + ", ciudad=" + ciudad
				+ ", direccion=" + direccion + ", tipoArchivo=" + tipoArchivo + ", file=" + file + ", categoria="
				+ categoria + ", enabled=" + enabled + ", logo=" + logo + "]";
	}

	
}
