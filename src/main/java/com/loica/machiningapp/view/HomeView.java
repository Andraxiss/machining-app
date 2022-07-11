package com.loica.machiningapp.view;

import com.loica.machiningapp.domain.model.Machine;
import com.loica.machiningapp.domain.model.Program;
import com.loica.machiningapp.domain.model.Template;
import com.loica.machiningapp.domain.service.MachineService;
import com.loica.machiningapp.domain.service.ProgramService;
import com.loica.machiningapp.domain.service.StepService;
import com.loica.machiningapp.domain.service.TemplateService;
import com.loica.machiningapp.view.utils.MachineFinder;
import com.loica.machiningapp.view.utils.NotificationGreen;
import com.loica.machiningapp.view.utils.NotificationRed;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import java.util.List;
import java.util.Map;
import javax.annotation.security.PermitAll;

@PermitAll
@Route(value = "", layout = MainLayout.class)
@PageTitle("Acceuil")
@CssImport("./styles/home.css")
public class HomeView extends VerticalLayout implements BeforeEnterObserver {
  private final MachineService machineService;
  private final ProgramService programService;
  private final TemplateService templateService;
  private final StepService stepService;

  private Map<String, List<String>> parametersMap;

  public HomeView(
      MachineService machineService,
      ProgramService programService,
      TemplateService templateService,
      StepService stepService) {
    this.machineService = machineService;
    this.templateService = templateService;
    this.programService = programService;
    this.stepService = stepService;
    prepareView();

    setAlignItems(Alignment.CENTER);
    setJustifyContentMode(JustifyContentMode.CENTER);
    setSizeFull();
  }

  private void prepareView() {
    Button btnModel = new Button("Créer un modèle");
    Button btnProgram = new Button("Créer un programme");
    Button btnEditModel = new Button("Editer un modèle");
    Button btnEditProgram = new Button("Editer un programme");
    Button btnCreateMachine = new Button("Créer une machine");
    Button btnDeleteMachine = new Button("Supprimer une machine");

    btnModel.setClassName("btn");
    btnProgram.setClassName("btn");
    btnEditModel.setClassName("btn");
    btnEditProgram.setClassName("btn");
    btnCreateMachine.setClassName("btn");
    btnDeleteMachine.setClassName("btn");

    btnModel.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    btnProgram.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    btnCreateMachine.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    btnDeleteMachine.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

    btnModel.addClickListener(e -> btnModel.getUI().ifPresent(ui -> ui.navigate("template")));
    btnEditModel.addClickListener(
        e -> btnEditModel.getUI().ifPresent(ui -> ui.navigate("template/edit")));
    btnProgram.addClickListener(e -> btnModel.getUI().ifPresent(ui -> ui.navigate("program")));
    btnEditProgram.addClickListener(
        e -> btnModel.getUI().ifPresent(ui -> ui.navigate("program/edit")));
    btnCreateMachine.addClickListener(e -> createMachine());
    btnDeleteMachine.addClickListener(e -> deleteMachine());

    HorizontalLayout horizontalLayout1 = new HorizontalLayout();
    horizontalLayout1.add(btnCreateMachine, btnModel, btnProgram);

    HorizontalLayout horizontalLayout2 = new HorizontalLayout();
    horizontalLayout2.add(btnDeleteMachine, btnEditModel, btnEditProgram);

    add(horizontalLayout1, horizontalLayout2);
  }

  private void handleParameters() {
    NotificationGreen notification;
    if (this.parametersMap != null) {
      if (parametersMap.containsKey("template-created")
          || parametersMap.containsKey("template-edited")) {

        if (parametersMap.containsKey("template-created")) {
          notification =
              new NotificationGreen(
                  "Le modèle suivant a été créé : " + parametersMap.get("template-created"));
        } else {
          notification =
              new NotificationGreen(
                  "Le modèle suivant a été édité : " + parametersMap.get("template-edited"));
        }
        notification.open();
      }
    }
  }

  private void createMachine() {
    Dialog dialog = new Dialog();
    dialog.setHeaderTitle("Créer une machine");
    dialog.setWidth(500, Unit.PIXELS);

    TextField machineName = new TextField();
    machineName.setLabel("Nom de la machine");
    machineName.setRequiredIndicatorVisible(true);
    machineName.setWidthFull();
    machineName.setErrorMessage("Le champ ne doit pas être vide");
    machineName.setValueChangeMode(ValueChangeMode.LAZY);
    machineName.setValueChangeTimeout(300);

    Button btnValidate = new Button("Valider");
    btnValidate.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    btnValidate.setEnabled(false);
    btnValidate.addClickListener(
        e -> {
          if (!machineName.getValue().isBlank()) {
            validateCreateMachine(machineName.getValue());
            dialog.close();
          } else {
            machineName.setInvalid(true);
          }
        });

    Button btnBack = new Button("Retour");
    btnBack.addClickListener(
        e -> {
          dialog.close();
        });

    machineName.addValueChangeListener(e -> btnValidate.setEnabled(true));

    HorizontalLayout btnLayout = new HorizontalLayout();
    btnLayout.setJustifyContentMode(JustifyContentMode.END);
    btnLayout.setWidthFull();
    btnLayout.add(btnBack, btnValidate);

    VerticalLayout verticalLayout = new VerticalLayout();
    verticalLayout.add(machineName, btnLayout);

    dialog.add(verticalLayout);
    dialog.open();
  }

  private void validateCreateMachine(String machineName) {
    this.machineService.createMachineWithName(machineName);
    NotificationGreen notificationGreen =
        new NotificationGreen("La machine " + machineName + " a été créée");
    notificationGreen.open();
  }

  private void deleteMachine() {
    Dialog dialog = new Dialog();
    dialog.setHeaderTitle("Supprimer une machine");

    Button btnValidate = new Button("Valider");
    btnValidate.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    btnValidate.setEnabled(false);

    Button btnBack = new Button("Retour");
    btnBack.addClickListener(
        e -> {
          dialog.close();
        });

    HorizontalLayout btnLayout = new HorizontalLayout();
    btnLayout.setJustifyContentMode(JustifyContentMode.END);
    btnLayout.setWidthFull();
    btnLayout.add(btnBack, btnValidate);

    MachineFinder machineFinder = new MachineFinder(this.machineService.getMachines());
    machineFinder.setWidthFull();

    machineFinder.addValueChangeListener(
        machine -> {
          btnValidate.setEnabled(true);
        });

    btnValidate.addClickListener(
        e -> {
          if (!machineFinder.isEmpty()) {
            validateDeleteMachine(machineFinder.getValue());
            dialog.close();
          }
        });

    VerticalLayout verticalLayout = new VerticalLayout();
    verticalLayout.add(machineFinder, btnLayout);
    verticalLayout.setWidth(500, Unit.PIXELS);

    dialog.add(verticalLayout);
    dialog.open();
  }

  private void validateDeleteMachine(Machine machine) {
    List<Template> templates = this.templateService.findByMachine(machine);
    List<Program> programs = this.programService.findAllByMachine(machine);

    StringBuilder sb = new StringBuilder();
    sb.append("Etes vous sur de vouloir supprimer la machine '")
        .append(machine.getName())
        .append("', les ")
        .append(programs.size())
        .append(" programmes et les ")
        .append(templates.size())
        .append(" modèles associés ?");

    Dialog dialog = new Dialog();
    H3 text = new H3(sb.toString());

    Button btnYes = new Button("Oui");
    btnYes.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    btnYes.addClickListener(
        e -> {
          dialog.close();
          programs.forEach(p -> this.stepService.deleteSteps(p.getSteps()));

          List<Program> programList = this.programService.findAllByMachine(machine);
          this.programService.deletePrograms(programList);
          this.templateService.deleteTemplated(templates);
          this.machineService.deleteMachine(machine);

          StringBuilder sb2 = new StringBuilder();
          sb2.append("La machine ")
              .append(machine.getName())
              .append("', les ")
              .append(programs.size())
              .append(" programmes et les ")
              .append(templates.size())
              .append(" modèles associés ont été supprimés");
          NotificationRed notificationRed =
              new NotificationRed(
                  sb2.toString());
          notificationRed.open();
        });

    Button btnNo = new Button("Non");
    btnNo.addThemeVariants(ButtonVariant.LUMO_ERROR);
    btnNo.addClickListener(e -> dialog.close());

    HorizontalLayout hrztL = new HorizontalLayout();
    hrztL.add(btnNo, btnYes);
    hrztL.setWidthFull();
    hrztL.setJustifyContentMode(JustifyContentMode.END);

    dialog.add(text, hrztL);
    dialog.open();
  }

  @Override
  public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
    Location location = beforeEnterEvent.getLocation();
    QueryParameters queryParameters = location.getQueryParameters();

    this.parametersMap = queryParameters.getParameters();
    handleParameters();
  }
}
