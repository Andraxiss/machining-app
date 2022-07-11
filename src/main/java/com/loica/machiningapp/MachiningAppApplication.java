package com.loica.machiningapp;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.PWA;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@PWA(
    name = "Usinapp",
    shortName = "Usinapp"
)
@SpringBootApplication
public class MachiningAppApplication extends SpringBootServletInitializer implements
    AppShellConfigurator {

  public static void main(String[] args) {
    SpringApplication.run(MachiningAppApplication.class, args);
  }

}
