package com.KangreSystem.models.imp;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.KangreSystem.models.entity.RespuestaSeguridad;
import com.KangreSystem.models.repository.RespuestaSeguridadRepo;
import com.KangreSystem.models.service.IRespuestaSeguridadServ;

@Service
public class RespuestaSeguridadServImp implements IRespuestaSeguridadServ {
	
	@Autowired
	private RespuestaSeguridadRepo respuestaRepository;

	@Override
	public List<RespuestaSeguridad> listar() {
		return (List<RespuestaSeguridad>) respuestaRepository.findAll();
	}

	@Override
	public void guardar(RespuestaSeguridad respuesta) {
		respuestaRepository.save(respuesta);
	}

	@Override
	public void eliminar(Long idRespuesta) {
		respuestaRepository.deleteById(idRespuesta);
	}

	@Override
	public RespuestaSeguridad buscarPorId(Long idRespuesta) {
		return respuestaRepository.findById(idRespuesta).orElse(null);
	}

}
