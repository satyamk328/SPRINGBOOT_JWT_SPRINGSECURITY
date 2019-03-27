package com.auth;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.bean.ProfileVO;
import com.erp.spring.model.RestResponse;
import com.erp.spring.model.RestStatus;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AuthSuccessHandler implements AuthenticationSuccessHandler {

	public static final ObjectMapper mapper = new ObjectMapper();

	@Autowired
	private TokenAuthenticationService authenticationService;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) {
		// We do not need to do anything extra on REST authentication success, because
		// there is no page to redirect to
		response.setStatus(HttpServletResponse.SC_OK);
		response.setHeader("Content-Type", "application/json");
		response.setHeader("Access-Control-Expose-Headers", "Authentication");
		try {
			String jwt = authenticationService.addAuthentication(response, authentication.getName(), request);

			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			User user = (User) auth.getPrincipal();

			List<String> userRoles = new ArrayList<>();
			for (GrantedAuthority o : user.getAuthorities()) {
				userRoles.add(o.getAuthority());
			}

			ProfileVO profile = new ProfileVO();

			profile.setUser(user.getUsername());
			profile.setStatus("SUCCESS");
			profile.setType(user.getAuthorities().toArray()[0].toString());

			PrintWriter writer = response.getWriter();
			response.setStatus(HttpServletResponse.SC_OK);
			profile.setAccessToken(jwt.split(" ")[1]);
			profile.setExpiresIn(TokenAuthenticationService.getExpirationTime());
			profile.setTokenType(jwt.split(" ")[0]);

			RestResponse<ProfileVO> responseObj = new RestResponse<ProfileVO>(profile,
					new RestStatus<>("200", "Login Success"));
			String result = mapper.writeValueAsString(profile);
			log.info("Login Response : " + result);
			writer.write(mapper.writeValueAsString(responseObj));
			writer.flush();

		} catch (Exception e) {
			log.error("Unable to complete authentication Success ", e);
			// loggingHandler.handleException("Unable to complete authentication Success ",
			// e);
		}
	}

	public void populateNagativeResponse(final HttpServletResponse response, final String cmsMessageKey) {
		try {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			final PrintWriter writer = response.getWriter();
			final RestResponse<String> responseObj = new RestResponse<>(null,
					new RestStatus<>("UNAUTHORIZED", "Incorrect Email ID/ User ID and Password error message"));
			final String result = mapper.writeValueAsString(responseObj);
			log.info("Login Response : " + result);
			writer.write(result);
			writer.flush();
		} catch (final IOException e) {
			log.error("Unable to complete the login failure", e);
			// dbLoggingHandler.handleException("Unable to complete the login failure", e);
		}

	}
}
