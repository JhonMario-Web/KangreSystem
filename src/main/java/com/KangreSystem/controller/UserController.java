package com.KangreSystem.controller;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.document.AbstractXlsxView;

import com.KangreSystem.models.entity.User;
import com.KangreSystem.models.repository.UserRepository;
import com.KangreSystem.models.service.IPaisService;
import com.KangreSystem.models.service.IRolService;
import com.KangreSystem.models.service.IUserService;
import com.KangreSystem.util.UserPDFExporter;
import com.lowagie.text.DocumentException;

@Controller
@RequestMapping("/user")
@Secured("ROLE_ADMIN")
@Component("/Views/SI/User/users.xlsx")
public class UserController extends AbstractXlsxView {

	@Autowired
	private IUserService userService;
	
	@Autowired
	private IRolService rolService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private IPaisService paisService;

	private List<User> users;
	
	private List<User> usersX;
	
	private long usuarios;
	
	private long admins;
	
	private long cajeros;
	
	private long cocineros;
	
	private long meseros;
	
	private Map<String, Long> usuariosMap;

	@GetMapping("/")
	public String listAll(Model model) {
		users = userService.findAllViaProc();
		model.addAttribute("usuarios", users);
		model.addAttribute("titulo", "Usuarios");
		
		usuarios = userService.contarTodos();
		admins = rolService.contarPorRol("ROLE_ADMIN");
		cajeros = rolService.contarPorRol("ROLE_CAJERO");
		cocineros = rolService.contarPorRol("ROLE_COCINERO");
		meseros = rolService.contarPorRol("ROLE_MESERO");
		
		usuariosMap = new LinkedHashMap<>();
		usuariosMap.put("ADMINISTRADORES", admins);
		usuariosMap.put("COCINEROS", cocineros);
		usuariosMap.put("CAJEROS", cajeros);
		usuariosMap.put("MESEROS", meseros);
		
		model.addAttribute("usuariosMap", usuariosMap);
		model.addAttribute("usuariosTotal", usuarios);
		model.addAttribute("admins", admins);
		model.addAttribute("cocineros", cocineros);
		model.addAttribute("meseros", meseros);
		model.addAttribute("cajeros", cajeros);
		return "/Views/SI/User/users";
	}

	@PostMapping("/")
	public String buscar(@Param("usernameModal") String usernameModal, @Param("ciudadModal") String ciudadModal,
			@Param("enabledModal") String enabledModal, @Param("generoModal") String generoModal, Model model) {

		if (!validFilter(usernameModal, generoModal, ciudadModal, enabledModal)) {
			users = userService.findAllViaProc();
			usersX = null;
			usersX = users;
			model.addAttribute("warning", "No se puede filtrar por esos filtros!");
			return listAll(model);
		}

		if (usernameModal.isEmpty() && generoModal.equals("") && ciudadModal.isEmpty() && enabledModal.isEmpty()) {
			users = userService.findAllViaProc();
			usersX = null;
			usersX = users;
			model.addAttribute("warning", "No se encontraron criterios de busqueda!");
			return listAll(model);
		}

		if (!usernameModal.isEmpty() && generoModal.equals("") && ciudadModal.isEmpty() && enabledModal.isEmpty()) {
			users = userService.buscarPorUsernameContaining(usernameModal);
			usersX = null;
			usersX = users;
			if (users.isEmpty()) {
				model.addAttribute("warning", "No se encontraron resultados");
				users = userService.findAllViaProc();
				usersX = null;
				usersX = users;
			}
			
			usuariosMap = new LinkedHashMap<>();
			usuariosMap.put("ADMINISTRADORES", admins);
			usuariosMap.put("COCINEROS", cocineros);
			usuariosMap.put("CAJEROS", cajeros);
			usuariosMap.put("MESEROS", meseros);
			
			model.addAttribute("usuariosMap", usuariosMap);
			model.addAttribute("usuariosTotal", usuarios);
			model.addAttribute("admins", admins);
			model.addAttribute("cocineros", cocineros);
			model.addAttribute("meseros", meseros);
			model.addAttribute("cajeros", cajeros);
			model.addAttribute("usuarios", users);
			return "Views/SI/User/users";
		}

		users = userService.filtrar(generoModal, ciudadModal, enabledModal);
		usersX = null;
		usersX = users;

		if (users.isEmpty()) {
			model.addAttribute("warning", "No se encontraron resultados");
			users = userService.findAllViaProc();
			usersX = null;
			usersX = users;
		}

		model.addAttribute("titulo", "Usuarios");
		model.addAttribute("usuarios", users);
		return "/Views/SI/User/users";
	}
	
	@GetMapping("/pdf")	
	public void exportToPDF(HttpServletResponse response) throws DocumentException, IOException {
		response.setContentType("application/pdf");
		
		DateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
		String currentDateTime = dateFormatter.format(new Date());
		
		String headerKey = "Content-Disposition";
		String headerValue = "attachment; filename=Usuarios_" + currentDateTime +".pdf";
		
		response.setHeader(headerKey, headerValue);
		
		if (usersX == null) {
			UserPDFExporter exporter = new UserPDFExporter(userService.findAllViaProc());
			exporter.export(response);
		}
		else {
			UserPDFExporter exporter = new UserPDFExporter(this.usersX);
			exporter.export(response);
		}
		
	}

	@GetMapping("/limpiar")
	public String limpiar() {
		usersX = null;
		return "redirect:/user/";
	}

	@GetMapping("/edit/{id}")
	public String edit(@PathVariable("id") Long idUser, Model model, RedirectAttributes attr) {
		User user = null;

		if (idUser > 0) {
			user = userService.buscarPorId(idUser);

			if (user == null) {
				System.out.println("El id del usuario no existe!");
				attr.addFlashAttribute("error", "El id del usuario a editar no existe!");
				return "redirect:/user/";
			}

		} else {
			System.out.println("El id del usuario no existe!");
			attr.addFlashAttribute("error", "El id del usuario a editar no existe!");
			return "redirect:/user/";
		}

		model.addAttribute("user", user);
		model.addAttribute("paises", paisService.listarPaises());
		model.addAttribute("titulo", "Editar usuario");
		return "/Views/SI/User/editUser";
	}

	@PostMapping("/edit")
	public String editUser(@ModelAttribute User user, Model model, RedirectAttributes attr) {

		User userAux = userRepository.findById(user.getIdUser()).orElse(null);
		user = oldToNewUser(user, userAux);
		String oldEmail = userAux.getEmail().toLowerCase();
		String newEmail = user.getEmail().toLowerCase();

		if (userRepository.existsByEmail(user.getEmail()) && !oldEmail.equals(newEmail)) {
			model.addAttribute("error", "El correo electronico a actualizar ya esta siendo utilizado!");
			user.setEmail(userAux.getEmail());
			model.addAttribute("user", user);
			model.addAttribute("paises", paisService.listarPaises());
			model.addAttribute("titulo", "Editar usuario");
			return "/Views/SI/User/editUser";
		}

		userRepository.save(user);
		attr.addFlashAttribute("success", "Usuario actualizado correctamente!");
		return "redirect:/user/";

	}
	
	@GetMapping("/details/{idUser}")
	public ResponseEntity<User> details(@PathVariable("idUser") Long idUser) {
		try {
			return new ResponseEntity<User>(userService.buscarPorId(idUser), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<User>(HttpStatus.BAD_REQUEST);
		}
		
	}

	@GetMapping("/delete/{id}")
	public String delete(@PathVariable("id") Long idUser, RedirectAttributes attribute, Model model) {
		userService.eliminar(idUser);
		attribute.addFlashAttribute("success", "Usuario eliminado exitosamente");
		return "redirect:/user/";
	}

	@GetMapping("/reset-password/{id}")
	public String resetPassword(@PathVariable("id") Long idUser, RedirectAttributes attr) {
		User user = userService.buscarPorId(idUser);
		userService.resetPassword(user);
		attr.addFlashAttribute("success", "La contraseña se ha restablecido correctamente!");
		return "redirect:/user/";
	}

	public boolean validFilter(String username, String genero, String ciudad, String enabled) {
		if ((!username.isEmpty() && !genero.isEmpty() && !ciudad.isEmpty() && !enabled.isEmpty())
				|| (!username.isEmpty() && !genero.isEmpty() && ciudad.isEmpty() && enabled.isEmpty())
				|| (!username.isEmpty() && genero.isEmpty() && !ciudad.isEmpty() && enabled.isEmpty())
				|| (!username.isEmpty() && genero.isEmpty() && ciudad.isEmpty() && !enabled.isEmpty())) {
			return false;
		}
		return true;
	}

	public User oldToNewUser(User newUser, User oldUser) {
		newUser.setFechaRegistro(oldUser.getFechaRegistro());
		newUser.setGenero(oldUser.getGenero());
		newUser.setNumeroDoc(oldUser.getNumeroDoc());
		newUser.setPassword(oldUser.getPassword());
		newUser.setTipoDoc(oldUser.getTipoDoc());
		newUser.setUsername(oldUser.getUsername());
		newUser.setNombres(newUser.getNombres().toUpperCase());
		newUser.setApellidos(newUser.getApellidos().toUpperCase());
		newUser.setDireccion(newUser.getDireccion().toUpperCase());
		newUser.setEmail(newUser.getEmail().toUpperCase());
		return newUser;
	}

	@Override
	protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setHeader("Content-Disposition", "attachment; filename=\"users.xlsx\"");
		Sheet hoja = workbook.createSheet("users");

		CellStyle style = workbook.createCellStyle();
		XSSFFont font = (XSSFFont) workbook.createFont();
		font.setBold(true);
		font.setFontHeight(16);
		style.setFont(font);

		Row filaTitulo = hoja.createRow(0);
		Cell celda = filaTitulo.createCell(0);
		celda.setCellValue("");
		celda.setCellStyle(style);

		Row filaData = hoja.createRow(0);
		String[] columnas = { "ID", "Numero documento", "Tipo documento", "Nombres", "Apellidos", "Celular",
				"Direccion", "Ciudad", "Email", "Pais origen", "Estado" };

		for (int i = 0; i < columnas.length; i++) {
			celda = filaData.createCell(i);
			celda.setCellValue(columnas[i]);
			celda.setCellStyle(style);
		}

		int numFila = 1;

		if (usersX == null) {
			usersX = userService.findAllViaProc();
		}

		for (User user : usersX) {
			filaData = hoja.createRow(numFila);

			filaData.createCell(0).setCellValue(user.getIdUser());
			hoja.autoSizeColumn(0);
			filaData.createCell(1).setCellValue(user.getNumeroDoc());
			hoja.autoSizeColumn(1);
			filaData.createCell(2).setCellValue(user.getTipoDoc());
			hoja.autoSizeColumn(2);
			filaData.createCell(3).setCellValue(user.getNombres());
			hoja.autoSizeColumn(3);
			filaData.createCell(4).setCellValue(user.getApellidos());
			hoja.autoSizeColumn(4);
			filaData.createCell(5).setCellValue(user.getCelular());
			hoja.autoSizeColumn(5);
			filaData.createCell(6).setCellValue(user.getDireccion());
			hoja.autoSizeColumn(6);
			filaData.createCell(7).setCellValue(user.getCiudad());
			hoja.autoSizeColumn(7);
			filaData.createCell(8).setCellValue(user.getEmail());
			hoja.autoSizeColumn(8);
			filaData.createCell(9).setCellValue(user.getPais().getPais());
			hoja.autoSizeColumn(9);
			filaData.createCell(10).setCellValue(user.isEnabled());
			hoja.autoSizeColumn(10);
			numFila++;
		}

	}

}
