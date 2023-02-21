package com.halbig.userservice.data;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatusJpa extends JpaRepository<Status, String>{

	List<Status> findAllByUserId(String userId);
	
	List<Status> findAllByTaskId(int taskId);
	
	Status findById(int id);
	
	Status findByTaskIdAndUserId(int taskId, String userId);
	
}
