package com.KangreSystem.models.service;

import java.util.List;

import com.KangreSystem.models.entity.RespuestaSeguridad;

public interface IRespuestaSeguridadServ {

	public List<RespuestaSeguridad> listar();
	public void guardar(RespuestaSeguridad respuesta);
	public void eliminar(Long idRespuesta);
	public RespuestaSeguridad buscarPorId(Long idRespuesta);
	
}
