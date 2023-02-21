package com.halbig.userservice.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestHeader;

import com.halbig.userservice.service.UserService;
import com.mashape.unirest.http.exceptions.UnirestException;


@RestController
public class UserController {
	
	
	@Autowired
	private UserService userService;
	
	/**
	 * Generate client roles of logged in user
	 * 
	 * @param headers
	 * @return String list with all client roles
	 */
	@GetMapping("api/user/clientrole/currentuser")
	public ResponseEntity<Object> getClientRoleOfCurrentUser(@RequestHeader HttpHeaders headers) {
		try {
			return userService.getReturnOk(userService.decodeAccessTokenToClientRoles(headers));
		} catch (Exception e) {
			return userService.getForbidden(e.getMessage());
		}
	}
	
	/**
	 * Generate realm roles of logged in user
	 * 
	 * @param headers
	 * @return String list of realm roles
	 */
	@GetMapping("api/user/role/currentuser")
	public ResponseEntity<Object> getRoleOfCurrentUser(@RequestHeader HttpHeaders headers) {
		try {
			return userService.getReturnOk(userService.decodeAccessTokenToRoles(headers));
		} catch (Exception e) {
			return userService.getForbidden(e.getMessage());
		}
	}
	
	/**
	 * Get detailed user information of logged in user from token
	 * 
	 * @param headers
	 * @return user entity with details from token
	 */
	@GetMapping("api/user/current/details")
	public ResponseEntity<Object> getDetailUserInfo(@RequestHeader HttpHeaders headers) {
		try {
			return userService.getReturnOk(userService.getUser(headers));
		} catch (Exception e) {
			return userService.getForbidden(e.getMessage());
		}
	}
	
	/**
	 * Generate user id from token
	 * 
	 * @param headers
	 * @return user id as string
	 */
	@GetMapping("api/user/id")
	public ResponseEntity<Object> getCurrentUserId(@RequestHeader HttpHeaders headers) throws UnirestException {
		try {
			return userService.getReturnOk(userService.decodeAccessTokenToUserId(headers));
		} catch (Exception e) {
			return userService.getForbidden(e.getMessage());
		}
	}
		
}
