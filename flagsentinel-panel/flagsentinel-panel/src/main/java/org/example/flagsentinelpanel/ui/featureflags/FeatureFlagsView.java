package org.example.flagsentinelpanel.ui.featureflags;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.example.flagsentinelpanel.ui.featureflags.components.FeatureFlagListComponent;
import org.example.flagsentinelpanel.ui.featureflags.service.FeatureFlagsService;
import org.example.flagsentinelpanel.ui.main.MainLayout;
import org.example.flagsentinelpanel.ui.rules.service.RulesService;

@PermitAll
@Route(value = "feature-flags", layout = MainLayout.class)
@PageTitle("Feature Flags | FlagSentinel")
public class FeatureFlagsView extends HorizontalLayout {

    private final FeatureFlagListComponent list;

    public FeatureFlagsView(FeatureFlagsService featureFlagsService,
                            RulesService rulesService) {

        setSizeFull();
        setPadding(true);
        setSpacing(true);
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        list = new FeatureFlagListComponent(featureFlagsService, rulesService);

        add(list);
    }
}
