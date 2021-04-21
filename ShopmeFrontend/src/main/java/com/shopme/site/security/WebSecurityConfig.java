package com.shopme.site.security;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private DataSource dataSource;

	@Bean
	public UserDetailsService userDetailsService() {
		return new CustomerUserDetailsService();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
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
		http.authorizeRequests()
				.antMatchers("/oauth2/**").permitAll()
				.antMatchers("/customer/**", "/ask_question/**", "/checkout", "/cart/**", 
						"/change_password", "/place_order", "/post_question/**",
						"/getquestionvote/**", "/getreviewvote/**",
						"/process_paypal_order", "/write_review/**")
					.authenticated()
				.anyRequest().permitAll()
				.and()
				.formLogin()
					.loginPage("/login")
					.usernameParameter("email")
					.permitAll()
//				.and()
//				.oauth2Login()
//					.loginPage("/login")
//					.userInfoEndpoint()
//					.userService(oauth2UserService)
//					.and()
//					.successHandler(oauth2LoginSuccessHandler)
				.and()
				.logout()
					.permitAll()
				.and().rememberMe()
					.tokenRepository(persistentTokenRepository())
				.and().sessionManagement()
					.sessionCreationPolicy(SessionCreationPolicy.ALWAYS);
	}
		
	
	@Bean
	public PersistentTokenRepository persistentTokenRepository() {
		JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
		tokenRepository.setDataSource(dataSource);

		return tokenRepository;
	}
	
//	@Autowired
//	private CustomOAuth2UserService oauth2UserService;

//	@Autowired
//	private OAuth2LoginSuccessHandler oauth2LoginSuccessHandler;

}
