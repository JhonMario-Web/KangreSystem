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
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "puntos_transferidos")
public class PuntoTransferido implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "id_trxn")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idTrxn;
	
	@ManyToOne
	@JoinColumn(name = "id_cliente")
	private Cliente cliente;
	
	@ManyToOne
	@JoinColumn(name = "trxn_acum")
	private PuntoAcumulado acumulado;
	
	@ManyToOne
	@JoinColumn(name = "id_favorito")
	private Favorito favorito;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date fecha;
	
	@DateTimeFormat(pattern = "hh:mm")
	private Date hora;

	private Integer transferidos;
	
	private Integer total;
	
	public PuntoTransferido() {
		
	}

	public Long getIdTrxn() {
		return idTrxn;
	}

	public void setIdTrxn(Long idTrxn) {
		this.idTrxn = idTrxn;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public PuntoAcumulado getAcumulado() {
		return acumulado;
	}

	public void setAcumulado(PuntoAcumulado acumulado) {
		this.acumulado = acumulado;
	}

	public Favorito getFavorito() {
		return favorito;
	}

	public void setFavorito(Favorito favorito) {
		this.favorito = favorito;
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

	public Integer getTransferidos() {
		return transferidos;
	}

	public void setTransferidos(Integer transferidos) {
		this.transferidos = transferidos;
	}

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	@Override
	public String toString() {
		return "PuntoTransferido [idTrxn=" + idTrxn + ", cliente=" + cliente + ", acumulado=" + acumulado
				+ ", favorito=" + favorito + ", fecha=" + fecha + ", hora=" + hora + ", transferidos=" + transferidos
				+ ", total=" + total + "]";
	}
	
}
