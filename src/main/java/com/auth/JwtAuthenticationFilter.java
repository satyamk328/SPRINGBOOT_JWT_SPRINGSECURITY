package com.auth;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by rajeevkumarsingh on 19/08/17.
 */

@Component
@Slf4j
public class JwtAuthenticationFilter extends GenericFilterBean {

	@Autowired
	private TokenAuthenticationService authenticationService;

	// @Autowired
	// private DBLoggingHandler dbLoggingHandler;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
			throws IOException, ServletException {
		try {
			Authentication authentication = authenticationService.getAuthentication((HttpServletRequest) request);
			SecurityContextHolder.getContext().setAuthentication(authentication);
		} catch (Exception ex) {
			log.error("Exception Setting Authentication Context", ex);
			// dbLoggingHandler.handleException("Exception Setting Authentication Context",
			// ex);
		}
		filterChain.doFilter(request, response);
	}

}
