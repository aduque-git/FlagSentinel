package org.example.flagsentinelpanel.layout;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLink;

public class MainLayout extends AppLayout {

    public MainLayout() {
        createHeader();
        createDrawer();
    }

    private void createHeader() {

        Icon shield = VaadinIcon.SHIELD.create();
        shield.setColor("#00A8FF");
        shield.setSize("28px");

        H1 logo = new H1("FlagSentinel Panel");
        logo.getStyle()
                .set("font-size", "1.4em")
                .set("margin", "0")
                .set("color", "#E5ECF5");

        HorizontalLayout header = new HorizontalLayout(new DrawerToggle(), shield, logo);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setSpacing(true);
        header.setPadding(true);

        addToNavbar(header);
    }

    private void createDrawer() {

        Span menuTitle = new Span("Menú");
        menuTitle.getStyle()
                .set("color", "#8FA3BF")
                .set("font-weight", "600")
                .set("font-size", "0.9rem");

        RouterLink home = new RouterLink("Inicio", org.example.flagsentinelpanel.views.MainView.class);
        home.getStyle().set("color", "#E5ECF5");

        Icon homeIcon = VaadinIcon.HOME.create();
        homeIcon.setColor("#00A8FF");
        homeIcon.setSize("18px");

        HorizontalLayout homeItem = new HorizontalLayout(homeIcon, home);
        homeItem.setAlignItems(FlexComponent.Alignment.CENTER);

        VerticalLayout menu = new VerticalLayout(menuTitle, homeItem);
        menu.setPadding(true);
        menu.setSpacing(true);

        addToDrawer(menu);
    }
}
