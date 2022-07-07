package com.loica.machiningapp.view;

import com.loica.machiningapp.security.SecurityService;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.RouterLink;


@CssImport("./styles/layout.css")
public class MainLayout extends AppLayout {
  private final SecurityService securityService;

  public MainLayout(SecurityService securityService) {
    this.securityService =securityService;

    createHeader();
  }

  private void createHeader() {
    H1 title = new H1("Usinapp");
    title.addClassNames("text-l", "m-m");

    Button logout = new Button("Deconnexion", e -> securityService.logout());
    logout.addClassNames("logout_btn");

    RouterLink homeLink = new RouterLink("Acceuil", HomeView.class);
    homeLink.setHighlightCondition(HighlightConditions.sameLocation());

    HorizontalLayout header = new HorizontalLayout(title,homeLink, logout);

    header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
    header.setWidth("100%");
    header.expand(title);

    addToNavbar(header);

  }
}
