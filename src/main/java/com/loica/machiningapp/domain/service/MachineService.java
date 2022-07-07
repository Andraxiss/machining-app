package com.loica.machiningapp.domain.service;

import com.loica.machiningapp.domain.model.Machine;
import com.loica.machiningapp.domain.repository.MachineRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class MachineService {
  private final MachineRepository machineRepository;

  public MachineService(MachineRepository machineRepository) {
    this.machineRepository = machineRepository;
  }

  public List<Machine> getMachines(){
    return this.machineRepository.findAll();
  }

  public Machine createMachineWithName(String name){
    Machine machine = new Machine();
    machine.setName(name);
    return this.machineRepository.save(machine);
  }
}
