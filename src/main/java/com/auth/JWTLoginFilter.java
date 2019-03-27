package com.auth;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Collections;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;

@Component
public class JWTLoginFilter extends AbstractAuthenticationProcessingFilter
		implements ApplicationContextAware, InitializingBean {

	@Autowired
	private AuthSuccessHandler authSuccessHandler;

	@Autowired
	private AuthFailureHandler authFailureHandler;

	static String url = "/api/v0/login";

	@Autowired
	private AuthenticationManager authManager;

	private ApplicationContext ctx;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		ctx = applicationContext;
	}

	@Override
	public void afterPropertiesSet() {
		setFilterProcessesUrl(url);
		setAuthenticationManager(authManager);
		authSuccessHandler = ctx.getBean(AuthSuccessHandler.class);
		setAuthenticationSuccessHandler(authSuccessHandler);
		setAuthenticationFailureHandler(authFailureHandler);
	}

	public JWTLoginFilter() {
		super(new AntPathRequestMatcher(url));
		setAuthenticationManager(authManager);
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res)
			throws AuthenticationException, IOException, ServletException {
		String[] cred = getBasicCred(req);
		if (cred != null && cred.length == 2) {
			Authentication auth = getAuthenticationManager()
					.authenticate(new UsernamePasswordAuthenticationToken(cred[0], cred[1], Collections.emptyList()));
			getAuthenticationManager().authenticate(auth);
			return auth;
		} else {
			throw new BadCredentialsException("No JWT token found in request headers");
		}
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		super.successfulAuthentication(request, response, chain, authResult);
		// As this authentication is in HTTP header, after success we need to continue
		// the request normally
		// and return the response as if the resource was not secured at all
		// chain.doFilter(request, response);
	}

	public static String[] getBasicCred(HttpServletRequest req) {
		String authorization = req.getHeader("Authorization");
		String[] values = null;
		if (authorization != null && authorization.startsWith("Basic")) {
			// Authorization: Basic base64credentials
			String base64Credentials = authorization.substring("Basic".length()).trim();
			String credentials = new String(Base64.getDecoder().decode(base64Credentials), Charset.forName("UTF-8"));
			// credentials = username:password
			values = credentials.split(":", 2);
		}
		return values;
	}
}
