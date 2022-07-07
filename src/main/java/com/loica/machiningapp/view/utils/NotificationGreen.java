package com.loica.machiningapp.view.utils;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class NotificationGreen extends Notification {

  public NotificationGreen(String content) {
    this.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    Icon icon = VaadinIcon.CHECK_CIRCLE.create();
    Div info;

    info = new Div(new Text(content));

    HorizontalLayout layout = new HorizontalLayout(icon, info, createCloseBtn(this));
    layout.setAlignItems(FlexComponent.Alignment.CENTER);

    this.add(layout);
    this.setDuration(5000);
  }

  public static Button createCloseBtn(Notification notification) {
    Button closeBtn =
        new Button(VaadinIcon.CLOSE_SMALL.create(), clickEvent -> notification.close());
    closeBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);

    return closeBtn;
  }
}
