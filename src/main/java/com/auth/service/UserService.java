package com.auth.service;

import java.util.List;

import com.auth.bean.User;

public interface UserService {

	User findByName(final String username);
	
	public List<User> findAll() ;

	public User findById(Long id) ;

	public void delete(long id) ;

	public User saveUser(User user);
}
