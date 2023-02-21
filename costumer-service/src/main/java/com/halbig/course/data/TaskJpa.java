package com.halbig.course.data;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskJpa extends JpaRepository<Task, Long> {
  
  Task findTaskById(int id);
  
  List<Task> findAllByCourseInOrderByTitleAsc(Collection<Course> course);
  
}


