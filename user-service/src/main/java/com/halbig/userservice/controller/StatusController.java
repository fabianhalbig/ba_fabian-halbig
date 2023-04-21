package com.halbig.userservice.controller;

import java.nio.file.AccessDeniedException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.halbig.userservice.common.StatusEnum;
import com.halbig.userservice.data.Status;
import com.halbig.userservice.data.StatusJpa;
import com.halbig.userservice.service.UserService;

@RestController
public class StatusController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	public StatusJpa statusJpa;
	
	/**
	 * Admins can get all status
	 * 
	 * @param headers
	 * @return all status in JSON
	 */
	@GetMapping("api/user/status/all")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<Object> getAllStatus(@RequestHeader HttpHeaders headers) {
		try {
			if (!userService.isAdmin(headers)) {
				throw new AccessDeniedException("Only admins are able to view all courses");
			}
			return userService.getReturnOk(statusJpa.findAll()); 
		} catch (Exception e ) {
			return userService.getForbidden(e.getMessage());
	    } 
	}
	
	/**
	 * Course Creator and admins are able to get status of subscriber per course
	 * 
	 * @param headers
	 * @param uuid of course
	 * @return status of one course as JSON
	 */
	@GetMapping("api/user/status/{uuid}/creator")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<Object> getStatusOfTopic(@RequestHeader HttpHeaders headers, 
			@PathVariable("uuid") String uuid) throws AccessDeniedException {
		try {
			if (!(userService.getCreatorIdFromCourse(uuid, headers).equals(userService.decodeAccessTokenToUserId(headers)) 
					|| userService.isAdmin(headers))) {
				throw new AccessDeniedException("User is not admin or creator of the course");
			}			
			return userService.getReturnOk(userService.getStatusOfTopicById(uuid, headers));
		} catch (Exception e) {
			return userService.getForbidden(e.getMessage());
		}
	}
	
	/**
	 * Admins and course creator of corresponding task are able to remove all status allocated to the task
	 * 
	 * @param headers
	 * @param taskId
	 * @return Success message
	 */
	@DeleteMapping("api/user/status/task/{taskId}")
	public ResponseEntity<Object> deletetAllStatusOfOneTask(@RequestHeader HttpHeaders headers,
			@PathVariable("taskId") int taskId) {
		try {
			if (!(userService.getCreatorIdFromTask(taskId, headers).equals(userService.decodeAccessTokenToUserId(headers))
					|| userService.isAdmin(headers))) {
				throw new AccessDeniedException("User is not admin or creator of the corresponding course");
			}
			return userService.getReturnOk(userService.deleteAllStatusFromTask(taskId));
		} catch (Exception e) {
			return userService.getForbidden(e.getMessage());

		}
	}
	
	/**
	 * Delete status per taskId and UserID
	 * 
	 * @param headers
	 * @param taskId
	 * @param userId
	 * @return success message
	 */
	@DeleteMapping("api/user/status/task/{taskId}/user/{userId}")
	public ResponseEntity<Object> delteStatusOfOneTaskFromOneUser(@RequestHeader HttpHeaders headers, @PathVariable("taskId") int taskId,
			@PathVariable("userId") String userId) {
		try {
			if (!(userService.getSubscirberFromTaskId(taskId, headers).contains(userService.decodeAccessTokenToUserId(headers)) 
					|| userService.isAdmin(headers))) {
				throw new AccessDeniedException("User is not admin or subscriber of the corresponding course");
			}
			statusJpa.delete(statusJpa.findByTaskIdAndUserId(taskId, userId));
			return userService.getReturnOk("Status deleted for task " + taskId);
		} catch (Exception e) {
			return userService.getForbidden(e.getMessage());

		}
	}
	
	/**
	 * Get all status of current logged in user
	 * 
	 * @param headers
	 * @return all status of the user
	 */
	@GetMapping("api/user/status/currentuser")
	@ResponseStatus(HttpStatus.OK)
	public List<Status> getStatusOfCurrentUser(@RequestHeader HttpHeaders headers) {
		return statusJpa.findAllByUserId(userService.decodeAccessTokenToUserId(headers));
	}
	
	/**
	 * Create status for subscriber
	 * 
	 * @param taskId
	 * @param headers
	 * @return success message
	 */
	@PostMapping("api/user/status/{taskId}")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Object> createStatus(@PathVariable("taskId") int taskId, @RequestHeader HttpHeaders headers) {
		try {
			return userService.getReturnOk(userService.createStatusForTasks(taskId, headers));
		} catch (Exception e) {
			return userService.getForbidden(e.getMessage());

		}
	}
	
	/**
	 * Admin and course creator can create required status on task creation
	 * @param taskId
	 * @param userId
	 * @param headers
	 * @return
	 */
	@PostMapping("api/user/status/{taskId}/user/{userId}")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Object> createStatusWithUserId(@PathVariable("taskId") int taskId, @PathVariable("userId") String userId, @RequestHeader HttpHeaders headers) {
		try {
			if (!(userService.getCreatorIdFromTask(taskId, headers).equals(userService.decodeAccessTokenToUserId(headers)) || userService.isAdmin(headers))) {
				throw new AccessDeniedException("User is not admin or creator of the corresponding course");
			}
			return userService.getReturnOk(userService.creationOfStatus(taskId, userId));
		} catch (Exception e) {
			return userService.getForbidden(e.getMessage());

		}
	}
	
	/**
	 * Set status of current logged in user to in progress
	 * 
	 * @param statusId
	 * @return success message 
	 */
	@PostMapping("api/user/status/{statusId}/progress")
	public String startProgressOfStatus(@PathVariable("statusId") int statusId) {
		return userService.setStatusInStatus(statusId, StatusEnum.IN_ARBEIT);
	}
	
	/**
	 * Set status of current logged in user to done
	 * 
	 * @param statusId
	 * @return success message 
	 */
	@PostMapping("api/user/status/{statusId}/finish")
	public String finishStatus(@PathVariable("statusId") int statusId) {
		return userService.setStatusInStatus(statusId, StatusEnum.FERTIG);
	}
	
	
	/**
	 * Set status of current logged in user to reopened
	 * 
	 * @param statusId
	 * @return success message 
	 */
	@PostMapping("api/user/status/{statusId}/reopen")
	public String reopenStatus(@PathVariable("statusId") int statusId) {
		return userService.setStatusInStatus(statusId, StatusEnum.OFFEN);
	}

}
