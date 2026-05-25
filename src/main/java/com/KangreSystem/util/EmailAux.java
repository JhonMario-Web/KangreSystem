package com.KangreSystem.util;

import java.util.ArrayList;
import java.util.List;

import com.KangreSystem.models.entity.Administrador;
import com.KangreSystem.models.entity.Cliente;
import com.KangreSystem.models.entity.Empleado;
import com.KangreSystem.models.entity.Proveedor;
import com.KangreSystem.models.entity.User;

public class EmailAux {
	
	private int item;
	
	private String correo;
	
	public EmailAux() {
		
	}
	
	public int getItem() {
		return item;
	}

	public void setItem(int item) {
		this.item = item;
	}

	public String getCorreo() {
		return correo;
	}

	public void setCorreo(String correo) {
		this.correo = correo;
	}

	@Override
	public String toString() {
		return "EmailAux [item=" + item + ", correo=" + correo + "]";
	}
	
	public static List<String> getCorreosAdmins(List<Administrador> admins) {
		List<String> correos = new ArrayList<>();
		Administrador admin = new Administrador();                    
												     
		for (int i = 0; i < admins.size(); i++) { 
			admin = admins.get(i);
			correos.add(admin.getUser().getEmail().toLowerCase());
		}
		return correos;

	}

	public static List<String> getCorreosUsuarios(List<User> usuarios) {
		List<String> correos = new ArrayList<>();
		User user = new User();                    
												     
		for (int i = 0; i < usuarios.size(); i++) { 
			user = usuarios.get(i);
			correos.add(user.getEmail().toLowerCase());
		}
		return correos;

	}
	
	public static List<String> getCorreosClientes(List<Cliente> clientes) {
		List<String> correos = new ArrayList<>();
		Cliente cliente = new Cliente();                    
												     
		for (int i = 0; i < clientes.size(); i++) { 
			cliente = clientes.get(i);
			correos.add(cliente.getUser().getEmail().toLowerCase());
		}
		return correos;

	}

	public static List<String> getCorreosEmpleados(List<Empleado> empleados) {
		List<String> correos = new ArrayList<>();
		Empleado empleado = new Empleado();                    
												     
		for (int i = 0; i < empleados.size(); i++) { 
			empleado = empleados.get(i);
			correos.add(empleado.getUser().getEmail().toLowerCase());
		}
		return correos;

	}

	public static List<String> getCorreosProveedores(List<Proveedor> proveedores) {
		List<String> correos = new ArrayList<>();
		Proveedor proveedor = new Proveedor();                    
												     
		for (int i = 0; i < proveedores.size(); i++) { 
			proveedor = proveedores.get(i);
			correos.add(proveedor.getEmail().toLowerCase());
		}
		return correos;

	}
	
	public static String[] getCorreoAdmins(List<Administrador> admins) {
		String correos[] = new String[admins.size()]; 
		Administrador admin = new Administrador();                    
												     
		for (int i = 0; i < correos.length; i++) { 
			admin = admins.get(i);					
			correos[i] = admin.getUser().getEmail().toLowerCase(); 
		}
		return correos;

	}

	public static String[] getCorreoUsuarios(List<User> users) {
		String correos[] = new String[users.size()]; 
		User user = new User();                    
												     
		for (int i = 0; i < correos.length; i++) { 
			user = users.get(i);					
			correos[i] = user.getEmail().toLowerCase(); 
		}
		return correos;

	}
	
	public static String[] getCorreoClientes(List<Cliente> clientes) {
		String correos[] = new String[clientes.size()];
		Cliente cliente = new Cliente();

		for (int i = 0; i < correos.length; i++) {
			cliente = clientes.get(i);
			correos[i] = cliente.getUser().getEmail();
		}

		return correos;
	}

	public static String[] getCorreoEmpleados(List<Empleado> empleados) {
		String correos[] = new String[empleados.size()];
		Empleado empleado = new Empleado();

		for (int i = 0; i < correos.length; i++) {
			empleado = empleados.get(i);
			correos[i] = empleado.getUser().getEmail();
		}

		return correos;
	}

	public static String[] getCorreoProveedores(List<Proveedor> proveedores) {
		String correos[] = new String[proveedores.size()];
		Proveedor proveedor = new Proveedor();

		for (int i = 0; i < correos.length; i++) {
			proveedor = proveedores.get(i);
			correos[i] = proveedor.getEmail();
		}

		return correos;
	}
	
}

