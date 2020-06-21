package com.auth.config;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.auth.handler.CustomLogoutSuccessHandler;
import com.auth.handler.JWTLoginFilter;
import com.auth.handler.JwtAuthenticationEntryPoint;
import com.auth.handler.JwtAuthenticationFilter;
import com.auth.service.UserDetailsImpl;

/**
 * Created by Satyam Kumar.
 */

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private CustomLogoutSuccessHandler customLogouthandler;

	@Autowired
	private JWTLoginFilter jwtLogin;

	@Autowired
	private JwtAuthenticationEntryPoint unauthorizedHandler;

	@Autowired
	private JwtAuthenticationFilter jwtAuthFilter;

	@Resource(name = "userService")
	private UserDetailsImpl userDetailsService;

	@Autowired
	public void configAuthentication(final AuthenticationManagerBuilder auth) throws Exception {
		final DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(userDetailsService);
		provider.setPasswordEncoder(passwordEncoder());
		provider.afterPropertiesSet();
		auth.authenticationProvider(provider);
	}


	@Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// Disable CSRF (cross site request forgery)
		http.csrf().disable();
		// No session will be created or used by spring security
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		// Entry points
		http.authorizeRequests()//
				//.antMatchers("/api/v0/login").permitAll()//
				.antMatchers("/api/v0/user/signup").permitAll()//
				// Disallow everything else..
				.anyRequest().authenticated();
		// Allow access to swagger only for Admin rule
		// If a user try to access a resource without having enough permissions
		http.exceptionHandling().authenticationEntryPoint(unauthorizedHandler);
		http.logout().logoutUrl("/api/v0/logout").logoutSuccessHandler(customLogouthandler);
		// Add our custom JWT security filter
		http.addFilterBefore(jwtLogin, UsernamePasswordAuthenticationFilter.class).addFilterBefore(jwtAuthFilter,
				UsernamePasswordAuthenticationFilter.class);
	}
}
