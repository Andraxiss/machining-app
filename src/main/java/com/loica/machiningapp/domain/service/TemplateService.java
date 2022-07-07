package com.loica.machiningapp.domain.service;

import com.loica.machiningapp.domain.model.Machine;
import com.loica.machiningapp.domain.model.Template;
import com.loica.machiningapp.domain.repository.TemplateRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TemplateService {
  private final TemplateRepository templateRepository;

  public TemplateService(TemplateRepository templateRepository) {
    this.templateRepository = templateRepository;
  }

  public Template saveTemplate(Template template) {
    return this.templateRepository.save(template);
  }

  public List<Template> findByMachine(Machine machine){
    if (machine != null){
      return this.templateRepository.findAllByMachine(machine);
    } else {
      throw new RuntimeException("Machine must not be null");
    }
  }
}
