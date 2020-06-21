package com.auth.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.auth.bean.User;
import com.auth.bean.UserPrincipal;
import com.auth.repository.UserDao;

@Service(value = "userService")
public class UserDetailsImpl implements org.springframework.security.core.userdetails.UserDetailsService {

	@Autowired
	private UserDao userDao;

	@Autowired
	private BCryptPasswordEncoder bcryptEncoder;

	@Override
	public UserDetails loadUserByUsername(final String username) {
		final User user = this.userDao.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("Username " + username + "not found"));
		return UserPrincipal.build(user);
	}


	public List<User> findAll() {
		return userDao.findAll();
	}

	public User findById(Long id) {
		return userDao.findById(id).get();
	}

	public void delete(long id) {
		userDao.deleteById(id);
	}

	public User saveUser(User user) {
		user.setPassword(bcryptEncoder.encode(user.getPassword()));
		userDao.save(user);
		return user;
	}

}