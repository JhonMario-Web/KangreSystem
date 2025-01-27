package com.KangreSystem.models.imp;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.StoredProcedureQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.KangreSystem.models.entity.RespuestaSeguridad;
import com.KangreSystem.models.entity.Rol;
import com.KangreSystem.models.entity.User;
import com.KangreSystem.models.repository.RespuestaSeguridadRepo;
import com.KangreSystem.models.repository.RolRepository;
import com.KangreSystem.models.repository.UserRepository;
import com.KangreSystem.models.service.ISimpleMailService;
import com.KangreSystem.models.service.IUserService;

@Service
public class UserServiceImp implements IUserService {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private EntityManager entityManager;
	
	@Autowired
	private RespuestaSeguridadRepo respuestaRepo;
	
	@Autowired
	private RolRepository rolRepo;
	
	@Autowired
	private ISimpleMailService mailService;
	
	@Autowired
	private BCryptPasswordEncoder passEncoder;
	
	@SuppressWarnings("unchecked")
	@Override
	public List<User> findAllViaProc() {
		StoredProcedureQuery storedProcedureQuery = this.entityManager.createNamedStoredProcedureQuery("getAllUsers");
		storedProcedureQuery.execute();
		return storedProcedureQuery.getResultList();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void guardar(User user) {
		
		user.setNombres(user.getNombres().toUpperCase());
		user.setApellidos(user.getApellidos().toUpperCase());
		user.setEmail(user.getEmail().toUpperCase());
		user.setDireccion(user.getDireccion().toUpperCase());
		user.setEdad(calcularEdad(new GregorianCalendar(user.getFechaNac().getYear(), user.getFechaNac().getMonth(), user.getFechaNac().getDate())) - 1900);
		
		userRepository.save(user);
	}

	@Override
	public void eliminar(Long idUser) {
		User user = userRepository.findById(idUser).orElse(null);
		List<Rol> roles =  rolRepo.findByUser(user);
		RespuestaSeguridad respuesta = respuestaRepo.findByUser(user);
		
		try {
			mailService.send("KangreSystem","<" + user.getEmail().toLowerCase() + ">", "CUENTA ELIMINADA", "Usted ha sido eliminado de KangreSystem");
			rolRepo.deleteAll(roles);
			respuestaRepo.delete(respuesta);
			
		} catch (Exception e) {
			 System.out.println(e.getMessage());
		}
		userRepository.deleteById(idUser);
	}

	@Override
	public User buscarPorId(Long idUser) {
		return userRepository.findById(idUser).orElse(null);
	}

	@Override
	public List<User> buscarUsuariosPorNumeroDoc(String numeroDoc) {
		if (!numeroDoc.isEmpty()) {
			return userRepository.findByUsernameContaining(numeroDoc);
		}
		return (List<User>) userRepository.findAll();
	}

	@Override
	public boolean existePorNumeroDoc(String numeroDoc) {
		return userRepository.existsByNumeroDoc(numeroDoc);
	}

	@Override
	public User buscarPorNumeroDoc(String numeroDoc) {
		return userRepository.findByNumeroDoc(numeroDoc);
	}

	@Override
	public boolean checkPassMatch(User user) throws Exception {
		
		if (!user.getPassword().equals(user.getConfirmPassword())) {
			throw new Exception("Las contraseñas no coinciden");
		}
		
		return true;
	}

	@Override
	public boolean checkEmailMatch(User user) throws Exception {
		
		if (!user.getEmail().toLowerCase().equals(user.getConfirmEmail().toLowerCase())) {
			throw new Exception("Las correos no coinciden");
		}
		
		return true;
	}

	@Override
	public boolean existsByEmail(User user){
		return userRepository.existsByEmail(user.getEmail());
	}

	@Override
	public boolean existsByNumeroDoc(User user) {
		return userRepository.existsByNumeroDoc(user.getNumeroDoc());
	}
	
	public static Integer calcularEdad(Calendar fechaNac) {
		Calendar fechaHoy = Calendar.getInstance();

		int anios = fechaHoy.get(Calendar.YEAR) - fechaNac.get(Calendar.YEAR);
		int meses = fechaHoy.get(Calendar.MONTH) - fechaNac.get(Calendar.MONTH);
		int dias = fechaHoy.get(Calendar.DAY_OF_MONTH) - fechaNac.get(Calendar.DAY_OF_MONTH);

		if (meses < 0 || (meses == 0 && dias < 0)) { // Aun no es el mes de su cumpleaños
			anios--; // o es el mes pero no ha llegado el dia.
		}

		return anios;
	}
	
	public static String calcularAntiguedad(Calendar fechaReg) {
		Calendar fechaHoy = Calendar.getInstance();

		int anios = fechaHoy.get(Calendar.YEAR) - fechaReg.get(Calendar.YEAR);
		int meses = fechaHoy.get(Calendar.MONTH) - fechaReg.get(Calendar.MONTH);
		int dias = fechaHoy.get(Calendar.DAY_OF_MONTH) - fechaReg.get(Calendar.DAY_OF_MONTH);
		
		if (meses < 0 || (meses == 0 && dias < 0)) { // Aun no es el mes de su cumpleaños
			anios--; // o es el mes pero no ha llegado el dia.
		}
		
		String antiguedad = dias + "D " + meses + "M " + (anios - 1900) + "A";

		return antiguedad;
	}

	@Override
	public List<User> buscarPorUsernameContaining(String username) {
		return userRepository.findByUsernameContaining(username);
	}

	@Override
	public List<User> buscarPorGenero(Character genero) {
		return userRepository.findByGenero(genero);
	}

	@Override
	public List<User> buscarPorCiudad(String ciudad) {
		return userRepository.findByCiudad(ciudad);
	}

	@Override
	public List<User> buscarPorEnabled(boolean enabled) {
		return userRepository.findByEnabled(enabled);
	}

	@Override
	public List<User> buscarPorGeneroCiudad(String genero, String ciudad) {
		return userRepository.findByGeneroAndCiudad(genero.charAt(0), ciudad);
	}

	@Override
	public List<User> buscarPorGeneroEnabled(String genero, boolean enabled) {
		return userRepository.findByGeneroAndEnabled(genero.charAt(0), enabled);
	}

	@Override
	public List<User> buscarPorCiudadEnabled(String ciudad, boolean enabled) {
		return userRepository.findByCiudadAndEnabled(ciudad, enabled);
	}

	@Override
	public List<User> filtrar(String genero, String ciudad, String enabled) {
		
		if (genero.toString().isEmpty() && ciudad.isEmpty() && enabled.isEmpty()) {
			return null;
		}
		
		if (!genero.toString().isEmpty() && ciudad.isEmpty() && enabled.isEmpty()) {
			return buscarPorGenero(genero.charAt(0));
		}
		
		if (genero.toString().isEmpty() && !ciudad.isEmpty() && enabled.isEmpty()) {
			return buscarPorCiudad(ciudad);
		}
		
		if (genero.toString().isEmpty() && ciudad.isEmpty() && !enabled.isEmpty()) {
			return buscarPorEnabled(Boolean.parseBoolean(enabled));
		}
		
		if (!genero.toString().isEmpty() && !ciudad.isEmpty() && enabled.isEmpty()) {
			return buscarPorGeneroCiudad(genero, ciudad);
		}
		
		if (!genero.toString().isEmpty() && ciudad.isEmpty() && !enabled.isEmpty()) {
			return buscarPorGeneroEnabled(genero, Boolean.parseBoolean(enabled));
		}
		
		if (genero.toString().isEmpty() && !ciudad.isEmpty() && !enabled.isEmpty()) {
			return buscarPorCiudadEnabled(ciudad, Boolean.parseBoolean(enabled));
		}
		
		return buscarPorGeneroCiudadEnabled(genero, ciudad, Boolean.parseBoolean(enabled));
		
	}

	@Override
	public List<User> buscarPorGeneroCiudadEnabled(String genero, String ciudad, boolean enabled) {
		return userRepository.findByGeneroAndCiudadAndEnabled(genero.charAt(0), ciudad, enabled);
	}

	@Override
	public void resetPassword(User user) {
		String newPassword = passEncoder.encode(user.getUsername());
		user.setPassword(newPassword);
		userRepository.save(user);
	}

	@Override
	public long contarTodos() {
		return userRepository.count();
	}

	@Override
	public boolean checkPassPuntosMatch(User user, String pass) {
		return passEncoder.matches(pass, user.getPasswordPuntos());
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean validFechaNacimiento(User user) {
		
		int edad = (calcularEdad(new GregorianCalendar(user.getFechaNac().getYear(), user.getFechaNac().getMonth(), user.getFechaNac().getDate())) - 1900);
		
		if (edad < 18) {
			return false;
		}
		
		return true;
	}

}
