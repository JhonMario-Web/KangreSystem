package com.KangreSystem.models.service;

public interface ISimpleMailService {
	public void send(String nombre, String correoTo, String asunto, String mensaje) throws Exception;
}
