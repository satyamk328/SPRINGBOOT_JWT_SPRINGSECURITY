package com.auth.handler;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.auth.bean.ProfileVO;
import com.auth.bean.UserPrincipal;
import com.auth.spring.model.RestResponse;
import com.auth.spring.model.RestStatus;
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

			UserPrincipal user = (UserPrincipal) auth.getPrincipal();

			List<String> userRoles = new ArrayList<>();
			for (GrantedAuthority o : user.getAuthorities()) {
				userRoles.add(o.getAuthority());
			}

			ProfileVO profile = new ProfileVO();

			// profile.setUserContext(user);
			profile.setStatus("SUCCESS");
			profile.setType(user.getAuthorities().toArray()[0].toString());

			PrintWriter writer = response.getWriter();
			response.setStatus(HttpServletResponse.SC_OK);
			profile.setAccessToken(jwt.split(" ")[1]);
			profile.setExpiresIn(TokenAuthenticationService.getExpirationTime());
			profile.setTokenType(jwt.split(" ")[0]);

			final RestResponse<ProfileVO> responseObj = new RestResponse<>(profile,
					new RestStatus<>("200", "Login Success"));
			final String result = mapper.writeValueAsString(responseObj);
			log.info("Login Response : " + result);
			writer.write(result);
			writer.flush();

		} catch (Exception e) {
			log.error("Unable to complete authentication Success ", e);
		}
	}
}
