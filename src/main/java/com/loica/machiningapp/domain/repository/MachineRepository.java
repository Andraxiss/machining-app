package com.loica.machiningapp.domain.repository;

import com.loica.machiningapp.domain.model.Machine;
import com.loica.machiningapp.domain.model.Program;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MachineRepository extends JpaRepository<Machine, Long> {}
