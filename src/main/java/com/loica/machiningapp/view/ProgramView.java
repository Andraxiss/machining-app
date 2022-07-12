package com.loica.machiningapp.view;

import com.loica.machiningapp.domain.model.Program;
import com.loica.machiningapp.domain.model.Step;
import com.loica.machiningapp.domain.model.Template;
import com.loica.machiningapp.domain.service.MachineService;
import com.loica.machiningapp.domain.service.ProgramService;
import com.loica.machiningapp.domain.service.StepService;
import com.loica.machiningapp.domain.service.TemplateService;
import com.loica.machiningapp.domain.service.TxtService;
import com.loica.machiningapp.view.utils.MachineFinder;
import com.loica.machiningapp.view.utils.NotificationGreen;
import com.loica.machiningapp.view.utils.NotificationRed;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.grid.dnd.GridDropLocation;
import com.vaadin.flow.component.grid.dnd.GridDropMode;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import java.util.Comparator;
import java.util.List;
import javax.annotation.security.PermitAll;

@PermitAll
@RouteAlias(value = "program/edit", layout = MainLayout.class)
@Route(value = "program", layout = MainLayout.class)
@PageTitle("Gérer un programme")
@CssImport("./styles/program.css")
public class ProgramView extends VerticalLayout implements BeforeEnterObserver {
  private final MachineService machineService;
  private final TemplateService templateService;
  private final ProgramService programService;
  private final StepService stepService;
  private final TxtService txtService;

  private Program program;

  private Step currentStep;
  private Step draggedStep;
  private boolean hasNotifRedOpen;

  TextField name = new TextField();
  TextField programNumber = new TextField();
  TextField fileName = new TextField();
  TextArea stepContent;
  TextArea preview;
  Grid<Step> stepGrid;

  private String notificationKey = "program-created";

  public ProgramView(
      MachineService machineService,
      TemplateService templateService,
      StepService stepService,
      ProgramService programService,
      TxtService txtService) {
    this.txtService = txtService;
    this.machineService = machineService;
    this.templateService = templateService;
    this.stepService = stepService;
    this.programService = programService;
  }

  // Layout handler

  private void prepareView() {
    H1 title = new H1();
    title.setText("Gestion des programmes");
    title.setClassName("title");

    Button btnAddStep = new Button("Ajouter une opération");
    btnAddStep.addClickListener(e -> addStep());
    btnAddStep.setClassName("btn-step");

    add(title, createTopSection(), btnAddStep, createCenter(), createButtons());
    this.refreshGrid();
  }

  private HorizontalLayout createTopSection() {

    TextField machineArea = new TextField();
    machineArea.setValue(program.getMachine().getName());
    machineArea.setLabel("Machine");
    machineArea.setWidth(300, Unit.PIXELS);
    machineArea.setReadOnly(true);

    name.setLabel("Nom du programme");
    name.setRequiredIndicatorVisible(true);
    name.setErrorMessage("Ce champ est obligatoire");
    name.setWidth(300, Unit.PIXELS);
    name.setValue(program.getName());

    this.programNumber.setLabel("Numéro du programme");
    this.programNumber.setRequiredIndicatorVisible(true);
    this.programNumber.setErrorMessage("Ce champ est obligatoire");
    this.programNumber.setWidth(300, Unit.PIXELS);
    this.programNumber.setValue(program.getProgramNumber());

    fileName.setReadOnly(true);
    fileName.setLabel("Ce programme sera enregistré sous le nom de : ");
    fileName.setWidthFull();

    HorizontalLayout horizontalLayout = new HorizontalLayout();
    horizontalLayout.add(machineArea, programNumber, name, fileName);
    horizontalLayout.setAlignItems(Alignment.CENTER);

    return horizontalLayout;
  }

  private HorizontalLayout createCenter() {
    stepGrid = new Grid<>();
    stepGrid.addColumn(Step::getName).setHeader("Opération");
    stepGrid
        .addColumn(
            new ComponentRenderer<>(
                Button::new,
                (button, step) -> {
                  button.addThemeVariants(
                      ButtonVariant.LUMO_ICON,
                      ButtonVariant.LUMO_ERROR,
                      ButtonVariant.LUMO_TERTIARY);
                  button.addClickListener(
                      e -> {
                        this.deleteStep(step);
                      });
                  button.setIcon(new Icon(VaadinIcon.TRASH));
                }))
        .setWidth("20px");
    stepGrid.setWidth(555, Unit.PIXELS);

    stepGrid
        .asSingleSelect()
        .addValueChangeListener(
            event -> {
              if (event.getValue() != null) {
                this.setCurrentStep(event.getValue());
                this.refreshGrid();
              }
            });

    this.stepContent = new TextArea();
    this.stepContent.setWidthFull();

    preview = new TextArea();
    preview.setLabel("Prévisualisation du programme");
    preview.setReadOnly(true);
    preview.setWidthFull();

    HorizontalLayout horizontalLayout = new HorizontalLayout();
    horizontalLayout.add(stepGrid, stepContent, preview);
    horizontalLayout.setWidthFull();
    horizontalLayout.setMaxHeight(50, Unit.VH);

    return horizontalLayout;
  }

  private void handleDragDrop() {
    program.getSteps().sort(Comparator.comparingInt(Step::getRank));
    GridListDataView<Step> gridData = stepGrid.setItems(program.getSteps());

    stepGrid.setRowsDraggable(true);
    stepGrid.setDropMode(GridDropMode.BETWEEN);

    stepGrid.addDragStartListener(
        e -> {
          if (this.currentStep == null){
            draggedStep = e.getDraggedItems().get(0);
          } else {
            if (!hasNotifRedOpen){
              NotificationRed notificationRed = new NotificationRed("Veuillez sauvegarder avant d'essayer de déplacer une opération");
              notificationRed.open();
              hasNotifRedOpen = true;
              notificationRed.addOpenedChangeListener(event -> hasNotifRedOpen= notificationRed.isOpened() );
            }

          }
        });

    stepGrid.addDropListener(
        e -> {
          Step targetStep = e.getDropTargetItem().orElse(null);

          if (targetStep == null || draggedStep.equals(targetStep)) {
            this.draggedStep = null;
          } else {
            gridData.removeItem(draggedStep);
            if (e.getDropLocation() == GridDropLocation.BELOW) {
              gridData.addItemAfter(draggedStep, targetStep);
              this.calculateStepRank();
            } else {
              gridData.addItemBefore(draggedStep, targetStep);
              this.calculateStepRank();
            }

            this.draggedStep = null;
            this.refreshGrid();
          }
        });
  }

  private void refreshGrid() {
    this.program = this.programService.findById(program.getId());
    this.handleDragDrop();

    if (this.currentStep != null) {
      stepContent.setValue(currentStep.getContent());
      this.stepContent.setLabel("Contenu de l'opération : " + this.currentStep.getName());
      this.stepContent.setReadOnly(false);
    } else {
      this.stepContent.setLabel("Aucune opération sélectionnée");
      this.stepContent.clear();
      this.stepContent.setReadOnly(true);
    }

    fileName.setValue(program.getProgramNumber() + " - " + program.getName() + ".txt");
    StringBuilder sb = new StringBuilder();
    if (this.program.getSteps() != null) {
      this.program.getSteps().forEach(step -> sb.append(step.getContent() + "\n\n"));
      preview.setValue(sb.toString());
    }
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

    Button btnExport = new Button("Sauvegarder et exporter");
    btnExport.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    btnExport.addClickListener(event -> {
      this.validateAndSave();
      txtService.createFile(this.program);
      NotificationGreen notificationGreen = new NotificationGreen("Programme sauvgardé sous le nom de "+program.getProgramNumber() + " - " + name.getValue() + ".txt");
      notificationGreen.open();
    });

    Button btnDelete = new Button("Supprimer le programme");
    btnDelete.addThemeVariants(ButtonVariant.LUMO_ERROR);
    btnDelete.addClickListener(event -> {
      this.deleteProgram(program);
    });

    HorizontalLayout horizontalLayout = new HorizontalLayout();
    horizontalLayout.setJustifyContentMode(JustifyContentMode.END);
    horizontalLayout.setWidthFull();
    horizontalLayout.add(btnBack,btnDelete, btnSave,btnExport);
    return horizontalLayout;
  }

  private void validateAndSave() {
    this.setCurrentStep(null);

    this.program.setName(name.getValue());
    this.program.setProgramNumber(programNumber.getValue());
    this.program = this.programService.saveProgram(program);
    this.refreshGrid();
    NotificationGreen notificationGreen =
        new NotificationGreen("Le programme a bien été sauvegardé");
    notificationGreen.open();
  }

  private void goHome() {
    this.getUI()
        .ifPresent(
            ui -> {
              ui.navigate("");
            });
  }

  // Step handler

  private void addStep() {
    Dialog dialog = new Dialog();
    dialog.setHeaderTitle("Choix du modèle pour l'opération");

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

    Select<Template> template = new Select<>();
    TextField stepName = new TextField();

    template.setLabel("Sélectionner un modèle");
    template.setItems(this.templateService.findByMachine(this.program.getMachine()));
    template.setItemLabelGenerator(Template::getName);
    template.setWidthFull();
    template.setEmptySelectionCaption("");
    template.addValueChangeListener(
        e -> {
          if (!stepName.getValue().isEmpty()) btnValidate.setEnabled(true);
        });

    stepName.setLabel("Nom de l'opération");
    stepName.setRequiredIndicatorVisible(true);
    stepName.addValueChangeListener(
        e -> {
          btnValidate.setEnabled(true);
        });
    stepName.setValueChangeMode(ValueChangeMode.LAZY);
    stepName.setValueChangeTimeout(300);

    btnValidate.addClickListener(
        e -> {
          Step step = new Step();
          if (template.getValue() != null) {
            step.setContent(template.getValue().getContent());
          } else {
            step.setContent("");
          }

          if (stepName.getValue() == null) {
            stepName.setInvalid(true);
          } else {
            step.setProgram(this.program);
            step.setName(stepName.getValue());
            if (this.program.getSteps() != null && this.program.getSteps().size() != 0) {
              step.setRank(this.program.getSteps().size());
            } else {
              step.setRank(0);
            }
            Step stepSaved = this.stepService.save(step);

            this.setCurrentStep(stepSaved);
            this.refreshGrid();
            dialog.close();
          }
        });

    VerticalLayout verticalLayout = new VerticalLayout();
    verticalLayout.add(template, stepName, btnLayout);
    verticalLayout.setWidth(500, Unit.PIXELS);

    dialog.add(verticalLayout);
    dialog.open();
  }

  private void deleteStep(Step step) {
    this.setCurrentStep(null);

    this.stepService.deleteStep(step);
    this.program = this.programService.findById(program.getId());
    program.getSteps().sort(Comparator.comparingInt(Step::getRank));
    this.calculateStepRank();
    this.refreshGrid();
  }

  private void setCurrentStep(Step step) {
    if (this.currentStep != null) {
      this.currentStep.setContent(stepContent.getValue());
      this.stepService.save(currentStep);
    }
    this.currentStep = step;
  }

  private void calculateStepRank() {
    this.program.getSteps().forEach(s -> s.setRank(this.program.getSteps().indexOf(s)));
    this.stepService.saveAll(this.program.getSteps());
  }

  private void handleCreate() {

    Dialog dialog = new Dialog();
    dialog.setHeaderTitle("Créer un programme");

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

    TextField programName = new TextField();
    TextField programNumber = new TextField();

    programName.setLabel("Nom du programme");
    programName.setRequiredIndicatorVisible(true);
    programName.setValueChangeMode(ValueChangeMode.LAZY);
    programName.setValueChangeTimeout(300);
    programName.setPlaceholder("Ex: Client - Nom Pièce");
    programName.addValueChangeListener(
        e -> {
          if (machineFinder.getValue() != null && !programNumber.getValue().isEmpty()) {
            btnValidate.setEnabled(true);
          }
        });

    programNumber.setLabel("Numéro du programme");
    programNumber.setRequiredIndicatorVisible(true);
    programNumber.setValueChangeMode(ValueChangeMode.LAZY);
    programNumber.setValueChangeTimeout(300);
    programNumber.setPlaceholder("Ex: O2500");
    programNumber.addValueChangeListener(
        e -> {
          if (machineFinder.getValue() != null && !programName.getValue().isEmpty()) {
            btnValidate.setEnabled(true);
          }
        });

    machineFinder.addValueChangeListener(
        machine -> {
          if (!programName.getValue().isEmpty()) {
            btnValidate.setEnabled(true);
          }
        });

    btnValidate.addClickListener(
        e -> {
          if (!machineFinder.isEmpty() && programName.getValue() != null) {
            this.program.setName(programName.getValue());
            this.program.setMachine(machineFinder.getValue());
            this.program.setProgramNumber(programNumber.getValue());
            this.programService.saveProgram(program);
            this.prepareView();
            dialog.close();
          }
        });

    VerticalLayout verticalLayout = new VerticalLayout();
    verticalLayout.add(machineFinder, programNumber, programName, btnLayout);
    verticalLayout.setWidth(500, Unit.PIXELS);

    dialog.add(verticalLayout);
    dialog.open();
  }

  private void handleEdit() {

    Dialog dialog = new Dialog();
    dialog.setHeaderTitle("Choix du programme à éditer");

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

    Select<Program> programToEdit = new Select<>();
    programToEdit.setWidthFull();
    programToEdit.setLabel("Programme");
    programToEdit.setRequiredIndicatorVisible(true);
    programToEdit.setEnabled(false);

    machineFinder.addValueChangeListener(
        machine -> {
          List<Program> programs = this.programService.findAllByMachine(machineFinder.getValue());
          programToEdit.setEnabled(true);
          programToEdit.setItems(programs);
          programToEdit.setItemLabelGenerator(p -> p.getProgramNumber() + " - " + p.getName());
          programToEdit.setPlaceholder("Sélectionner la programme à éditer");
        });

    programToEdit.addValueChangeListener(
        e -> {
          btnValidate.setEnabled(true);
        });

    btnValidate.addClickListener(
        e -> {
          this.program = programToEdit.getValue();
          this.prepareView();
          dialog.close();
        });

    VerticalLayout verticalLayout = new VerticalLayout();
    verticalLayout.add(machineFinder, programToEdit, btnLayout);
    verticalLayout.setWidth(500, Unit.PIXELS);

    dialog.add(verticalLayout);
    dialog.open();
  }

  private void deleteProgram(Program program){
    Dialog dialog = new Dialog();
    H3 text = new H3("Voulez vous vraiment supprimer le programme "+program.getName()+"?");

    Button btnYes = new Button("Oui");
    btnYes.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    btnYes.addClickListener(e-> {
      dialog.close();
      this.stepService.deleteSteps(program.getSteps());
      this.programService.deleteProgram(programService.findById(program.getId()));
      NotificationRed notificationRed = new NotificationRed("Le programme "+program.getName()+" a été supprimé");
      notificationRed.open();
      goHome();
    });
    Button btnNo = new Button("Non");
    btnNo.addThemeVariants(ButtonVariant.LUMO_ERROR);
    btnNo.addClickListener(e -> dialog.close());

    HorizontalLayout hrztL = new HorizontalLayout();
    hrztL.add(btnNo,btnYes);
    hrztL.setWidthFull();
    hrztL.setJustifyContentMode(JustifyContentMode.END);

    dialog.add(text,hrztL);
    dialog.open();
  }

  @Override
  public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
    if (beforeEnterEvent.getLocation().getSegments().size() > 1
        && beforeEnterEvent.getLocation().getSegments().get(1).equals("edit")) {
      this.notificationKey = "program-edited";
      handleEdit();
    } else {
      this.program = new Program();
      this.handleCreate();
    }
  }
}
