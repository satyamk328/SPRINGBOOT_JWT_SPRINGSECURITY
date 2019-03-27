package com.auth;


import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.erp.spring.model.RestResponse;
import com.erp.spring.model.RestStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.model.ProfileVO;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AuthSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

	public static final ObjectMapper mapper = new ObjectMapper();
	
	@Autowired
	private TokenAuthenticationService authenticationService;
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		try {
			String jwt = authenticationService.addAuthentication(response, authentication.getName(), request);

			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			User user = (User) auth.getPrincipal();
			
			List<String> userRoles = new ArrayList<>();
			for (GrantedAuthority o : user.getAuthorities()) {
				userRoles.add(o.getAuthority());
			}

			response.setStatus(HttpServletResponse.SC_OK);
			response.setHeader("Content-Type", "application/json");

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
			//loggingHandler.handleException("Unable to complete authentication Success ", e);
		}
	}
}
