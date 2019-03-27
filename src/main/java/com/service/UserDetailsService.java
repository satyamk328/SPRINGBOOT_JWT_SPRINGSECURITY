package com.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.bean.User;
import com.dao.UserDao;

@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

	@Autowired
	private UserDao userDao;

	@Override
	public UserDetails loadUserByUsername(final String username) {
		final User user = this.userDao.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("Username " + username + "not found"));

		if (user == null) {
			throw new UsernameNotFoundException(username);
		}
		return user;

	}

}