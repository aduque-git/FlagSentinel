package org.example.flagsentinelpanel.ui.featureflags.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import org.example.flagsentinelpanel.dto.*;
import org.example.flagsentinelpanel.exceptions.ApiClientException;
import org.example.flagsentinelpanel.ui.components.AbstractCrudGrid;
import org.example.flagsentinelpanel.ui.components.PaginationProperties;
import org.example.flagsentinelpanel.ui.featureflags.service.FeatureFlagsService;
import org.example.flagsentinelpanel.ui.rules.service.RulesService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class FeatureFlagListComponent extends AbstractCrudGrid<FeatureFlagResponse> {

    private final FeatureFlagsService featureFlagsService;
    private final RulesService rulesService;

    private MultiSelectComboBox<RuleResponse> rulesField;
    private final PaginationProperties pagination;

    public FeatureFlagListComponent(FeatureFlagsService featureFlagsService,
                                    RulesService rulesService,
                                    PaginationProperties pagination) {

        super(FeatureFlagResponse.class, new Span("Feature Flags"));

        this.featureFlagsService = featureFlagsService;
        this.rulesService = rulesService;
        this.pagination = pagination;

        // IMPORTANTE: NO USAR DataProvider
        // El AbstractCrudGrid ya controla la paginación híbrida
        init();
    }

    @Override
    protected void configureColumns() {

        Binder<FeatureFlagResponse> binder = new Binder<>(FeatureFlagResponse.class);
        grid.getEditor().setBinder(binder);
        grid.getEditor().setBuffered(true);

        // ========= FLAG CODE =========
        TextField flagCodeField = new TextField();
        binder.forField(flagCodeField).bind("flagCode");

        Grid.Column<FeatureFlagResponse> colFlagCode = grid
                .addColumn(FeatureFlagResponse::getFlagCode)
                .setHeader("Flag Code")
                .setAutoWidth(true)
                .setFlexGrow(1);
        colFlagCode.setEditorComponent(flagCodeField);

        // ========= ENABLED =========
        Checkbox enabledField = new Checkbox();
        binder.forField(enabledField).bind("enabled");

        Grid.Column<FeatureFlagResponse> colEnabled = grid
                .addColumn(flag -> flag.isEnabled() ? "Enabled" : "Disabled")
                .setHeader("Enabled")
                .setAutoWidth(true)
                .setFlexGrow(0);
        colEnabled.setEditorComponent(enabledField);

        // ========= RULES =========
        List<RuleResponse> allRules = rulesService.findAll();

        rulesField = new MultiSelectComboBox<>();
        rulesField.setItems(allRules);
        rulesField.setItemLabelGenerator(RuleResponse::getAttribute);
        rulesField.addClassName("rules-multiselect");

        grid.getEditor().addOpenListener(event -> {
            FeatureFlagResponse item = event.getItem();
            if (item != null) {
                if (item.getRules() == null) {
                    item.setRules(new ArrayList<>());
                }

                Set<RuleResponse> toSelect = item.getRules().stream()
                        .map(r -> allRules.stream()
                                .filter(a -> a.getId().equals(r.getId()))
                                .findFirst()
                                .orElse(null))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());

                rulesField.setValue(toSelect);
            }
        });

        rulesField.addValueChangeListener(event -> {
            FeatureFlagResponse item = grid.getEditor().getItem();
            if (item != null) {
                item.setRules(event.getValue() != null
                        ? new ArrayList<>(event.getValue())
                        : new ArrayList<>());
            }
        });

        Grid.Column<FeatureFlagResponse> rulesColumn = grid.addColumn(flag ->
                        flag.getRules().stream()
                                .map(RuleResponse::getAttribute)
                                .collect(Collectors.joining(", "))
                )
                .setHeader("Rules")
                .setAutoWidth(true)
                .setFlexGrow(2);
        rulesColumn.setEditorComponent(rulesField);

        // ========= ACTIONS =========
        grid.addComponentColumn(flag -> {

                    HorizontalLayout actions = new HorizontalLayout();
                    actions.addClassName("no-hover");

                    rowButtons.computeIfAbsent(flag, k -> new ArrayList<>());

                    if (isEditing(flag)) {

                        Button save = new Button(new Icon(VaadinIcon.CHECK));
                        save.addClassName("inline-action");
                        rowButtons.get(flag).add(save);
                        save.addClickListener(e -> {
                            grid.getEditor().save();
                            save(flag);
                        });

                        Button cancel = new Button(new Icon(VaadinIcon.CLOSE));
                        cancel.addClassName("inline-action");
                        rowButtons.get(flag).add(cancel);
                        cancel.addClickListener(e -> cancelEdit());

                        actions.add(save, cancel);

                    } else {

                        Button edit = new Button(new Icon(VaadinIcon.EDIT));
                        edit.addClassName("inline-action");
                        rowButtons.get(flag).add(edit);
                        edit.addClickListener(e -> startEdit(flag));

                        Button delete = new Button(new Icon(VaadinIcon.TRASH));
                        delete.addClassName("inline-action");
                        rowButtons.get(flag).add(delete);
                        delete.addClickListener(e -> delete(flag));

                        actions.add(edit, delete);
                    }

                    return actions;

                })
                .setHeader("")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setClassNameGenerator(flag -> "no-hover");
    }

    @Override
    protected PageResponse<FeatureFlagResponse> fetchPage(int apiPage, int pageSize) {
        try {
            return featureFlagsService.findPaged(apiPage, pageSize);
        } catch (ApiClientException ex) {
            showErrorNotification(ex.getMessage());
            return new PageResponse<>(List.of(), 0, 0, pageSize, apiPage);
        }
    }


    @Override
    protected FeatureFlagResponse createEmpty() {
        return new FeatureFlagResponse(null, "", false, new ArrayList<>());
    }

    @Override
    protected void save(FeatureFlagResponse flag) {

        boolean isCreating = (creatingItem == flag);

        if (flag.getFlagCode() == null || flag.getFlagCode().isBlank()) {
            showErrorNotification("Flag code cannot be empty");
            cancelEdit();
            return;
        }

        flag.setRules(new ArrayList<>(rulesField.getSelectedItems()));

        List<Long> ruleIds = flag.getRules().stream()
                .map(RuleResponse::getId)
                .toList();

        try {
            if (isCreating) {
                featureFlagsService.create(new CreateFeatureFlagRequest(
                        flag.getFlagCode(),
                        flag.isEnabled(),
                        ruleIds
                ));

                cancelEdit();
                goToPageOfNewElement();
                return;

            } else {
                featureFlagsService.update(flag.getId(), new UpdateFeatureFlagRequest(
                        flag.getFlagCode(),
                        flag.isEnabled(),
                        ruleIds
                ));
            }

            cancelEdit();
            refresh();

        } catch (ApiClientException ex) {
            showErrorNotification(ex.getMessage());
            cancelEdit();
            refresh();
        }
    }

    @Override
    protected void delete(FeatureFlagResponse flag) {
        try {
            featureFlagsService.delete(flag.getId());
            adjustPageAfterDelete();
        } catch (ApiClientException ex) {
            showErrorNotification(ex.getMessage());
            refresh();
        }
    }
}
