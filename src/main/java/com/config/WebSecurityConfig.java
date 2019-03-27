package com.config;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.auth.CustomLogoutSuccessHandler;
import com.auth.JWTLoginFilter;
import com.auth.JwtAuthenticationFilter;
import com.auth.JwtAuthenticationEntryPoint;
import com.service.UserDetailsService;

/**
 * Created by rajeevkumarsingh on 01/08/17.
 */

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private CustomLogoutSuccessHandler customLogouthandler;

	@Autowired
	private DataSource dataSource;

	@Autowired
	private JWTLoginFilter jwtLogin;

	@Autowired
	private CustomAuthenticationProvider authProvider;

	@Autowired
	private JwtAuthenticationEntryPoint unauthorizedHandler;

	@Autowired
	private JwtAuthenticationFilter jwtAuthFilter;

	@Resource(name = "userService")
	private UserDetailsService userDetailsService;

	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authProvider);
		// auth.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder());
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(256);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// Disable CSRF (cross site request forgery)
		http.csrf().disable();
		// No session will be created or used by spring security
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		// Entry points
		http.authorizeRequests()//
				.antMatchers("/api/v0/users/signin").permitAll()//
				.antMatchers("/api/v0/users/signup").permitAll()//
				// Disallow everything else..
				.anyRequest().authenticated();
		// UI related
		http.authorizeRequests().antMatchers("/", "/favicon.ico", "/**/*.png", "/**/*.gif", "/**/*.svg", "/**/*.jpg",
				"/**/*.html", "/**/*.css", "/**/*.js").permitAll();
		// Allow access to swagger only for Admin rule
		http.authorizeRequests().antMatchers("/v2/api-docs", "/swagger-ui.html", "/webjars/**", "/swagger-resources/**")
				.permitAll();// .hasRole("ADMIN");
		// If a user try to access a resource without having enough permissions
		http.exceptionHandling().authenticationEntryPoint(unauthorizedHandler);
		http.logout().logoutUrl("/api/v0/logout").logoutSuccessHandler(customLogouthandler);
		// Add our custom JWT security filter
		http.addFilterBefore(jwtLogin, UsernamePasswordAuthenticationFilter.class).addFilterBefore(jwtAuthFilter,
				UsernamePasswordAuthenticationFilter.class);
	}
}
