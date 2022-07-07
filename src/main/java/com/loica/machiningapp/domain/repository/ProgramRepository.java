package com.loica.machiningapp.domain.repository;

import com.loica.machiningapp.domain.model.Machine;
import com.loica.machiningapp.domain.model.Program;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

@Repository
public interface ProgramRepository extends JpaRepository<Program, Long> {

  List<Program> findAllByMachine(Machine machine);
}
