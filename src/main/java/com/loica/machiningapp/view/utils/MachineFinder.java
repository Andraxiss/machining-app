package com.loica.machiningapp.view.utils;

import com.loica.machiningapp.domain.model.Machine;
import com.vaadin.flow.component.select.Select;
import java.util.List;

public class MachineFinder extends Select<Machine> {

  public MachineFinder(List<Machine> machineList) {
    this.setLabel("Machine");
    this.setItems(machineList);
    this.setItemLabelGenerator(Machine::getName);
    this.setPlaceholder("Sélectionner la machine");
    this.setRequiredIndicatorVisible(true);
  }

  public MachineFinder() {
  }
}
