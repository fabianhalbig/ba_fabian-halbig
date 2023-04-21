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
import com.halbig.course.data.Task;
import com.halbig.course.data.TaskJpa;
import com.halbig.course.service.CourseService;

@RestController
public class TaskController {
		
	@Autowired
	public TaskJpa taskJpa;
	
	@Autowired
	public CourseJpa courseJpa;
	
	@Autowired
	public CourseService courseService;

	/**
	 * Admins can get all tasks
	 * 
	 * @param headers
	 * @return all saved tasks as JSON
	 */
	@GetMapping("/api/course/task/all")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<Object> getAllTasks(@RequestHeader HttpHeaders headers) {
		try {
			if (!courseService.isAdmin(headers)) {
				throw new AccessDeniedException("Only admins are able to view all tasks");
			}
			return courseService.getReturnOk(taskJpa.findAll());
		} catch (Exception e) {
			return courseService.getForbidden(e.getMessage());
		}
	}
	
	/**
	 * Admins, subscriber and the course creator are able to get a task of a course
	 * 
	 * @param headers
	 * @param id
	 * @return task as JSON
	 */
	@GetMapping("api/course/task/{id}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<Object> getTaskById(@RequestHeader HttpHeaders headers, @PathVariable("id") int id) {
		Task t = taskJpa.findTaskById(id);
		try {
			if (t == null) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No task found with given ID");
			}
			Course c = t.getCourse();
			if (!(courseService.isSubscriber(headers, c) || courseService.isCreator(headers, c) ||
					courseService.isAdmin(headers))) {
				throw new AccessDeniedException("Only subscriber and creator of the corresponding course as well as admins are able to view this task");
			}
			return courseService.getReturnOk(t);
		} catch (Exception e) {
			return courseService.getForbidden(e.getMessage());
		}
	}
	
	/**
	 * Admins, subscriber and course creator are able to get all tasks of a course
	 * 
	 * @param headers
	 * @param uuid
	 * @return list of tasks as JSON
	 */
	@GetMapping("api/course/tasks/{uuid}")
	public ResponseEntity<Object> getAllTasksFromCourse(@RequestHeader HttpHeaders headers, @PathVariable("uuid") String uuid) {
		Course c = courseJpa.findByUuid(uuid);
		try {
			if (c == null) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No course found with given ID");
			}
			if (!(courseService.isSubscriber(headers, c) || courseService.isCreator(headers, c) 
					|| courseService.isAdmin(headers))) {
				throw new AccessDeniedException("Only subscriber and creator of the corresponding course as well as admins are able to view all task");
			}
			return courseService.getReturnOk(c.getTasks());
		} catch (Exception e) {
			return courseService.getForbidden(e.getMessage());
		}
			
	}
	
	/**
	 * Admin or creator of a course are able to remove a task (course have to be listed in JSON without task value)
	 * @param headers
	 * @param id
	 * @return success message
	 */
	@DeleteMapping("api/course/task/{id}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<Object> deleteTask(@RequestHeader HttpHeaders headers, @PathVariable("id") int id) {
		Task t = taskJpa.findTaskById(id);
		try {
			if (t == null) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No task found with given ID");
			}
			Course c = t.getCourse();
			if (!(courseService.isCreator(headers, c) || courseService.isAdmin(headers))) {
				throw new AccessDeniedException("Only admins and the creator are able remove a course");
			}
			return courseService.getReturnOk(courseService.deleteTaskAndStatus(t, headers)); 
		} catch (Exception e) {
			return courseService.getForbidden(e.getMessage());
		}
	}
	
	/**
	 * Admins and the course creator are able to add tasks to a course
	 * 
	 * @param headers
	 * @param task
	 * @return success message
	 */
	@PostMapping("api/course/task")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Object> createTask(@RequestHeader HttpHeaders headers, @RequestBody Task task) {
		Course c = task.getCourse();
		try {
			if (!(courseService.isCreator(headers, c) || courseService.isAdmin(headers))) {
				throw new AccessDeniedException("Only admins and the creator of the course are able to create a task");
			}
			return courseService.getReturnOk(courseService.createTaskAndStatus(task, headers));
		} catch (Exception e) {
			return courseService.getForbidden(e.getMessage());
		}
	}
	
	/**
	 * Get subscriber of a topic by task id
	 * 
	 * @param headers
	 * @param id
	 * @return list of subscriber id
	 */
	@GetMapping("api/course/task/{id}/subscriber")
	public ResponseEntity<Object> getCreatorAndSubscriberOfCourseFromTaskId(@RequestHeader HttpHeaders headers,
			@PathVariable("id") int id) {
		Task t = taskJpa.findTaskById(id);
		Course c = t.getCourse();
		try {	
			if (c == null) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No course found with given ID");
			}
			if (!(courseService.isAdmin(headers) || courseService.isSubscriber(headers, c))) {
				throw new AccessDeniedException("User is not admin or subscriber of the corresponding course");
			}
			return courseService.getReturnOk(t.getCourse().getSubscriber());
		} catch (Exception e) {
			return courseService.getForbidden(e.getMessage());
		}
	}
	
	/**
	 * Get creator id from task id
	 * 
	 * @param headers
	 * @param id
	 * @return creator user id
	 */
	@GetMapping("api/course/task/{id}/creator")
	public ResponseEntity<Object> getCreatorOfCourseFromTaskId(@RequestHeader HttpHeaders headers,
			@PathVariable("id") int id) {
		Task t = taskJpa.findTaskById(id);
		Course c = t.getCourse();
		try {
			if (c == null) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No course found with given ID");
			}
			if (!(courseService.isAdmin(headers) || courseService.isCreator(headers, c))) {
				throw new AccessDeniedException("User is not admin or creator of the corresponding course");
			}
			return courseService.getReturnOk(c.getUserId());
		} catch (Exception e) {
			return courseService.getForbidden(e.getMessage());
		}
	}
}
