package org.example.flagsentinelpanel.ui.rules.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
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
import org.example.flagsentinelpanel.ui.rules.service.OperatorService;
import org.example.flagsentinelpanel.ui.rules.service.RulesService;

import java.util.ArrayList;
import java.util.List;

public class RulesListComponent extends AbstractCrudGrid<RuleResponse> {

    private final RulesService rulesService;
    private final OperatorService operatorService;
    private final PaginationProperties pagination;

    public RulesListComponent(RulesService rulesService,
                              OperatorService operatorService,
                              PaginationProperties pagination) {

        super(RuleResponse.class, new Span("Rules"));
        this.rulesService = rulesService;
        this.operatorService = operatorService;
        this.pagination = pagination;

        // IMPORTANTE: NO USAR DataProvider
        // El AbstractCrudGrid ya controla la paginación híbrida
        init();
    }

    @Override
    protected void configureColumns() {

        Binder<RuleResponse> binder = new Binder<>(RuleResponse.class);
        grid.getEditor().setBinder(binder);

        // ATTRIBUTE
        TextField attrField = new TextField();
        binder.forField(attrField).bind("attribute");

        Grid.Column<RuleResponse> colAttr = grid
                .addColumn(RuleResponse::getAttribute)
                .setHeader("Attribute")
                .setAutoWidth(true)
                .setFlexGrow(1);
        colAttr.setEditorComponent(attrField);

        // OPERATOR
        ComboBox<OperatorOption> operatorCombo = new ComboBox<>();
        List<OperatorOption> operators = operatorService.findAll();

        operatorCombo.setItems(operators);
        operatorCombo.setItemLabelGenerator(OperatorOption::getLabel);

        binder.forField(operatorCombo)
                .withConverter(
                        op -> op != null ? op.getCode() : null,
                        code -> operators.stream()
                                .filter(o -> o.getCode().equals(code))
                                .findFirst()
                                .orElse(null)
                )
                .bind(RuleResponse::getOperator, RuleResponse::setOperator);

        Grid.Column<RuleResponse> colOp = grid
                .addColumn(rule -> operators.stream()
                        .filter(o -> o.getCode().equals(rule.getOperator()))
                        .map(OperatorOption::getLabel)
                        .findFirst()
                        .orElse(rule.getOperator())
                )
                .setHeader("Operator")
                .setAutoWidth(true)
                .setFlexGrow(1);

        colOp.setEditorComponent(operatorCombo);


        // VALUE
        TextField valField = new TextField();
        binder.forField(valField).bind("value");

        Grid.Column<RuleResponse> colVal = grid
                .addColumn(RuleResponse::getValue)
                .setHeader("Value")
                .setAutoWidth(true)
                .setFlexGrow(1);
        colVal.setEditorComponent(valField);

        // ACTIONS
        grid.addComponentColumn(rule -> {

                    HorizontalLayout actions = new HorizontalLayout();
                    actions.addClassName("no-hover");

                    rowButtons.computeIfAbsent(rule, k -> new ArrayList<>());

                    if (isEditing(rule)) {

                        Button save = new Button(new Icon(VaadinIcon.CHECK));
                        save.addClassName("inline-action");
                        rowButtons.get(rule).add(save);
                        save.addClickListener(e -> {
                            grid.getEditor().save();
                            save(rule);
                        });

                        Button cancel = new Button(new Icon(VaadinIcon.CLOSE));
                        cancel.addClassName("inline-action");
                        rowButtons.get(rule).add(cancel);
                        cancel.addClickListener(e -> cancelEdit());

                        actions.add(save, cancel);

                    } else {

                        Button edit = new Button(new Icon(VaadinIcon.EDIT));
                        edit.addClassName("inline-action");
                        rowButtons.get(rule).add(edit);
                        edit.addClickListener(e -> startEdit(rule));

                        Button delete = new Button(new Icon(VaadinIcon.TRASH));
                        delete.addClassName("inline-action");
                        rowButtons.get(rule).add(delete);
                        delete.addClickListener(e -> delete(rule));

                        actions.add(edit, delete);
                    }

                    return actions;

                })
                .setHeader("")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setClassNameGenerator(r -> "no-hover");
    }

    @Override
    protected PageResponse<RuleResponse> fetchPage(int apiPage, int pageSize) {
        try {
            return rulesService.findPaged(apiPage, pageSize);
        } catch (ApiClientException ex) {
            showErrorNotification(ex.getMessage());
            return new PageResponse<>(List.of(), 0, 0, pageSize, apiPage);
        }
    }


    @Override
    protected void save(RuleResponse rule) {

        boolean isCreating = (creatingItem == rule);

        if (isNullOrBlank(rule.getAttribute()) ||
                rule.getOperator() == null ||
                rule.getOperator().isBlank() ||
                isNullOrBlank(rule.getValue())) {

            showErrorNotification("No puedes crear una regla vacía");
            cancelEdit();
            return;
        }

        try {
            if (isCreating) {

                CreateRuleRequest dto = new CreateRuleRequest();
                dto.setAttribute(rule.getAttribute());
                dto.setOperator(rule.getOperator());
                dto.setValue(rule.getValue());

                rulesService.create(dto);

                cancelEdit();
                goToPageOfNewElement();
                return;

            } else {

                UpdateRuleRequest dto = new UpdateRuleRequest();
                dto.setAttribute(rule.getAttribute());
                dto.setOperator(rule.getOperator());
                dto.setValue(rule.getValue());

                rulesService.update(rule.getId(), dto);
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
    protected void delete(RuleResponse rule) {
        try {
            rulesService.delete(rule.getId());
            adjustPageAfterDelete();
        } catch (ApiClientException ex) {
            showErrorNotification(ex.getMessage());
            refresh();
        }
    }

    @Override
    protected RuleResponse createEmpty() {
        return new RuleResponse();
    }
}
