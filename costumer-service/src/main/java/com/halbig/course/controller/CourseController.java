package com.halbig.course.controller;

import java.nio.file.AccessDeniedException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.halbig.course.data.Course;
import com.halbig.course.data.CourseJpa;
import com.halbig.course.service.CourseService;

@RestController
public class CourseController {
	
	@Autowired
	public CourseJpa courseJpa;
	
	@Autowired
	public CourseService courseService;
	
	/**
	 * Admins can get all courses
	 * 
	 * @param headers
	 * @return all saved courses as JSON
	 */
	@GetMapping("api/course/all")
	public ResponseEntity<Object> getAllCourses(@RequestHeader HttpHeaders headers) {
		try {
			if (!courseService.isAdmin(headers)) {
				throw new AccessDeniedException("Only admins are able to view all courses");
			}
			return courseService.getReturnOk(courseJpa.findAll()); 
		} catch (Exception e ) {
			return courseService.getForbidden(e.getMessage());
	    }
	}
	
	/**
	 * Get subscribed courses of current user
	 * 
	 * @param headers
	 * @return subscribed courses
	 */
	@GetMapping("api/course/subscribed")
	public ResponseEntity<Object> getSubscribedCourses(@RequestHeader HttpHeaders headers) {
		try {
			return courseService.getReturnOk(courseService.getAllSubscribedCourses(headers));
		} catch (Exception e) {
			return courseService.getForbidden(e.getMessage());

		}
	}
	
	/**
	 * Get created courses of current user
	 * 
	 * @param headers
	 * @return created courses
	 */
	@GetMapping("api/course/created")
	public ResponseEntity<Object> getCreatedCourses(@RequestHeader HttpHeaders headers) {
		try {
			return courseService.getReturnOk(courseJpa.findAllByUserId(courseService.decodeAccessTokenToUserId(headers)));
		} catch (Exception e) {
			return courseService.getForbidden(e.getMessage());
		}
	}
	
	/**
	 * Admins, creator and subscriber are able to find course by uuid
	 * 
	 * @param headers
	 * @param uuid
	 * @return course JSON
	 */
	@GetMapping("api/course/{uuid}")
	public ResponseEntity<Object> getCourseById(@RequestHeader HttpHeaders headers, @PathVariable("uuid") String uuid) {
		Course c = courseJpa.findByUuid(uuid);
		try {
			if (c == null) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No course found with given ID");
			}
			if (!(courseService.isSubscriber(headers, c) || courseService.isCreator(headers, c) ||
					courseService.isAdmin(headers))) {
				throw new AccessDeniedException("Only admins, subscriber and the creator are able to view this course");
			}
			return courseService.getReturnOk(c);
		} catch (Exception e) {
			return courseService.getForbidden(e.getMessage());
		}
	}
	
	/**
	 * Admins and course creator are able to delete courses by uuid
	 * 
	 * @param headers
	 * @param uuid
	 * @return success message
	 */
	@DeleteMapping("api/course/{uuid}")
	public ResponseEntity<Object> deleteCourse(@RequestHeader HttpHeaders headers,
			@PathVariable("uuid") String uuid) {
		Course c = courseJpa.findByUuid(uuid);
		try {
			if (c == null) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No course found with given ID");
			}
			if (!(courseService.isCreator(headers, c) || courseService.isAdmin(headers))) {
				throw new AccessDeniedException("Only admins and the creator are able remove a course");
			}
			courseJpa.delete(c);
			return courseService.getReturnOk("Course " + uuid + "sucessfully deleted");
		} catch (Exception e) {
			return courseService.getForbidden(e.getMessage());
		}
	}
	
	/**
	 * Logged in users are able to create courses
	 * 
	 * @param course
	 * @param headers
	 * @return success message
	 */
	@PostMapping("api/course")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Object> createCourse(@RequestBody Course course, @RequestHeader HttpHeaders headers) {
		try {
			return courseService.getReturnOk(courseService.createCourse(course, headers));
		} catch (Exception e){
			return courseService.getForbidden(e.getMessage());
		}
	}
	
	/**
	 * Users that are not yet subscribed to the course and are not the 
	 * course creator are able to subscribe to a course via the uuid
	 * 
	 * @param uuid
	 * @param headers
	 * @return success message
	 */
	@PostMapping("api/course/subscribe/{uuid}")
	public ResponseEntity<Object> subscirbeToCourse(@PathVariable("uuid") String uuid,
			@RequestHeader HttpHeaders headers) {
		Course c = courseJpa.getOne(uuid);
		String userId = courseService.decodeAccessTokenToUserId(headers);
		try {
			if (c == null) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No course found with given ID");
			}
			if (courseService.isSubscriber(headers, c)) {
				throw new AccessDeniedException("You are already subscriber of this course");
			} else if (courseService.isCreator(headers, c)) {
				throw new AccessDeniedException("It is not possible to subscribe to own courses");
			}
			return courseService.getReturnOk(courseService.createStatusOnSubscription(c, userId, headers));
		} catch (Exception e) {
			return courseService.getForbidden(e.getMessage());
		}
	}
	
	
	/**
	 * Deabo course and delete corresponding status of user
	 * 
	 * @param headers
	 * @param uuid
	 * @return success message
	 */
	@DeleteMapping("api/course/reject/{uuid}")
	public ResponseEntity<Object> deaboCurrentUserFromCourse(@RequestHeader HttpHeaders headers,
			@PathVariable("uuid") String uuid) {
		Course c = courseJpa.getOne(uuid);
		String userId = courseService.decodeAccessTokenToUserId(headers);
		try {
			if (c == null) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No course found with given ID");
			}
			if (!courseService.isSubscriber(headers, c)) {
				throw new AccessDeniedException("You are not a subscriber of this course");
			}
			return courseService.getReturnOk(courseService.deleteAboAndStatusOfUser(c, userId, headers));
		} catch (Exception e) {
			return courseService.getForbidden(e.getMessage());
		}
		
	}
	
	/**
	 * Get creator of a course from the uuid
	 * 
	 * @param headers
	 * @param uuid
	 * @return user id of creator
	 */
	@GetMapping("api/course/{uuid}/creator")
	public ResponseEntity<Object> getCreatorOfCourseFromUuid(@RequestHeader HttpHeaders headers,
			@PathVariable("uuid") String uuid) {
		Course c = courseJpa.findByUuid(uuid);
		try {
			if (c == null) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No course found with given ID");
			}
			if (!(courseService.isAdmin(headers) || courseService.isCreator(headers, c))) {
				throw new AccessDeniedException("User is not admin or creator of the course");
			}
			return courseService.getReturnOk(c.getUserId());
		} catch (Exception e) {
			return courseService.getForbidden(e.getMessage());
		}
	}

}
