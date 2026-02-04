package org.example.flagsentinelpanel.views;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.example.flagsentinelpanel.layout.MainLayout;

@PermitAll
@Route(value = "main", layout = MainLayout.class)
public class MainView extends VerticalLayout {

    public MainView() {

        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        H1 title = new H1("Bienvenido a FlagSentinel");
        title.getStyle()
                .set("color", "#E5ECF5")
                .set("font-size", "2.2rem");

        add(title);
    }
}
