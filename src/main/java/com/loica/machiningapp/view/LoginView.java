package com.loica.machiningapp.view;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("login")
@PageTitle("Login | Usinapp")
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

  private final LoginForm login = new LoginForm();

  public LoginView(){
    addClassName("login-view");
    configureLogin();
    setSizeFull();
    setAlignItems(Alignment.CENTER);
    setJustifyContentMode(JustifyContentMode.CENTER);

    login.setAction("login");

    add(new H1("Usinapp"), login);
  }

  @Override
  public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
    // inform the user about an authentication error
    if(beforeEnterEvent.getLocation()
        .getQueryParameters()
        .getParameters()
        .containsKey("error")) {
      login.setError(true);
    }
  }

  private void configureLogin(){
    LoginI18n i18n = LoginI18n.createDefault();

    LoginI18n.Form i18nForm = i18n.getForm();
    i18nForm.setTitle("Connexion");
    i18nForm.setUsername("Nom d'utilisateur");
    i18nForm.setPassword("Mot de passe");
    i18nForm.setSubmit("Se connecter");
    i18n.setForm(i18nForm);

    LoginI18n.ErrorMessage i18nErrorMessage = i18n.getErrorMessage();
    i18nErrorMessage.setTitle("Une erreur est survenue");
    i18nErrorMessage.setMessage("Veuillez remplir tous les champs");
    i18n.setErrorMessage(i18nErrorMessage);

    this.login.setI18n(i18n);

    login.setForgotPasswordButtonVisible(false);
  }
}