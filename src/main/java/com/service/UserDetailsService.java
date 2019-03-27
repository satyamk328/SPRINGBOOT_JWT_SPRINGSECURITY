package com.service;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.bean.Role;
import com.bean.RoleName;


@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

	@Override
	public UserDetails loadUserByUsername(final String username) {
		final ConsumerLoginModel user = consumerDao.getConsumerLoginModelByName(username);

		if (user == null) {
			throw new UsernameNotFoundException(username);
		}

		user.setRole(Arrays.asList(new Role(RoleName.ADMIN)));
		return new UserPrincipal(user);
	}

}