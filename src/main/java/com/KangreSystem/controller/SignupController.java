package com.KangreSystem.controller;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.KangreSystem.models.entity.Pais;
import com.KangreSystem.models.entity.RespuestaSeguridad;
import com.KangreSystem.models.entity.User;
import com.KangreSystem.models.repository.UserRepository;
import com.KangreSystem.models.service.IPaisService;
import com.KangreSystem.models.service.IRespuestaSeguridadServ;
import com.KangreSystem.models.service.ISimpleMailService;
import com.KangreSystem.models.service.IUserService;

@Controller
@RequestMapping("/sign-up")
public class SignupController {
	
	@Autowired
	private IPaisService paisService;
	
	@Autowired
	private IUserService userService;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private IRespuestaSeguridadServ respuestaService;
	
	@Autowired
	private BCryptPasswordEncoder passEncoder;
	
	@Autowired
	private ISimpleMailService mailService;
	
	private String terminos = "El acceso al Portal por parte de los Visitantes es libre y gratuito "
			+ "para personas mayores de edad. En caso de ser Usted menor de edad, debe obtener con "
			+ "anterioridad el consentimiento de sus padres, tutores o representantes legales, quienes "
			+ "serán responsables de los actos que Usted lleve a cabo en contravención a estos términos "
			+ "y condiciones de uso del Portal. Se da por entendido que los menores de edad que accedan "
			+ "y usen el Portal cuentan con este consentimiento. El acceso al Portal permite acceder a "
			+ "toda la información publicada por Publicaciones KangreSystem S.A. (en adelante, '"
			+ "KangreSystem'), por los Ciberperiodistas y a algunos blogs destacados (en adelante, el '"
			+ "Contenido'). El acceso a las funcionalidades de Comunidades requiere un registro previ"
			+ "o, en las condiciones que se describen en las Condiciones de Acceso y Uso de las Comu"
			+"nidades. Condiciones de Uso del Contenido Público El Contenido (que incluye o puede in"
			+ "cluir textos, información, imágenes, fotografías, dibujos, logos, diseños, video, mul"
			+ "timedia, software, aplicaciones, música, sonidos, entre otros, así como su selección y"
			+ " disposición), es propiedad exclusiva de KangreSystem, sus anunciantes, o de terceros"
			+ " que hayan otorgado una licencia a KangreSystem, con todos los derechos reservados. C"
			+ "omo tal, dicho Contenido se encuentra protegido por las leyes y tratados internaciona"
			+ "les vigentes en materia de Propiedad Intelectual. KangreSystem confiere a Usted una li"
			+ "cencia para visualizar el Contenido en el Portal, y para realizar una copia caché en s"
			+ "u computador con dicho fin únicamente. Este documento puede ser impreso y almacenado p"
			+ "or Usted. Aparte de lo anterior, KangreSystem no confiere a los Visitantes ninguna lic"
			+ "encia para descargar, reproducir, copiar, enmarcar, compilar, cargar o republicar en ningún "
			+ "sitio de Internet, Intranet o Extranet, adaptar, modificar, transmitir, vender ni comunicar "
			+ "al público, total o parcialmente, el Contenido. Cualquiera de estas actividades requiere de "
			+ "la autorización previa, expresa y por escrito de KangreSystem, so pena de incurrir en "
			+ "violación a los derechos de propiedad industrial e intelectual, y someterse a las consecuencias"
			+ " civiles y penales de tal hecho, así como al derecho de KangreSystem de revocar la licencia "
			+ "aquí conferida. Salvo que se indique expresamente lo contrario en el presente Contrato, nada"
			+ " de lo dispuesto en los presentes Términos y Condiciones de Uso del Portal deberá interpretarse"
			+ " en el sentido de otorgar una licencia sobre derechos de propiedad intelectual, ya sea por "
			+ "impedimento legal, implícitamente o de cualquier otra forma. Esta licencia podrá ser revocada "
			+ "en cualquier momento y sin preaviso, con o sin causa. Usted se compromete a hacer un uso adecuado"
			+ " del Contenido. De manera enunciativa pero no limitativa, Usted se compromete a no: "
			+ "Utilizar el Contenido para incurrir y/o incitar a terceros a incurrir en actividades ilícitas, "
			+ "ilegales o contrarias a la buena fe y al orden público, o para difundir contenidos o propaganda "
			+ "de carácter racista, xenófobo, pornográfico-ilegal, de apología del terrorismo o atentatorio "
			+ "contra los derechos humanos; Usar secuencias de comandos automatizadas para recopilar información "
			+ "publicada en el Portal o a través del Portal o para interactuar de cualquier otro modo con los "
			+ "mismos; provocar daños en los sistemas físicos y lógicos de KangreSystem, de sus proveedores o de "
			+ "terceras personas, introducir o difundir en la red virus informáticos, troyanos, código malicioso "
			+ "o cualesquiera otros sistemas físicos o lógicos que sean susceptibles de provocar daños en y/o "
			+ "estén diseñados para interrumpir, destruir o limitar la funcionalidad de cualquier software, "
			+ "hardware o equipo de telecomunicaciones o para dañar, deshabilitar, sobrecargar o perjudicar el "
			+ "Portal de cualquier modo; y intentar acceder, recolectar o almacenar los datos personales de otros "
			+ "Visitantes y/o Usuarios del Portal y, en su caso, utilizar las cuentas de correo electrónico de "
			+ "otros Visitantes y/o Usuarios y modificar o manipular sus mensajes. Cookies Este portal hace "
			+ "uso de cookies propias y de terceros. Tenga en cuenta que el uso de la cookies va a permitir "
			+ "optimizar su experiencia en este portal. ¿Qué son las cookies? Una cookie es un fichero que se"
			+ " descarga en el ordenador/smartphone/tablet del usuario al acceder a determinadas páginas web. "
			+ "Finalidades de las cookies PROYECTOS KangreSystem S.A. y/o PUBLICACIONES KangreSystem S.A. harán "
			+ "uso de las cookies para: - determinar sus preferencias de navegación - para efectos promocionales, "
			+ "comerciales y publicitarios - para efectos estadísticos, entre otros fines. Aceptación de uso de "
			+ "Cookies Al aceptar estos “Términos y condiciones”, Usted acepta que PUBLICACIONES KangreSystem S.A;";
	
	@GetMapping("/personal-info")
	public String signUp(Model model) {
		List<Pais> paises = paisService.listarPaises();
		model.addAttribute("titulo", "Registro");
		model.addAttribute("subtitulo", "1: Informacion personal");
		model.addAttribute("user", new User());
		model.addAttribute("paises", paises);
		return "/Views/Registro/registroU";
	}
	
	@PostMapping("/personal-info")
	public String signUp(@ModelAttribute User user, RedirectAttributes atribute, Model model) {
		user.setUsername(user.getNumeroDoc());
		String encodedPassword = passEncoder.encode(user.getNumeroDoc());
		
		user.setPassword(encodedPassword);
		user.setEnabled(true);
		user.setTerminos(false);
		user.setFechaRegistro(new Date());
		
		try {

			if (!userService.validFechaNacimiento(user)) {
				model.addAttribute("error", "Fecha de nacimiento invalida, recuerde que debe tener min 18 años para registrarte!");
				model.addAttribute("titulo", "Registro");
				model.addAttribute("subtitulo", "1: Informacion personal");
				model.addAttribute("paises", paisService.listarPaises());
				model.addAttribute("user", user);
				return "/Views/Registro/registroU";
			}
			
			userService.guardar(user);
		} catch (Exception e) {
			model.addAttribute("error", e.getMessage());
			model.addAttribute("titulo", "Registro");
			model.addAttribute("subtitulo", "1: Informacion personal");
			model.addAttribute("paises", paisService.listarPaises());
			model.addAttribute("user", user);
			return "/Views/Registro/registroU";
		}
		
		atribute.addFlashAttribute("user", user);
		System.out.println("voy a terminos");
		return "redirect:/sign-up/terminos-condiciones";
		
	}
	
	@GetMapping("/terminos-condiciones")
	public String terminosCondiciones(Model model) {
		User user = (User) model.getAttribute("user");
		System.out.println(user);
		if (user != null) {
			System.out.println("estoy en terminos");
			model.addAttribute("titulo", "Registro");
			model.addAttribute("subtitulo", "2: Terminos y condiciones");		
			model.addAttribute("terminos", terminos);
			return "/Views/Registro/terminosCondiciones";
		}
		
		return "redirect:/sign-up/personal-info";
		
	}

	@PostMapping("/terminos-condiciones")
	public String terminosCondiciones(@ModelAttribute User user, RedirectAttributes atribute) {
		System.out.println(user.getFechaRegistro());
		
		RespuestaSeguridad respuesta = new RespuestaSeguridad();
		respuesta.setUser(user);
		userRepository.save(user);
		atribute.addFlashAttribute("respuesta", respuesta);
		
		return "redirect:/sign-up/preguntas-seguridad";
	}
	
	@GetMapping("/preguntas-seguridad")
	public String preguntasSeguridad(Model model) {
		
		RespuestaSeguridad respuesta = (RespuestaSeguridad) model.getAttribute("respuesta");
		if (respuesta != null) {
			System.out.println("estoy en terminos");
			model.addAttribute("titulo", "Registro");
			model.addAttribute("subtitulo", "3: Preguntas de seguridad");
			return "/Views/Registro/preguntasSeguridad";
		}
		
		return "redirect:/sign-up/personal-info";
	}
	
	
	@PostMapping("/preguntas-seguridad")
	public String preguntasSeguridad(@ModelAttribute RespuestaSeguridad respuesta, RedirectAttributes atribute) {
		respuesta.setRespuestaUno(respuesta.getRespuestaUno().toLowerCase());
		respuesta.setRespuestaDos(respuesta.getRespuestaDos().toLowerCase());
		respuesta.setRespuestaTres(respuesta.getRespuestaTres().toLowerCase());
		
		respuestaService.guardar(respuesta);
		atribute.addFlashAttribute("user", respuesta.getUser());
		
		System.out.println("ANTES "+respuesta.getUser().getFechaRegistro());
		return "redirect:/sign-up/contrasenia";
	}
	
	@GetMapping("/contrasenia")
	public String contraseña(Model model, @Param("idUser") Long idUser) {
		
		User user = (User) model.getAttribute("user");
		if (user != null) {
			model.addAttribute("titulo", "Registro");
			model.addAttribute("subtitulo", "4: Asignar contraseña");
			return "/Views/Registro/asignarContraseña";
		}
		
		return "redirect:/sign-up/personal-info";
	}
	
	@PostMapping("/contrasenia")
	public String contraseña(@ModelAttribute User user, RedirectAttributes atribute, Model model) {
		
		String encodedPassword = passEncoder.encode(user.getPassword());
		user.setPassword(encodedPassword);
		user.setFechaRegistro(new Date());
		
		userRepository.save(user);
		try {
			mailService.send("KangreSystem","<" + user.getEmail().toLowerCase() + ">", "REGISTRO EXISTOSO", "Felicidades su registro se ha realizado exitosamente");
		} catch (Exception e) {
			model.addAttribute("titulo", "Registro");
			model.addAttribute("user", user);
			model.addAttribute("subtitulo", "4: Asignar contraseña");
			model.addAttribute("error", e.getMessage());
			return "/Views/Registro/asignarContraseña";
		}
    		
		atribute.addFlashAttribute("success", "Se ha registrado correctamente!");
		System.out.println("Registro exitoso");
		return "redirect:/login";
	}
	
	@GetMapping("/restablecer-contraseña")
	public String restablecerContraseña() {
		return "/Views/Registro/restablecerContraseña";
	}
	
	@GetMapping("/codigoContraseña")
	public String codigoContraseña() {
		return "/Views/Registro/codigoContraseña";
	}
	
	@GetMapping("/limpiar")
	public String limpiar(Model model) {
		return "redirect:/sign-up/personal-info";
	}
	
	
}
