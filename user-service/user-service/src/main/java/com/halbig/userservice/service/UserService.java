package com.halbig.userservice.service;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.halbig.userservice.common.Roles;
import com.halbig.userservice.common.StatusEnum;
import com.halbig.userservice.data.Status;
import com.halbig.userservice.data.StatusJpa;
import com.halbig.userservice.data.User;
import com.mashape.unirest.http.exceptions.UnirestException;


@Component
public class UserService {
	
	@Autowired
	public StatusJpa statusJpa;
	
	@Autowired 
	public RestTemplate restTemplate;
	
	//user controller
	/**
	 * Generate user id from token
	 * 
	 * @param headers
	 * @return userId from token
	 */
	public String decodeAccessTokenToUserId(HttpHeaders headers) {
		String user_id = "";
		try {
			JSONObject jsonObject = new JSONObject(this.getPayloadFromHeader(headers));
			user_id = jsonObject.getString("sub");
		}catch (JSONException e){
			System.out.println("No user ID found in response");
		}
		return user_id;
	}

	/**
	 * Generate realm roles from token
	 * 
	 * @param headers
	 * @return 
	 */
	public String decodeAccessTokenToRoles(HttpHeaders headers) {
		String user_roles = "";
		try {
			JSONObject jsonObject = new JSONObject(this.getPayloadFromHeader(headers));
			user_roles = jsonObject.getJSONObject("resource_access").toString(); 
		}catch (JSONException e){
			return e.getMessage();
		}
		return user_roles;
	}
	
	/**
	 * Generate user informations from decoded JWT token
	 * 
	 * @param userId
	 * @param headers
	 * @return Entity of user class with required attributes
	 */
	public User getUser(HttpHeaders headers) throws UnirestException {
		try {
		JSONObject jsonObject = new JSONObject(this.getPayloadFromHeader(headers));
		return new User(jsonObject.get("sub").toString(), 
				jsonObject.get("preferred_username").toString(),
				jsonObject.get("given_name").toString(), 
				jsonObject.get("family_name").toString(), 
				jsonObject.get("email").toString());
		} catch (JSONException e) {
			return null;
		}
	}
	
	/**
	 * Generate client roles from token
	 * 
	 * @param headers
	 * @return roles as string array list
	 */
	public ArrayList<String> decodeAccessTokenToClientRoles(HttpHeaders headers) {
		ArrayList<String> roles = new ArrayList<String>();
		JSONArray user_roles;
		try {
			JSONObject jsonObject = new JSONObject(this.getPayloadFromHeader(headers));
			user_roles = jsonObject
					.getJSONObject("resource_access")
					.getJSONObject("demo-spring-boot")
					.getJSONArray("roles"); 
		} catch (JSONException e) {
			return null;
		}
		if (user_roles != null) {
			for (int i=0;i<user_roles.length();i++){
				roles.add(user_roles.getString(i));
			}
		}
		return roles;
	}
	
	//status controller
	/**
	 * Create status with userId and taskId
	 * @param taskId
	 * @param userId
	 */
	public String creationOfStatus(int taskId, String userId) {
		Status newStatus = new Status(taskId);
		newStatus.setUser(userId);
		statusJpa.save(newStatus);
		return "Status created for user " + userId;
	}
	
	/**
	 * Set status attribute of status to corresponding Enum
	 * @param statusId
	 * @param statusEnum
	 * @return success message
	 */
	public String setStatusInStatus(int statusId, StatusEnum statusEnum) {
		Status s = statusJpa.findById(statusId);
		s.setStatus(statusEnum);
		statusJpa.save(s);
		return "Status " + statusId + " changed to " + statusEnum; 
	}
	

	/**
	 * Check if user is admin according to JWT-Token (copy from courseService to avoid restTemplate requests)
	 * @param headers
	 * @return  boolean true if user is admin
	 */
	public boolean isAdmin(HttpHeaders headers) {
		return this.decodeAccessTokenToClientRoles(headers).contains(Roles.admin);
	} 
	
	/**
	 * create status for subscriber
	 * 
	 * @param taskId
	 * @param headers
	 * @return success message
	 */
	public String createStatusForTasks(int taskId, HttpHeaders headers) throws AccessDeniedException {
		String checkedUsers = restTemplate.exchange("http://course-service/api/course/task/" + taskId + "/subscriber", 
				HttpMethod.GET, new HttpEntity<String>(headers), String.class).getBody().toString();
		if (checkedUsers == null) {
			throw new AccessDeniedException("Only admin and subcriber are able to create a status for current logged in user");
		}
		creationOfStatus(taskId, decodeAccessTokenToUserId(headers));
		return "Status created for task " + taskId;
	}
	
	/**
	 * Delete all tasks that are allocated to one task
	 * 
	 * @param taskId
	 * @return success message
	 */
	public String deleteAllStatusFromTask(int taskId) {
		for (Status s: statusJpa.findAllByTaskId(taskId)) {
			statusJpa.delete(s);
		}
		return "Status deleted for task " + taskId;
	}
	
	/**
	 * Get creator from course uuid
	 * 
	 * @param uuid
	 * @param headers
	 * @return creator ID of course
	 */
	public String getCreatorIdFromCourse(String uuid, HttpHeaders headers) {
		return restTemplate.exchange("http://course-service/api/course/" + uuid + "/creator", 
				HttpMethod.GET, new HttpEntity<String>(headers), String.class).getBody().toString();
	}
	
	public String getSubscirberFromTaskId(int taskId, HttpHeaders headers) {
		return restTemplate.exchange("http://course-service/api/course/task/" + taskId + "/subscriber", 
				HttpMethod.GET, new HttpEntity<String>(headers), String.class).getBody().toString();
	}
	
	/**
	 * Get creator from task id
	 * 
	 * @param uuid
	 * @param headers
	 * @return creator ID of course from task
	 */
	public String getCreatorIdFromTask(int taskId, HttpHeaders headers) {
		return restTemplate.exchange("http://course-service/api/course/task/" + taskId + "/creator", 
				HttpMethod.GET, new HttpEntity<String>(headers), String.class).getBody().toString();
	}
	
	/**
	 * Get all status of one course by uuid
	 * 
	 * @param uuid
	 * @param headers
	 * @return all status of one course
	 */
	public List<Status> getStatusOfTopicById(String uuid, HttpHeaders headers) {
		JSONArray allTasks = getStatusJSONFromHttpResponse(restTemplate.exchange("http://course-service/api/course/tasks/" + uuid, 
				HttpMethod.GET, new HttpEntity<String>(headers), String.class).getBody().toString());
			    List<Status> statusOfCourse = new ArrayList<Status>();
	    for (int i = 0; i < allTasks.length(); i++) {
	    	int taskId = (Integer) allTasks.getJSONObject(i).get("id");
	    	statusOfCourse.addAll(statusJpa.findAllByTaskId(taskId));
	    }
	    return statusOfCourse;
	}
	
	
	//Helpers
	/**
	 * Decode given JWT token
	 * 
	 * @param headers
	 * @return string in JSON format from encoded JWT token
	 */
	private String getPayloadFromHeader(HttpHeaders headers) {
		Base64.Decoder decoder = Base64.getDecoder();
		String access_token = headers.getFirst("authorization");
		String token = access_token.substring(7, access_token.length());
		return new String(decoder.decode(token.split("\\.")[1]));
	}
	
	/**
	 * Create ResponseEntity on error
	 * 
	 * @param msg
	 * @return ResponseEntity with error message
	 */
	public ResponseEntity<Object> getForbidden(String msg) {
		return ResponseEntity
	            .status(HttpStatus.FORBIDDEN)
	            .body(msg);
	}
	
	/**
	 * Create ResponseEntity for object return
	 * 
	 * @param object
	 * @return ResponseEntity with object
	 */
	public ResponseEntity<Object> getReturnOk(Object object) {
		return ResponseEntity
	            .status(HttpStatus.OK)
	            .body(object);
	}
	
	/**
	 * Status response to JSON
	 * 
	 * @param response
	 * @return Status as JSON
	 */
	public JSONArray getStatusJSONFromHttpResponse(String response) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
	    return new JSONArray (response.trim());
	}
	
	
}
