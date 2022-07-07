package com.loica.machiningapp.view;

import com.loica.machiningapp.domain.model.Machine;
import com.loica.machiningapp.domain.model.Template;
import com.loica.machiningapp.domain.service.MachineService;
import com.loica.machiningapp.domain.service.TemplateService;
import com.loica.machiningapp.view.utils.MachineFinder;
import com.loica.machiningapp.view.utils.NotificationGreen;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import java.util.List;
import java.util.Map;
import javax.annotation.security.PermitAll;

@PermitAll
@RouteAlias(value = "template/edit", layout = MainLayout.class)
@Route(value = "template", layout = MainLayout.class)
@PageTitle("Créer un modèle")
@CssImport("./styles/template.css")
public class TemplateView extends VerticalLayout implements BeforeEnterObserver {
  private final MachineService machineService;
  private final TemplateService templateService;

  MachineFinder machine;
  TextField name = new TextField();
  TextArea content = new TextArea();

  private Template template;
  Binder<Template> binder = new BeanValidationBinder<>(Template.class);

  private String notificationKey = "template-created";

  public TemplateView(MachineService machineService, TemplateService templateService) {
    this.machineService = machineService;
    this.templateService = templateService;

    this.template = new Template();
    this.machine = new MachineFinder(machineService.getMachines());
    binder.bindInstanceFields(this);


    Button btnCreateMachine = new Button("Créer une machine");
    btnCreateMachine.addClickListener(e -> createMachine());
    btnCreateMachine.setClassName("btn-machine");

    content.setLabel("Contenu");
    content.setWidthFull();
    content.setHeight(50, Unit.VH);

    H1 title = new H1();
    title.setText("Gestion des modèles");
    title.setClassName("title");

    add(title, createTopSection(), btnCreateMachine, content, createButtons());
  }

  private HorizontalLayout createTopSection() {
    machine.setWidth(300,Unit.PIXELS);

    name.setLabel("Nom du modèle");
    name.setRequired(true);
    name.setRequiredIndicatorVisible(true);
    name.setErrorMessage("Ce champ est obligatoire");
    name.setWidth(300,Unit.PIXELS);

    HorizontalLayout horizontalLayout = new HorizontalLayout();
    horizontalLayout.add(machine, name);
    horizontalLayout.setAlignItems(Alignment.CENTER);

    return horizontalLayout;
  }

  private HorizontalLayout createButtons() {
    Button btnBack = new Button("Retour");
    btnBack.addClickListener(
        e -> {
          goHome();
        });

    Button btnSave = new Button("Sauvegarder");
    btnSave.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    btnSave.addClickListener(event -> validateAndSave());

    HorizontalLayout horizontalLayout = new HorizontalLayout();
    horizontalLayout.setJustifyContentMode(JustifyContentMode.END);
    horizontalLayout.setWidthFull();
    horizontalLayout.add(btnBack, btnSave);
    return horizontalLayout;
  }

  private void validateAndSave() {
      if ((this.template == null)) {
        this.template = new Template();
      }

      binder.forField(machine).bind(Template::getMachine,Template::setMachine);

      try{
        binder.writeBean(template);
      } catch (ValidationException e){
        System.out.println(e.getValidationErrors());
      }


      this.templateService.saveTemplate(template);
      this.getUI()
          .ifPresent(
              ui ->
                  ui.navigate(
                      "",
                      new QueryParameters(
                          Map.of(this.notificationKey, List.of(template.getName())))));
  }

  private void handleEdit() {

    Dialog dialog = new Dialog();
    dialog.setHeaderTitle("Choix du modèle à éditer");

    Button btnValidate = new Button("Valider");
    btnValidate.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    btnValidate.setEnabled(false);

    Button btnBack = new Button("Retour");
    btnBack.addClickListener(
        e -> {
          dialog.close();
          goHome();
        });

    HorizontalLayout btnLayout = new HorizontalLayout();
    btnLayout.setJustifyContentMode(JustifyContentMode.END);
    btnLayout.setWidthFull();
    btnLayout.add(btnBack, btnValidate);

    MachineFinder machineFinder = new MachineFinder(this.machineService.getMachines());
    machineFinder.setWidthFull();

    Select<Template> templateToEdit = new Select<>();
    templateToEdit.setWidthFull();
    templateToEdit.setLabel("Modèle");
    templateToEdit.setRequiredIndicatorVisible(true);
    templateToEdit.setEnabled(false);

    machineFinder.addValueChangeListener(
        machine -> {
          List<Template> templates = this.templateService.findByMachine(machineFinder.getValue());
          templateToEdit.setEnabled(true);
          templateToEdit.setItems(templates);
          templateToEdit.setItemLabelGenerator(Template::getName);
          templateToEdit.setPlaceholder("Sélectionner la modèle à éditer");
        });

    templateToEdit.addValueChangeListener(
        e -> {
          btnValidate.setEnabled(true);
        });

    btnValidate.addClickListener(
        e -> {
          this.template = templateToEdit.getValue();
          binder.readBean(template);
          this.machine.setValue(template.getMachine());
          dialog.close();
        });

    VerticalLayout verticalLayout = new VerticalLayout();
    verticalLayout.add(machineFinder, templateToEdit, btnLayout);
    verticalLayout.setWidth(500, Unit.PIXELS);

    dialog.add(verticalLayout);
    dialog.open();
  }

  private void goHome() {
    this.getUI()
        .ifPresent(
            ui -> {
              ui.navigate("");
            });
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
          if (!machineName.getValue().isBlank()){
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

      List<Machine> machineList = this.machineService.getMachines();
      machine.setItems(machineList);
      machine.setValue(
          machineList.stream()
              .filter(m -> m.getName().equals(machineName))
              .findFirst()
              .orElse(null));

  }

  @Override
  public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
    if (beforeEnterEvent.getLocation().getSegments().size() > 1
        && beforeEnterEvent.getLocation().getSegments().get(1).equals("edit")) {
      this.notificationKey = "template-edited";
      handleEdit();
    }
  }
}
