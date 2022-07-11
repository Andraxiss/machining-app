package com.loica.machiningapp.domain.model;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Entity
public class Program {
  @Id
  @GeneratedValue
  private Long id;

  @NotNull
  @ManyToOne
  private Machine machine;

  @NotEmpty
  private String name;

  @NotEmpty
  private String programNumber;

  @OneToMany(mappedBy = "program", fetch = FetchType.EAGER)
  private List<Step> steps = new ArrayList<>();

  public Program() {
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

  public List<Step> getSteps() {
    return steps;
  }

  public void setSteps(List<Step> steps) {
    this.steps = steps;
  }

  public String getProgramNumber() {
    return programNumber;
  }

  public void setProgramNumber(String programNumber) {
    this.programNumber = programNumber;
  }
}
