package com.halbig.course.data;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@Entity
@Table(name = "task")
public class Task {

  @Id
  @NotNull
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private int id;

  @NotNull
  @Length(min = 8, max = 32)
  @Column(length = 32)
  private String title;

  @NotNull
  @Length(min = 10, max = 60)
  @Column(length = 60)
  private String longDescription;

  @Column
  private String deadline;
  
  @NotNull
  @ManyToOne
  @JsonIgnoreProperties("tasks")
  private Course course;

  public Task() {
	  
  }

  public Task(Course course, String title, String longDescription, String deadline) {
	this.course = course;
	course.addTask(this);
    this.title = title;
    this.longDescription = longDescription;
    this.deadline = deadline;
  }

  @Override
  public String toString() {
    return "Task \"" + title + "\"";
  }

  public int getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public String getLongDescription() {
    return longDescription;
  }

  public String getDeadline() {
    return deadline;
  }

  public void setLongDescription(String longDescription) {
    this.longDescription = longDescription;

  }

  public void setDeadline(String deadline) {
    this.deadline = deadline;
  }
  
  public Course getCourse() {
	    return course;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof Task)) {
      return false;
    }
    Task other = (Task) obj;
    return Objects.equals(getId(), other.getId());
  }
}

