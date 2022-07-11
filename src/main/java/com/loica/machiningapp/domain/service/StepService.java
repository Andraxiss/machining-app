package com.loica.machiningapp.domain.service;

import com.loica.machiningapp.domain.model.Program;
import com.loica.machiningapp.domain.model.Step;
import com.loica.machiningapp.domain.repository.StepRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class StepService {
  private final StepRepository stepRepository;

  public StepService(StepRepository stepRepository) {
    this.stepRepository = stepRepository;
  }

  public void deleteStep(Step step) {
    this.stepRepository.delete(step);
  }

  public List<Step> findAllByProgram(Program program) {
    return this.stepRepository.findAllByProgram(program);
  }

  public Step save(Step step) {
    return this.stepRepository.save(step);
  }

  public List<Step> saveAll(List<Step> steps) {
    return this.stepRepository.saveAll(steps);
  }

  public void deleteSteps(List<Step> steps){
    this.stepRepository.deleteAll(steps);
  }
}
