package org.example.flagsentinelpanel.ui.rules.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import org.example.flagsentinelpanel.dto.CreateRuleRequest;
import org.example.flagsentinelpanel.dto.RuleResponse;
import org.example.flagsentinelpanel.dto.UpdateRuleRequest;
import org.example.flagsentinelpanel.ui.components.AbstractCrudGrid;
import org.example.flagsentinelpanel.ui.rules.service.RulesService;

import java.util.List;

public class RulesListComponent extends AbstractCrudGrid<RuleResponse> {

    private final RulesService rulesService;

    public RulesListComponent(RulesService rulesService) {
        super(RuleResponse.class, new Span("Rules"));
        this.rulesService = rulesService;
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
        TextField opField = new TextField();
        binder.forField(opField).bind("operator");

        Grid.Column<RuleResponse> colOp = grid
                .addColumn(RuleResponse::getOperator)
                .setHeader("Operator")
                .setAutoWidth(true)
                .setFlexGrow(1);

        colOp.setEditorComponent(opField);

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
                    actions.addClassName("no-hover"); // 👈 CLAVE

                    if (isEditing(rule)) {
                        Button save = new Button(new Icon(VaadinIcon.CHECK));
                        save.addClassName("inline-action");
                        save.addClickListener(e -> {
                            grid.getEditor().save();
                            save(rule);
                        });

                        Button cancel = new Button(new Icon(VaadinIcon.CLOSE));
                        cancel.addClassName("inline-action");
                        cancel.addClickListener(e -> cancelEdit());

                        actions.add(save, cancel);

                    } else {
                        Button edit = new Button(new Icon(VaadinIcon.EDIT));
                        edit.addClassName("inline-action");
                        edit.addClickListener(e -> startEdit(rule));

                        Button delete = new Button(new Icon(VaadinIcon.TRASH));
                        delete.addClassName("inline-action");
                        delete.addClickListener(e -> delete(rule));

                        actions.add(edit, delete);
                    }

                    return actions;

                })
                .setHeader("")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setClassNameGenerator(user -> "no-hover"); // 👈 MUY IMPORTANTE
    }


    @Override
    protected List<RuleResponse> fetchAll() {
        return rulesService.findAll();
    }

    @Override
    protected void save(RuleResponse rule) {

        if (isNullOrBlank(rule.getAttribute()) ||
                isNullOrBlank(rule.getOperator()) ||
                isNullOrBlank(rule.getValue())) {

            showErrorNotification("No puedes crear una regla vacía");
            cancelEdit();
            return;
        }


        if (creatingItem == rule) {
            CreateRuleRequest dto = new CreateRuleRequest();
            dto.setAttribute(rule.getAttribute());
            dto.setOperator(rule.getOperator());
            dto.setValue(rule.getValue());
            rulesService.create(dto);
        } else {
            UpdateRuleRequest dto = new UpdateRuleRequest();
            dto.setAttribute(rule.getAttribute());
            dto.setOperator(rule.getOperator());
            dto.setValue(rule.getValue());
            rulesService.update(rule.getId(), dto);
        }

        cancelEdit();
    }

    @Override
    protected void delete(RuleResponse rule) {
        rulesService.delete(rule.getId());
        refresh();
    }

    @Override
    protected RuleResponse createEmpty() {
        return new RuleResponse();
    }
}
