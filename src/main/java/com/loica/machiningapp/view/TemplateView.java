package com.loica.machiningapp.view;

import com.loica.machiningapp.domain.model.Machine;
import com.loica.machiningapp.domain.model.Template;
import com.loica.machiningapp.domain.service.MachineService;
import com.loica.machiningapp.domain.service.TemplateService;
import com.loica.machiningapp.view.utils.MachineFinder;
import com.loica.machiningapp.view.utils.NotificationGreen;
import com.loica.machiningapp.view.utils.NotificationRed;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
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

  TextField machine = new TextField();
  TextField name = new TextField();
  TextArea content = new TextArea();

  private Template template;

  private String notificationKey = "template-created";

  public TemplateView(MachineService machineService, TemplateService templateService) {
    this.machineService = machineService;
    this.templateService = templateService;
  }

  private void generateView() {
    this.machine.setLabel("Machine");
    this.machine.setReadOnly(true);
    this.machine.setValue(template.getMachine().getName());

    content.setLabel("Contenu");
    content.setWidthFull();
    content.setHeight(50, Unit.VH);
    if (template.getContent() != null){
      content.setValue(template.getContent());
    }

    name.setValue(template.getName());

    H1 title = new H1();
    title.setText("Gestion des modèles");
    title.setClassName("title");

    add(title, createTopSection(), content, createButtons());
  }

  private HorizontalLayout createTopSection() {
    machine.setWidth(300, Unit.PIXELS);

    name.setLabel("Nom du modèle");
    name.setRequired(true);
    name.setRequiredIndicatorVisible(true);
    name.setErrorMessage("Ce champ est obligatoire");
    name.setWidth(300, Unit.PIXELS);

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

    Button btnDelete = new Button("Supprimer le modèle");
    btnDelete.addThemeVariants(ButtonVariant.LUMO_ERROR);
    btnDelete.addClickListener(
        event -> {
          this.deleteTemplate(template);
        });

    HorizontalLayout horizontalLayout = new HorizontalLayout();
    horizontalLayout.setJustifyContentMode(JustifyContentMode.END);
    horizontalLayout.setWidthFull();
    horizontalLayout.add(btnBack, btnDelete, btnSave);
    return horizontalLayout;
  }

  private void validateAndSave() {
    if ((this.template == null)) {
      this.template = new Template();
    }

    if (!content.isEmpty()) {
      template.setContent(content.getValue());
      content.setInvalid(false);
    } else {
      content.setInvalid(true);
    }

    if (!name.getValue().isEmpty()) {
      template.setName(name.getValue());
      name.setInvalid(false);
    } else {
      name.setInvalid(true);
    }

    if (!name.isInvalid() && !content.isInvalid() && !machine.isInvalid()) {
      this.templateService.saveTemplate(template);
      this.getUI()
          .ifPresent(
              ui ->
                  ui.navigate(
                      "",
                      new QueryParameters(
                          Map.of(this.notificationKey, List.of(template.getName())))));
    }
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

    MachineFinder machineFinderEdit = new MachineFinder(this.machineService.getMachines());
    machineFinderEdit.setWidthFull();

    Select<Template> templateToEdit = new Select<>();
    templateToEdit.setWidthFull();
    templateToEdit.setLabel("Modèle");
    templateToEdit.setRequiredIndicatorVisible(true);
    templateToEdit.setEnabled(false);

    machineFinderEdit.addValueChangeListener(
        machine -> {
          List<Template> templates =
              this.templateService.findByMachine(machineFinderEdit.getValue());
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
          this.machine.setValue(template.getMachine().getName());
          this.content.setValue(template.getContent());
          this.name.setValue(template.getName());
          generateView();
          dialog.close();
        });

    VerticalLayout verticalLayout = new VerticalLayout();
    verticalLayout.add(machineFinderEdit, templateToEdit, btnLayout);
    verticalLayout.setWidth(500, Unit.PIXELS);

    dialog.add(verticalLayout);
    dialog.open();
  }

  private void handleCreate() {
    Dialog dialog = new Dialog();
    dialog.setHeaderTitle("Créer un modèle");

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

    TextField templateName = new TextField();

    templateName.setLabel("Nom du modèle");
    templateName.setRequiredIndicatorVisible(true);
    templateName.setValueChangeMode(ValueChangeMode.LAZY);
    templateName.setValueChangeTimeout(300);
    templateName.setPlaceholder("Ex: Taraudage");
    templateName.addValueChangeListener(
        e -> {
          if (machineFinder.getValue() != null) {
            btnValidate.setEnabled(true);
          }
        });

    machineFinder.addValueChangeListener(
        machine -> {
          if (!templateName.getValue().isEmpty()) {
            btnValidate.setEnabled(true);
          }
        });

    btnValidate.addClickListener(
        e -> {
          if (!machineFinder.isEmpty() && templateName.getValue() != null) {
            this.template.setName(templateName.getValue());
            this.template.setMachine(machineFinder.getValue());
            this.generateView();
            dialog.close();
          }
        });

    VerticalLayout verticalLayout = new VerticalLayout();
    verticalLayout.add(machineFinder, templateName, btnLayout);
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

  private void deleteTemplate(Template template) {
    Dialog dialog = new Dialog();
    H3 text = new H3("Voulez vous vraiment supprimer le modèle " + template.getName() + "?");

    Button btnYes = new Button("Oui");
    btnYes.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    btnYes.addClickListener(
        e -> {
          dialog.close();
          this.templateService.deleteTemplate(template);
          NotificationRed notificationRed =
              new NotificationRed("Le modèle " + template.getName() + " a été supprimé");
          notificationRed.open();
          goHome();
        });
    Button btnNo = new Button("No");
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
    if (beforeEnterEvent.getLocation().getSegments().size() > 1
        && beforeEnterEvent.getLocation().getSegments().get(1).equals("edit")) {
      this.notificationKey = "template-edited";
      handleEdit();
    } else {
      this.template = new Template();
      handleCreate();
    }
  }
}
