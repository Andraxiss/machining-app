package com.loica.machiningapp.view;

import com.loica.machiningapp.view.utils.NotificationGreen;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
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

  private Map<String, List<String>> parametersMap;

  public HomeView() {
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

    btnModel.setClassName("btn");
    btnProgram.setClassName("btn");
    btnEditModel.setClassName("btn");
    btnEditProgram.setClassName("btn");

    btnModel.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    btnProgram.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    btnEditModel.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    btnEditProgram.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

    btnModel.addClickListener(e -> btnModel.getUI().ifPresent(ui -> ui.navigate("template")));
    btnEditModel.addClickListener(
        e -> btnEditModel.getUI().ifPresent(ui -> ui.navigate("template/edit")));
    btnProgram.addClickListener(e -> btnModel.getUI().ifPresent(ui -> ui.navigate("program")));
    btnEditProgram.addClickListener(e -> btnModel.getUI().ifPresent(ui -> ui.navigate("program/edit")));

    HorizontalLayout horizontalLayout1 = new HorizontalLayout();
    horizontalLayout1.add(btnModel, btnProgram);

    HorizontalLayout horizontalLayout2 = new HorizontalLayout();
    horizontalLayout2.add(btnEditModel, btnEditProgram);

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

  @Override
  public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
    Location location = beforeEnterEvent.getLocation();
    QueryParameters queryParameters = location.getQueryParameters();

    this.parametersMap = queryParameters.getParameters();
    handleParameters();
  }
}
