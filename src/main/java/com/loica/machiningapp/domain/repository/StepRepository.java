package com.loica.machiningapp.domain.repository;

import com.loica.machiningapp.domain.model.Program;
import com.loica.machiningapp.domain.model.Step;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StepRepository extends JpaRepository<Step, Long> {

  List<Step> findAllByProgram(Program program);
}
