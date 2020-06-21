package com.auth.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.auth.bean.User;
import com.auth.service.UserDetailsImpl;
import com.auth.spring.model.RestResponse;
import com.auth.spring.model.RestStatus;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/api/v0/user")
@Api(value = "DepartmentController")
public class UserController {

	interface Constants {
		String FETCH_RECORDS = "Record fetch successfully";
		String NOT_FOUND = "Record not found";
		String ADD_RECORD = "Record added successfully";
		String UPDATE_RECORD = "Record updated successfully";
		String DELETE_MSG = "Record deleted successfully";
	}
	
	@Autowired
	private UserDetailsImpl userService;

	@ApiOperation(value = "Get All Users")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Your request was successful"),
			@ApiResponse(code = 400, message = "Your request is not accepted"),
			@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
			@ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
			@ApiResponse(code = 408, message = "Your request has timed out"),
			@ApiResponse(code = 409, message = "There was a resource conflict"),
			@ApiResponse(code = 500, message = "Generic server error"),
			@ApiResponse(code = 503, message = "Server Unavailable timeout") })
	@GetMapping(value = "/")
	public ResponseEntity<RestResponse<List<User>>> listUser() {
		RestStatus<?> restStatus = new RestStatus<String>(HttpStatus.OK.toString(), Constants.FETCH_RECORDS);
		List<User> page = userService.findAll();
		return new ResponseEntity<>(new RestResponse(page, restStatus), HttpStatus.OK);
	}


	@SuppressWarnings("rawtypes")
	@ApiOperation(value = "Get User By Id")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Your request was successful"),
			@ApiResponse(code = 400, message = "Your request is not accepted"),
			@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
			@ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
			@ApiResponse(code = 408, message = "Your request has timed out"),
			@ApiResponse(code = 409, message = "There was a resource conflict"),
			@ApiResponse(code = 500, message = "Generic server error"),
			@ApiResponse(code = 503, message = "Server Unavailable timeout") })
	@GetMapping(value = "/{id}")
	public ResponseEntity<RestResponse<?>> getUserDetails(@PathVariable(value = "id") Long id) {
		RestStatus<?> restStatus = new RestStatus<String>(HttpStatus.OK.toString(), Constants.FETCH_RECORDS);
		User page = userService.findById(id);
		return new ResponseEntity<>(new RestResponse(page, restStatus), HttpStatus.OK);
	}

	@ApiOperation(value = "Add User")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Your request was successful"),
			@ApiResponse(code = 400, message = "Your request is not accepted"),
			@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
			@ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
			@ApiResponse(code = 408, message = "Your request has timed out"),
			@ApiResponse(code = 409, message = "There was a resource conflict"),
			@ApiResponse(code = 500, message = "Generic server error"),
			@ApiResponse(code = 503, message = "Server Unavailable timeout") })
	@PostMapping(value = "/signup")
	public ResponseEntity<RestResponse<RestStatus<Object>>> saveUser(@RequestBody(required = true) User user) {
		
		RestStatus<?> restStatus = new RestStatus<String>(HttpStatus.OK.toString(), Constants.ADD_RECORD);
		User u = userService.saveUser(user);
		return new ResponseEntity<>(new RestResponse(u, restStatus), HttpStatus.OK);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<RestResponse<RestStatus<?>>> deleteUser(@PathVariable(value = "id") Long id) {
		userService.delete(id);
		RestStatus<?> restStatus = new RestStatus<String>(HttpStatus.OK.toString(), Constants.DELETE_MSG);
		return new ResponseEntity<>(new RestResponse(null, restStatus), HttpStatus.OK);
	}

}
