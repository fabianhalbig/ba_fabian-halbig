package com.halbig.userservice.data;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import com.halbig.userservice.common.StatusEnum;

@Entity
public class Status {

  @Id
  @NotNull
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private int id;

  @NotNull
  @Column(length = 36)
  @Length(min = 36, max = 36)
  private String userId;

  @NotNull
  private int taskId;

  @Enumerated(EnumType.STRING)
  @NotNull
  private StatusEnum status = StatusEnum.NEU;

  public Status() {

  }

  public Status(int taskId) {
    this.taskId = taskId;
  }

  @Override
  public String toString() {
    return "Status " + status;
  }

  public int getId() {
    return id;
  }

  public String getUser() {
    return userId;
  }
  
  public void setUser(String userId) {
	    this.userId = userId;
  }
  
  public void setTaskId(int id) {
	    this.taskId = id;
  }
  public StatusEnum getStatus() {
    return status;
  }

  public void setStatus(final StatusEnum status) {
    this.status = status;
  }

  public int getTask() {
    return taskId;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof Status)) {
      return false;
    }
    Status other = (Status) obj;
    return Objects.equals(getId(), other.getId());
  }

}

