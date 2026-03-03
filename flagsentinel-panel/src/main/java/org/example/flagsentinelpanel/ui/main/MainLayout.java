package org.example.flagsentinelpanel.ui.main;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLink;
import org.example.flagsentinelpanel.ui.featureflags.FeatureFlagsView;
import org.example.flagsentinelpanel.ui.rules.RulesView;
import org.example.flagsentinelpanel.ui.users.UsersView;

public class MainLayout extends AppLayout {

    public MainLayout() {
        createHeader();
        createDrawer();
        UI.getCurrent().getPage().executeJs(
                "window.history.replaceState(null, '', window.location.href);"
                        + "window.addEventListener('popstate', function () {"
                        + " if (!localStorage.getItem('jwt')) {"
                        + " window.location.replace('login');"
                        + " }"
                        + "});"
        );
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

        // === LOGOUT BUTTON (derecha) ===
        Button logoutButton = new Button(new Icon(VaadinIcon.SIGN_OUT));
        logoutButton.addClassName("inline-action");
        logoutButton.getStyle().set("background", "transparent");
        logoutButton.getStyle().set("color", "#00A8FF");

        logoutButton.addClickListener(e -> {
            UI.getCurrent().getPage().executeJs("localStorage.removeItem('jwt');");
            UI.getCurrent().getSession().close();
            UI.getCurrent().getPage().setLocation("login");
        });

        // === SPACER para empujar logout a la derecha ===
        Div spacer = new Div();
        spacer.getStyle().set("flex-grow", "1");

        HorizontalLayout header = new HorizontalLayout(
                new DrawerToggle(),
                shield,
                logo,
                spacer,        // empuja logout a la derecha
                logoutButton
        );

        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setSpacing(true);
        header.setPadding(true);
        header.setWidthFull();

        addToNavbar(header);
    }


    private void createDrawer() {

        Span menuTitle = new Span();
        menuTitle.getStyle()
                .set("color", "#8FA3BF")
                .set("font-weight", "600")
                .set("font-size", "0.9rem");

        // --- Inicio ---
        RouterLink home = new RouterLink("Home", MainView.class);
        home.getStyle().set("color", "#E5ECF5");
        Icon homeIcon = VaadinIcon.HOME.create();
        homeIcon.setColor("#00A8FF");
        homeIcon.setSize("18px");
        HorizontalLayout homeItem = new HorizontalLayout(homeIcon, home);
        homeItem.setAlignItems(FlexComponent.Alignment.CENTER);
        homeItem.addClassName("menu-link");

        // --- Rules ---
        RouterLink rules = new RouterLink("Rules", RulesView.class);
        rules.getStyle().set("color", "#E5ECF5");
        Icon rulesIcon = VaadinIcon.COG.create();
        rulesIcon.setColor("#00A8FF");
        rulesIcon.setSize("18px");
        HorizontalLayout rulesItem = new HorizontalLayout(rulesIcon, rules);
        rulesItem.setAlignItems(FlexComponent.Alignment.CENTER);
        rulesItem.addClassName("menu-link");

        // --- User ---
        RouterLink user = new RouterLink("Users", UsersView.class);
        user.getStyle().set("color", "#E5ECF5");
        Icon userIcon = VaadinIcon.USERS.create();
        userIcon.setColor("#00A8FF");
        userIcon.setSize("18px");
        HorizontalLayout userItem = new HorizontalLayout(userIcon, user);
        userItem.setAlignItems(FlexComponent.Alignment.CENTER);
        userItem.addClassName("menu-link");

        // --- Feature Flag ---
        RouterLink fFlag = new RouterLink("FeatureFlags", FeatureFlagsView.class);
        fFlag.getStyle().set("color", "#E5ECF5");
        Icon fFlagIcon = VaadinIcon.USERS.create();
        fFlagIcon.setColor("#00A8FF");
        fFlagIcon.setSize("18px");
        HorizontalLayout featureFlagItem = new HorizontalLayout(fFlagIcon, fFlag);
        featureFlagItem.setAlignItems(FlexComponent.Alignment.CENTER);
        featureFlagItem.addClassName("menu-link");

        VerticalLayout menu = new VerticalLayout(menuTitle, homeItem, rulesItem, featureFlagItem, userItem);
        menu.setPadding(true);
        menu.setSpacing(true);

        addToDrawer(menu);
    }

}
