package com.shopme.admin.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
//@EnableGlobalMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Bean
	public UserDetailsService userDetailsService() {
		return new ShopmeUserDetailsService();
	}
	
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		
		authProvider.setUserDetailsService(userDetailsService());
		authProvider.setPasswordEncoder(passwordEncoder());
		
		return authProvider;
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authenticationProvider());
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
//		http.authorizeRequests().antMatchers("/").permitAll();
		http.authorizeRequests()
//			.antMatchers("/images/**", "/js/**").permitAll()
			.antMatchers("/login").permitAll()
			.antMatchers("/users/**", "/settings/**").hasAuthority("Admin")
			.antMatchers("/products/new", "/products/delete/**").hasAnyAuthority("Admin", "Editor")
			.antMatchers("/products/save", "/products/edit/**", "/products/check_unique").hasAnyAuthority("Admin", "Editor", "Salesperson")
			.antMatchers("/products", "/products/", "/products/page/**", "/products/detail/**").hasAnyAuthority("Admin", "Editor", "Salesperson", "Shipper")
			.antMatchers("/products/**").hasAnyAuthority("Admin", "Editor")
			.anyRequest().authenticated()
			.and()
			.formLogin()
				.loginPage("/login")
				.usernameParameter("email")
				.permitAll()
			.and()
			.rememberMe().key("AbcdEfghIjklmNopQrsTuvXyz_0123456789")
			.and()
			.logout().permitAll();
		http.headers().frameOptions().sameOrigin();
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/images/**", "/js/**", "/webjars/**");
	}
	
}
