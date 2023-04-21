package com.halbig.course.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@Entity
@Table(name = "course")
public class Course {

  @Id
  @NotNull
  @Column(length = 36)
  @Length(min = 36, max = 36)
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(
      name = "UUID",
      strategy = "org.hibernate.id.UUIDGenerator")
  private String uuid;
  
  @NotNull
  @Column(length = 36)
  @Length(min = 36, max = 36)
  private String userId;


  @NotNull
  @Column(length = 60)
  @Length(min = 10, max = 60)
  private String title;

  @NotNull
  @Column
  @Length(min = 10, max = 120)
  private String description;
  
  @OneToMany(mappedBy = "course")
  @JsonIgnoreProperties("course")
  private Collection<Task> tasks = new ArrayList<Task>();
  
  
  @Column(name="subscriber")
  private ArrayList<String> subscriber = new ArrayList<String>();

  public Course() {

  }

  public Course(final String uuid, final String title, final String description) {
    this.uuid = uuid;
    this.title = title;
    this.description = description;
  }

  @Override
  public String toString() {
    return "Course " + title;
  }

  public String getUuid() {
    return uuid;
  }
  
  public String getUserId() {
	    return userId;
	  }


  public String getTitle() {
    return title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }
  
  public void setUserId(String userId) {
	  this.userId = userId;
  }
  
  public void addTask(Task t) {
	  tasks.add(t);
  }

  public void removeTask(Task t) {
	  tasks.remove(t);
  }

  public Collection<Task> getTasks() {
	  return Collections.unmodifiableCollection(tasks);
  }
  
  public void register(String subUserId) {
	  subscriber.add(subUserId);
  }

  public void removeSubscriber(String subUserId) {
      subscriber.remove(subUserId);
  }
  
  public ArrayList<String> getSubscriber() {
	  return subscriber;
  }

  @Override
  public int hashCode() {
    return Objects.hash(uuid);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof Course)) {
      return false;
    }
    Course other = (Course) obj;
    return Objects.equals(getUuid(), other.getUuid());
  }

}
