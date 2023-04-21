package com.halbig.course.service;

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

import com.halbig.course.data.Course;
import com.halbig.course.data.CourseJpa;
import com.halbig.course.data.Task;
import com.halbig.course.data.TaskJpa;

@Component
public class CourseService {
	
	public static final String admin = "ROLE_ADMIN";
	
	@Autowired
	public CourseJpa courseJpa;
	
	@Autowired
	public TaskJpa taskJpa;
	
	@Autowired
	public CourseService courseService;	
	
	@Autowired 
	public RestTemplate restTemplate;
	
	//Course Controller
	/**
	 * Generate List of subscribed courses
	 * 
	 * @param headers
	 * @return subscribed courses
	 */
	public List<Course> getAllSubscribedCourses(HttpHeaders headers) {
		List<Course> subscribedCourses = new ArrayList<Course>();
		for (Course c : courseJpa.findAll()) {
			if (c.getSubscriber().contains(courseService.decodeAccessTokenToUserId(headers))) {
				subscribedCourses.add(c);
			}
		}
		return subscribedCourses;
	}
	
	/**
	 * Create course for current user
	 * 
	 * @param course
	 * @param headers
	 * @return success message
	 */
	public String createCourse(Course course, HttpHeaders headers) {
		course.setUserId(courseService.decodeAccessTokenToUserId(headers));
		courseJpa.save(course);
		return "Course created with ID: " + course.getUuid();
	}
	
	/**
	 * Remove subscription of user and corresponding status in course service
	 * 
	 * @param c
	 * @param userId
	 * @param headers
	 * @return success message
	 */
	public String deleteAboAndStatusOfUser(Course c, String userId, HttpHeaders headers) {
		for (Task t: c.getTasks()) {
			restTemplate.exchange("http://user-service/api/user/status/task/" + t.getId() + "/user/" + userId,
					HttpMethod.DELETE, new HttpEntity<String>(headers), String.class).getBody();
		}
		c.removeSubscriber(userId);
		courseJpa.save(c);
		return "User " + userId + "unsubscribed from course " + c.getUuid();
	}
	
	/**
	 * create status for users after course subscription
	 * 
	 * @param c
	 * @param userId
	 * @param headers
	 * @return success message
	 */
	public String createStatusOnSubscription(Course c, String userId, HttpHeaders headers) {
		c.register(userId);		
		courseJpa.save(c);
		for (Task task: c.getTasks()) {
			restTemplate.exchange("http://user-service/api/user/status/" + task.getId(),
					HttpMethod.POST, new HttpEntity<String>(headers), String.class).getBody();
		}
		return "User " + userId + " added to subscription list of course " + c.getUuid();
	}
	
	//Task controller
	/**
	 * delete task and referenced status
	 * 
	 * @param t
	 * @param headers
	 * @return message
	 */
	public String deleteTaskAndStatus(Task t, HttpHeaders headers) {
		restTemplate.exchange("http://user-service/api/user/status/task/" + t.getId(),
				HttpMethod.DELETE, new HttpEntity<String>(headers), String.class);
		taskJpa.delete(t);
		return "Task " + t.getId() + "removed from course " + t.getCourse().getUuid();
	}
	
	/**
	 * Create task and status for task for each subscriber
	 * 
	 * @param task
	 * @param headers
	 * @return success message
	 */
	public String createTaskAndStatus(Task task, HttpHeaders headers) {
		taskJpa.save(task);
		for (String s: task.getCourse().getSubscriber()) {
			restTemplate.exchange("http://user-service/api/user/status/" + task.getId() + "/user/" + s,
					HttpMethod.POST, new HttpEntity<String>(headers), String.class);
		}
		return "Task created with ID: " + task.getId() +
				" for course " + task.getCourse().getUuid() +
				"// Added status for subs: " + task.getCourse().getSubscriber();
	}
	
	/**
	 * Generate user id from token (copy from userService to avoid restTemplate requests)
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
	 * Generate client roles from token (copy from userService to avoid restTemplate requests)
	 * 
	 * @param headers
	 * @return roles as string array list
	 */
	public ArrayList<String> decodeAccessTokenToClientRoles(HttpHeaders headers) {
		ArrayList<String> roles = new ArrayList<String>();
		JSONArray user_roles = null;
		try {
			JSONObject jsonObject = new JSONObject(this.getPayloadFromHeader(headers));
			user_roles = jsonObject.getJSONObject("resource_access")
					.getJSONObject("demo-spring-boot")
					.getJSONArray("roles"); 
		}catch (JSONException e){
			System.out.println("No user ID found in response");
		}
		if (user_roles != null) {
			for (int i=0;i<user_roles.length();i++){
				roles.add(user_roles.getString(i));
			}
		}
		return roles;
	}
	
	/**
	 * Decode given JWT token (copy from userService to avoid restTemplate requests)
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
	
	//Helpers
	/**
	 * Check if user is admin according to JWT-Token
	 * @param headers
	 * @return boolean true if user is admin
	 */
	public boolean isAdmin(HttpHeaders headers) {
		return this.decodeAccessTokenToClientRoles(headers).contains(admin);
	}
	
	/**
	 * Check if user is subscriber of a course
	 * 
	 * @param headers
	 * @return boolean true if user is subscriber
	 */
	public boolean isSubscriber(HttpHeaders headers, Course course) {
		return course.getSubscriber().contains(this.decodeAccessTokenToUserId(headers));
	}
	
	/**
	 * Check if user is creator of a course
	 * 
	 * @param headers
	 * @return boolean true if user is the course creator
	 */
	public boolean isCreator(HttpHeaders headers, Course course) {
		return course.getUserId().equals(this.decodeAccessTokenToUserId(headers));
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

}
