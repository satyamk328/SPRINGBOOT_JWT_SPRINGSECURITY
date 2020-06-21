package com.auth.handler;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.auth.bean.User;
import com.auth.exception.UnAuthorizedException;
import com.auth.service.UserDetailsImpl;
import com.auth.spring.model.RestResponse;
import com.auth.spring.model.RestStatus;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AuthFailureHandler extends SimpleUrlAuthenticationFailureHandler {

	public static final ObjectMapper mapper = new ObjectMapper();

	@Autowired
	private UserDetailsImpl userDetailsService;

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {

		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setHeader("Content-Type", "application/json");

		String errorMessage = "Unknown username or bad password";
		User user = null;
		String userName = "Anonymous";
		try {
			final String[] creds = JWTLoginFilter.getBasicCred(request);
			if (creds != null && creds.length == 2) {
				user = (User) userDetailsService.loadUserByUsername(creds[0]);
				if(user != null)
					userName = user.getUsername();
			}
		} catch (Exception e) {
		}
		if (exception instanceof UnAuthorizedException) {
			errorMessage = "User is not authorised to use the Admin Application";
		}

		PrintWriter writer = response.getWriter();
		RestResponse<String> responseObj = new RestResponse<>(null, new RestStatus<>("UNAUTHORIZED", errorMessage));
		String result = mapper.writeValueAsString(responseObj);
		log.debug("Login Response : {}", result);
		writer.write(result);
		writer.flush();
	}
}