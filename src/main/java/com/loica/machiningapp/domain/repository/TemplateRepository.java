package com.loica.machiningapp.domain.repository;

import com.loica.machiningapp.domain.model.Machine;
import com.loica.machiningapp.domain.model.Template;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TemplateRepository extends JpaRepository<Template, Long> {

  List<Template> findAllByMachine(Machine machine);
}
