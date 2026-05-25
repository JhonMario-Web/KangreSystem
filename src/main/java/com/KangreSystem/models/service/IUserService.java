package com.KangreSystem.models.service;

import java.util.List;
import com.KangreSystem.models.entity.User;

public interface IUserService{
	
	//METODOS C R U D
	public void guardar(User user);
	public void eliminar(Long idUser);
	public List<User> findAllViaProc();
	
	//METODOS PARA LA BUSQUEDA DINAMICA
	public List<User> filtrar(String genero, String ciudad, String enabled);
	public List<User> buscarPorUsernameContaining(String username);
	public List<User> buscarPorGenero(Character genero);
	public List<User> buscarPorCiudad(String ciudad);
	public List<User> buscarPorEnabled(boolean enabled);
	public List<User> buscarPorGeneroCiudad(String genero, String ciudad);
	public List<User> buscarPorGeneroEnabled(String genero, boolean enabled);
	public List<User> buscarPorCiudadEnabled(String ciudad, boolean enabled);
	public List<User> buscarPorGeneroCiudadEnabled(String genero, String ciudad, boolean enabled);
	
	//METODOS AUXILIARES
	public User buscarPorId(Long idUser);
	public User buscarPorNumeroDoc(String numeroDoc);
	public boolean existePorNumeroDoc(String numeroDoc);
	public boolean checkPassMatch(User user) throws Exception;
	public boolean checkPassPuntosMatch(User user, String pass);
	public boolean checkEmailMatch(User user) throws Exception;
	public boolean existsByEmail(User user);
	public boolean existsByNumeroDoc(User user);
	public List<User> buscarUsuariosPorNumeroDoc(String numeroDoc);
	public void resetPassword(User user);
	public long contarTodos();
	
	//METODOS LOGICOS DEL SISTEMA
	public boolean validFechaNacimiento(User user);
	
	
}
