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
@Table(name = "insumos")
public class Insumo implements Serializable{

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_insumo")
	private Long idInsumo;
	
	private String nombre;
	
	@Column(name = "stock_actual")
	private Float stockActual;
	
	@Column(name = "stock_min")
	private Integer stockMin;
	
	@Column(name = "stock_max")
	private Integer stockMax;
	
	private Integer entradas;
	
	private Integer salidas;
	
	@ManyToOne
	@JoinColumn(name = "id_categoria")
	private CategoriaInsumo categoria;
	
	private String estado;	
	
	@ManyToOne
	@JoinColumn(name = "id_proveedor")
	private Proveedor proveedor;
	
	private Long precio;
	
	@Column(name = "tipo_archivo")
	private String tipoArchivo;
	
	@Transient
	private MultipartFile file;
	
	private boolean devolucion;
	
	public Insumo(String nombre, Float stockActual, Integer stockMin, Integer stockMax, Double entradas, Double salidas,
			CategoriaInsumo categoria, String estado, Proveedor proveedor,
			Long precio, String tipoArchivo) {
		super();
		this.nombre = nombre;
		this.stockActual = stockActual;
		this.stockMax = stockMax;
		this.stockMin = stockMin;
		this.categoria = categoria;
		this.estado = estado;
		this.proveedor = proveedor;
		this.precio = precio;
		this.tipoArchivo = tipoArchivo;
	}

	public Insumo() {
		
	}

	public Long getIdInsumo() {
		return idInsumo;
	}

	public void setIdInsumo(Long idInsumo) {
		this.idInsumo = idInsumo;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Float getStockActual() {
		return stockActual;
	}

	public void setStockActual(Float stockActual) {
		this.stockActual = stockActual;
	}

	public Integer getStockMin() {
		return stockMin;
	}

	public void setStockMin(Integer stockMin) {
		this.stockMin = stockMin;
	}

	public Integer getStockMax() {
		return stockMax;
	}

	public void setStockMax(Integer stockMax) {
		this.stockMax = stockMax;
	}

	public Integer getEntradas() {
		return entradas;
	}

	public void setEntradas(Integer entradas) {
		this.entradas = entradas;
	}

	public Integer getSalidas() {
		return salidas;
	}

	public void setSalidas(Integer salidas) {
		this.salidas = salidas;
	}

	public CategoriaInsumo getCategoria() {
		return categoria;
	}

	public void setCategoria(CategoriaInsumo categoria) {
		this.categoria = categoria;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public Proveedor getProveedor() {
		return proveedor;
	}

	public void setProveedor(Proveedor proveedor) {
		this.proveedor = proveedor;
	}

	public Long getPrecio() {
		return precio;
	}

	public void setPrecio(Long precio) {
		this.precio = precio;
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

	public boolean isDevolucion() {
		return devolucion;
	}

	public void setDevolucion(boolean devolucion) {
		this.devolucion = devolucion;
	}

	@Override
	public String toString() {
		return "Insumo [idInsumo=" + idInsumo + ", nombre=" + nombre + ", stockActual=" + stockActual + ", stockMin="
				+ stockMin + ", stockMax=" + stockMax + ", entradas=" + entradas + ", salidas=" + salidas
				+ ", categoria=" + categoria + ", estado=" + estado + ", proveedor=" + proveedor + ", precio=" + precio
				+ ", tipoArchivo=" + tipoArchivo + ", file=" + file + ", devolucion=" + devolucion + "]";
	}
	
	
}
