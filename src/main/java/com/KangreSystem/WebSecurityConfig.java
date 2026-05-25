package com.KangreSystem;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@EnableGlobalMethodSecurity(securedEnabled = true)
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter{
	
	@Autowired
	private DataSource dataSource;
	
	@Autowired
	private BCryptPasswordEncoder passEncoder;
	
	String[] resources = new String[] {
		"/", "/index", "/producto/catalogo/", "/sucursal/", "/sucursal/bogota", "/sucursal/chia", "/home","/sign-up/personal-info","/sign-up/terminos-condiciones", "/sign-up/preguntas-seguridad", "/sign-up/limpiar",
		"/sign-up/contrasenia","/quienes-somos", "/contactanos", "/restaurantes", "/include/**","/css/**","/icons/**","/img/**","/js/**","/layer/**"
	};
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers(resources).permitAll()
				/*
				 * .antMatchers("/","/index","/home").permitAll()
				 * .antMatchers("/bienvenida").hasAnyRole("USER")
				 * .antMatchers("/user/").hasAnyRole("ADMIN")
				 * .antMatchers("/user/edit/**").hasAnyRole("ADMIN")
				 * .antMatchers("/user/delete/**").hasAnyRole("ADMIN")
				 * .antMatchers("/proveedor/").hasAnyRole("ADMIN")
				 * .antMatchers("/rol/").hasAnyRole("ADMIN")
				 * .antMatchers("/rol/asignar-rol").hasAnyRole("ADMIN")
				 * .antMatchers("/empleado/").hasAnyRole("ADMIN")
				 * .antMatchers("/empleado/buscar").hasAnyRole("ADMIN")
				 * .antMatchers("/empleado/registro").hasAnyRole("ADMIN")
				 */
		.anyRequest().authenticated()
		.and()
		.formLogin().loginPage("/login")
		.permitAll()
		.and()
		.logout().permitAll();
	}
	
	@Autowired
	public void configurerSecurityGlobal(AuthenticationManagerBuilder builder) throws Exception {
		builder.jdbcAuthentication()
		.dataSource(dataSource)
		.passwordEncoder(passEncoder)
		.usersByUsernameQuery("SELECT username, password, enabled FROM users WHERE username=?")
		.authoritiesByUsernameQuery("SELECT u.username, r.rol FROM roles r INNER JOIN users u ON r.id_user = u.id_user WHERE u.username=?");
				
	}
}
