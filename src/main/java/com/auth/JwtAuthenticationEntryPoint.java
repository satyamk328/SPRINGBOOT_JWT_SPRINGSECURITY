package com.auth;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.erp.spring.model.RestResponse;
import com.erp.spring.model.RestStatus;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by rajeevkumarsingh on 07/12/17.
 */
@Component
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

	public static final ObjectMapper mapper = new ObjectMapper();

	@Override
	public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			AuthenticationException authException) throws IOException, ServletException {
		// This is invoked when user tries to access a secured REST resource without
		// supplying any credentials
		// We should just send a 401 Unauthorized response because there is no 'login
		// page' to redirect to
		log.error("Responding with unauthorized error. Message - {}", authException.getMessage());
		httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

		PrintWriter writer = httpServletResponse.getWriter();
		RestResponse<String> responseObj = new RestResponse<>(null,
				new RestStatus<>("401_UNAUTHORIZED", authException.getMessage()));

		String result = mapper.writeValueAsString(responseObj);
		writer.write(result);
		writer.flush();
	}
}