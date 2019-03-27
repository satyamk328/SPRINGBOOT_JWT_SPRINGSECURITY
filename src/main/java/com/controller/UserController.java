package com.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.erp.spring.model.RestResponse;
import com.erp.spring.model.RestStatus;

@RestController
@RequestMapping("/api/v0")
public class UserController {

	public ResponseEntity<RestResponse<RestStatus<Object>>> signin() {
		return null;
		
	}

}
