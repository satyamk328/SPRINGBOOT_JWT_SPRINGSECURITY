package com.auth.config;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.auth.bean.Role;
import com.auth.bean.User;
import com.auth.exception.UnAuthorizedException;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CustomAuthenticationProvider implements AuthenticationProvider {
	// @Autowired
	// LdapAuthentication ldapLogin;

	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String userName = authentication.getName().trim();
		String password = authentication.getCredentials().toString().trim();
		Authentication auth = null;
		// Authenticate the user based on your custom logic

		// LdapUser ldapuser = null;

		// ldapuser = ldapLogin.getApplicationRole(userName, password);

		if (null == null) {
			throw new UnAuthorizedException("User is not authorised to use the Admin Application");
		}

		List<Role> roles = null;// ldapuser.getRoles();

		List<SimpleGrantedAuthority> grantedAuthList = new ArrayList<>();

		for (Role role : roles) {
			log.debug("Role :{}", role.getId());
			SimpleGrantedAuthority grantedAuths = new SimpleGrantedAuthority(
					role.getName().name().trim().toUpperCase());
			grantedAuthList.add(grantedAuths);
		}
		User user = null;
		auth = new UsernamePasswordAuthenticationToken(user, password, grantedAuthList);
		return auth;
	}

	@Override
	public boolean supports(Class<? extends Object> authentication) {
		return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
	}

}
