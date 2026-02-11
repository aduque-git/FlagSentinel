package org.example.flagsentinelpanel.ui.rules;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.example.flagsentinelpanel.ui.main.MainLayout;
import org.example.flagsentinelpanel.ui.rules.components.RulesListComponent;
import org.example.flagsentinelpanel.ui.rules.service.RulesService;

@PermitAll
@Route(value = "rules", layout = MainLayout.class)
@PageTitle("Rules | FlagSentinel")
public class RulesView extends HorizontalLayout {

    private final RulesListComponent list;

    public RulesView(RulesService rulesService) {

        setSizeFull();
        setPadding(true);
        setSpacing(true);
        setAlignItems(Alignment.CENTER); // centra horizontalmente
        setJustifyContentMode(JustifyContentMode.CENTER); // o CENTER si quieres centrar verticalmente

        list = new RulesListComponent(rulesService);

        add(list);
    }

}
