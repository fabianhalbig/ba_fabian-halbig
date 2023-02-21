package com.halbig.course.data;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CourseJpa extends JpaRepository<Course, String> {
	
  Course findByUuid(String uuid);
  
  List<Course> findAllByUserId(String userId);

}

