package com.auth.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.auth.bean.User;
import com.auth.repository.UserDao;

@Service(value = "userService")
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

	@Autowired
	private UserDao userDao;

	@Autowired
	private BCryptPasswordEncoder bcryptEncoder;

	@Override
	public UserDetails loadUserByUsername(final String username) {
		final User user = this.userDao.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("Username " + username + "not found"));
		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
				getAuthority(user));
	}

	private Set<GrantedAuthority> getAuthority(User user) {
		Set<GrantedAuthority> authorities = new HashSet<>();
		user.getRoles().forEach(role -> {
			authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
		});
		return authorities;
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