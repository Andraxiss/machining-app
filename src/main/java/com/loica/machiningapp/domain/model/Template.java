package com.loica.machiningapp.domain.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Entity
public class Template {
  @Id
  @GeneratedValue
  private Long id;

  @NotEmpty
  private String name;

  @NotNull
  @ManyToOne
  private Machine machine;

  @NotEmpty
  private String content;

  public Template() {
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Machine getMachine() {
    return machine;
  }

  public void setMachine(Machine machine) {
    this.machine = machine;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }


}
