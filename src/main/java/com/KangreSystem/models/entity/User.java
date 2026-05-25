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
import javax.persistence.NamedStoredProcedureQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
@NamedStoredProcedureQuery(name = "getAllUsers", procedureName = "get_all_users", resultClasses = {User.class})
@Table(name = "users")
public class User implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_user")
	private Long idUser;
	
	@Column(unique = true)
	private String username;
	
	@Column(nullable = true)
	private String password;
	
	@Column(name = "password_puntos")
	private String passwordPuntos;
	
	@Transient
	private String confirmPasswordPuntos;

	@Transient
	private String confirmPassword;
	private boolean enabled;
	
	@Column(name = "numero_doc")
	private String numeroDoc;
	
	@Column(name = "tipo_doc")
	private String tipoDoc;
	private String nombres;
	private String apellidos;
	private String celular;
	private Character genero;
	private String telefono;
	private String direccion;
	private String ciudad;
	private String email;
	
	@Transient
	private String confirmEmail;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@Column(name = "f_nacimiento")
	private Date fechaNac;
	private Integer edad;
	
	@ManyToOne
	@JoinColumn(name = "id_pais")
	private Pais pais;
	
	@Column(name = "fecha_registro")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date fechaRegistro;
	
	private boolean terminos;
	
	
	public User() {
		
	}
	
	public User( String username, String password, String confirmPassword,
			String numeroDoc, String tipoDoc, String nombres, String apellidos, String celular, Character genero,
			String telefono, String direccion, String ciudad, String email, String confirmEmail, Date fechaNac, Pais pais,
			Date fechaRegistro, boolean terminos) {
		super();
		this.username = username;
		this.password = password;
		this.confirmPassword = confirmPassword;
		this.numeroDoc = numeroDoc;
		this.tipoDoc = tipoDoc;
		this.nombres = nombres;
		this.apellidos = apellidos;
		this.celular = celular;
		this.genero = genero;
		this.telefono = telefono;
		this.direccion = direccion;
		this.ciudad = ciudad;
		this.email = email;
		this.confirmEmail = confirmEmail;
		this.fechaNac = fechaNac;
		this.pais = pais;
		this.fechaRegistro = fechaRegistro;
		this.terminos = terminos;
	}

	public Long getIdUser() {
		return idUser;
	}

	public void setIdUser(Long idUser) {
		this.idUser = idUser;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPasswordPuntos() {
		return passwordPuntos;
	}

	public void setPasswordPuntos(String passwordPuntos) {
		this.passwordPuntos = passwordPuntos;
	}

	public String getConfirmPasswordPuntos() {
		return confirmPasswordPuntos;
	}

	public void setConfirmPasswordPuntos(String confirmPasswordPuntos) {
		this.confirmPasswordPuntos = confirmPasswordPuntos;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getNumeroDoc() {
		return numeroDoc;
	}

	public void setNumeroDoc(String numeroDoc) {
		this.numeroDoc = numeroDoc;
	}

	public String getTipoDoc() {
		return tipoDoc;
	}

	public void setTipoDoc(String tipoDoc) {
		this.tipoDoc = tipoDoc;
	}

	public String getNombres() {
		return nombres;
	}

	public void setNombres(String nombres) {
		this.nombres = nombres;
	}

	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	public String getCelular() {
		return celular;
	}

	public void setCelular(String celular) {
		this.celular = celular;
	}

	public Character getGenero() {
		return genero;
	}

	public void setGenero(Character genero) {
		this.genero = genero;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public String getCiudad() {
		return ciudad;
	}

	public void setCiudad(String ciudad) {
		this.ciudad = ciudad;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getConfirmEmail() {
		return confirmEmail;
	}

	public void setConfirmEmail(String confirmEmail) {
		this.confirmEmail = confirmEmail;
	}

	public Date getFechaNac() {
		return fechaNac;
	}

	public void setFechaNac(Date fechaNac) {
		this.fechaNac = fechaNac;
	}

	public Integer getEdad() {
		return edad;
	}

	public void setEdad(Integer edad) {
		this.edad = edad;
	}

	public Pais getPais() {
		return pais;
	}

	public void setPais(Pais pais) {
		this.pais = pais;
	}

	public Date getFechaRegistro() {
		return fechaRegistro;
	}

	public void setFechaRegistro(Date fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}

	public boolean isTerminos() {
		return terminos;
	}

	public void setTerminos(boolean terminos) {
		this.terminos = terminos;
	}

	@Override
	public String toString() {
		return "User [idUser=" + idUser + ", username=" + username + ", password=" + password + ", passwordPuntos="
				+ passwordPuntos + ", confirmPasswordPuntos=" + confirmPasswordPuntos + ", confirmPassword="
				+ confirmPassword + ", enabled=" + enabled + ", numeroDoc=" + numeroDoc + ", tipoDoc=" + tipoDoc
				+ ", nombres=" + nombres + ", apellidos=" + apellidos + ", celular=" + celular + ", genero=" + genero
				+ ", telefono=" + telefono + ", direccion=" + direccion + ", ciudad=" + ciudad + ", email=" + email
				+ ", confirmEmail=" + confirmEmail + ", fechaNac=" + fechaNac + ", edad=" + edad + ", pais=" + pais
				+ ", fechaRegistro=" + fechaRegistro + ", terminos=" + terminos + "]";
	}
}
