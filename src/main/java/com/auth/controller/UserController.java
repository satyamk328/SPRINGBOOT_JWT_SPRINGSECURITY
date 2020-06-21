package com.auth.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.auth.bean.User;
import com.auth.service.UserDetailsService;
import com.auth.spring.model.RestResponse;
import com.auth.spring.model.RestStatus;

@RestController
@RequestMapping("/api/v0/users")
public class UserController {

	@Autowired
	private UserDetailsService userService;

	//@Secured({"ROLE_ADMIN", "ROLE_USER"})
    @PreAuthorize("hasRole('ADMIN')")
	@GetMapping(value = "/")
	public List<User> listUser() {
		return userService.findAll();
	}

	// @Secured("ROLE_USER")
	// @PreAuthorize("hasRole('USER')")
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	@GetMapping(value = "/{id}")
	public User getUserDetails(@PathVariable(value = "id") Long id) {
		return userService.findById(id);
	}

	@PostMapping(value = "/signup")
	public ResponseEntity<RestResponse<RestStatus<Object>>> saveUser(@RequestBody(required = true) User user) {
		User u = userService.saveUser(user);
		return null;

	}

	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(value = "/user/{id}", method = RequestMethod.DELETE)
	public User deleteUser(@PathVariable(value = "id") Long id) {
		userService.delete(id);
		return null;
	}

}
