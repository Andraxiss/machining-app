package com.loica.machiningapp.domain.service;

import com.loica.machiningapp.domain.model.Machine;
import com.loica.machiningapp.domain.model.Program;
import com.loica.machiningapp.domain.repository.ProgramRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ProgramService {
  private final ProgramRepository programRepository;

  public ProgramService(ProgramRepository programRepository) {
    this.programRepository = programRepository;
  }

  public Program saveProgram(Program program) {
    return this.programRepository.save(program);
  }

  public Program findById(Long id) {
    return this.programRepository.findById(id).orElse(null);
  }

  public List<Program> findAllByMachine(Machine machine) {
    return this.programRepository.findAllByMachine(machine);
  }

  public void deleteProgram(Program program){
    this.programRepository.delete(program);
  }

  public void deletePrograms(List<Program> programs){
    this.programRepository.deleteAll(programs);
  }
}
