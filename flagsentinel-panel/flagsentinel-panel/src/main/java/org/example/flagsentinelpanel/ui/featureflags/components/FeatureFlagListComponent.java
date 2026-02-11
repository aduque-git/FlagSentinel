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
import org.example.flagsentinelpanel.dto.CreateFeatureFlagRequest;
import org.example.flagsentinelpanel.dto.FeatureFlagResponse;
import org.example.flagsentinelpanel.dto.RuleResponse;
import org.example.flagsentinelpanel.dto.UpdateFeatureFlagRequest;
import org.example.flagsentinelpanel.ui.components.AbstractCrudGrid;
import org.example.flagsentinelpanel.ui.featureflags.service.FeatureFlagsService;
import org.example.flagsentinelpanel.ui.rules.service.RulesService;

import java.util.*;
import java.util.stream.Collectors;

public class FeatureFlagListComponent extends AbstractCrudGrid<FeatureFlagResponse> {

    private final FeatureFlagsService featureFlagsService;
    private final RulesService rulesService;

    private MultiSelectComboBox<RuleResponse> rulesField;

    public FeatureFlagListComponent(FeatureFlagsService featureFlagsService,
                                    RulesService rulesService) {

        super(FeatureFlagResponse.class, new Span("Feature Flags"));

        this.featureFlagsService = featureFlagsService;
        this.rulesService = rulesService;

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

        // ========= RULES (COLUMNA ÚNICA) =========
        List<RuleResponse> allRules = rulesService.findAll();

        rulesField = new MultiSelectComboBox<>();
        rulesField.setItems(allRules);
        rulesField.setItemLabelGenerator(RuleResponse::getValue);
        rulesField.addClassName("rules-multiselect");

        // Cada vez que se abre el editor, sincronizamos el valor correctamente
        grid.getEditor().addOpenListener(event -> {
            FeatureFlagResponse item = event.getItem();
            if (item != null) {
                if (item.getRules() == null) {
                    item.setRules(new ArrayList<>());
                }

                // 🔹 Mapear las reglas asignadas a los objetos exactos del ComboBox
                Set<RuleResponse> toSelect = item.getRules().stream()
                        .map(r -> allRules.stream()
                                .filter(a -> a.getId().equals(r.getId()))
                                .findFirst()
                                .orElse(null))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());

                rulesField.setValue(toSelect); // ahora los checks aparecen correctamente
            }
        });

        // ValueChangeListener que actualiza directamente el item actual
        rulesField.addValueChangeListener(event -> {
            FeatureFlagResponse item = grid.getEditor().getItem();
            if (item != null) {
                // Reemplazamos la lista con la selección actual; lista vacía si nada seleccionado
                item.setRules(event.getValue() != null ? new ArrayList<>(event.getValue()) : new ArrayList<>());
            }
        });

        // Columna única de Rules, sirve para mostrar y editar
        Grid.Column<FeatureFlagResponse> rulesColumn = grid.addColumn(flag ->
                        flag.getRules().stream()
                                .map(RuleResponse::getValue)
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

                    if (isEditing(flag)) {

                        Button save = new Button(new Icon(VaadinIcon.CHECK));
                        save.addClassName("inline-action");
                        save.addClickListener(e -> {
                            grid.getEditor().save();
                            save(flag);
                        });

                        Button cancel = new Button(new Icon(VaadinIcon.CLOSE));
                        cancel.addClassName("inline-action");
                        cancel.addClickListener(e -> cancelEdit());

                        actions.add(save, cancel);

                    } else {

                        Button edit = new Button(new Icon(VaadinIcon.EDIT));
                        edit.addClassName("inline-action");
                        edit.addClickListener(e -> startEdit(flag));

                        Button delete = new Button(new Icon(VaadinIcon.TRASH));
                        delete.addClassName("inline-action");
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
    protected List<FeatureFlagResponse> fetchAll() {
        // 🔹 Aseguramos que cada item tenga lista de reglas no nula
        List<FeatureFlagResponse> flags = featureFlagsService.findAll();
        flags.forEach(f -> {
            if (f.getRules() == null) {
                f.setRules(new ArrayList<>());
            }
        });
        return flags;
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

        // 🔹 Actualizamos las reglas del item desde el MultiSelectComboBox
        flag.setRules(new ArrayList<>(rulesField.getSelectedItems()));

        List<Long> ruleIds = flag.getRules().stream()
                .map(RuleResponse::getId)
                .collect(Collectors.toList());

        if (isCreating) {
            featureFlagsService.create(new CreateFeatureFlagRequest(
                    flag.getFlagCode(),
                    flag.isEnabled(),
                    ruleIds
            ));
        } else {
            featureFlagsService.update(flag.getId(), new UpdateFeatureFlagRequest(
                    flag.getFlagCode(),
                    flag.isEnabled(),
                    ruleIds
            ));
        }

        cancelEdit(); // cerramos editor
        refresh();    // refrescamos grid para mostrar botones edit/delete
    }

    @Override
    protected void delete(FeatureFlagResponse flag) {
        featureFlagsService.delete(flag.getId());
        refresh();
    }
}
