package com.KangreSystem.models.imp;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.KangreSystem.models.entity.Pais;
import com.KangreSystem.models.repository.PaisRepository;
import com.KangreSystem.models.service.IPaisService;

@Service
public class PaisServiceImp implements IPaisService {

	@Autowired
	private PaisRepository paisRepository;
	
	@Override
	public List<Pais> listarPaises() {
		return (List<Pais>) paisRepository.findAll();
	}

}
