package org.example.flagsentinelpanel.ui.users.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import org.example.flagsentinelpanel.dto.*;
import org.example.flagsentinelpanel.exceptions.ApiClientException;
import org.example.flagsentinelpanel.ui.components.AbstractCrudGrid;
import org.example.flagsentinelpanel.ui.components.PaginationProperties;
import org.example.flagsentinelpanel.ui.users.service.UsersService;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class UserListComponent extends AbstractCrudGrid<UserResponse> {

    private final UsersService usersService;
    private final PaginationProperties pagination;

    public UserListComponent(UsersService usersService, PaginationProperties pagination) {
        super(UserResponse.class, new Span("Users"));
        this.usersService = usersService;
        this.pagination = pagination;

        init();
    }

    @Override
    protected void configureColumns() {

        Binder<UserResponse> binder = new Binder<>(UserResponse.class);
        grid.getEditor().setBinder(binder);

        // USERNAME
        TextField usernameField = new TextField();
        binder.forField(usernameField).bind("username");

        Grid.Column<UserResponse> colUsername = grid
                .addColumn(UserResponse::getUsername)
                .setHeader("Username")
                .setAutoWidth(true)
                .setFlexGrow(1);

        colUsername.setEditorComponent(usernameField);

        // ROLE
        ComboBox<Role> roleField = new ComboBox<>();
        roleField.setItems(Role.values());
        roleField.setItemLabelGenerator(Enum::name);
        roleField.addClassName("user-role-combo");
        roleField.setAllowCustomValue(false);
        roleField.getElement().executeJs(
                "this.shadowRoot.querySelector('input').setAttribute('readonly', true);"
        );

        binder.forField(roleField)
                .withConverter(
                        role -> role == null ? null : role.name(),
                        str -> str == null ? null : Role.valueOf(str)
                )
                .bind("role");

        Grid.Column<UserResponse> colRole = grid
                .addColumn(UserResponse::getRole)
                .setHeader("Role")
                .setAutoWidth(true)
                .setFlexGrow(1);

        colRole.setEditorComponent(roleField);

        // ACTIONS
        grid.addComponentColumn(user -> {

                    HorizontalLayout actions = new HorizontalLayout();
                    actions.addClassName("no-hover");

                    rowButtons.computeIfAbsent(user, k -> new ArrayList<>());

                    if (isEditing(user)) {

                        Button save = new Button(new Icon(VaadinIcon.CHECK));
                        save.addClassName("inline-action");
                        rowButtons.get(user).add(save);
                        save.addClickListener(e -> {
                            grid.getEditor().save();
                            save(user);
                        });

                        Button cancel = new Button(new Icon(VaadinIcon.CLOSE));
                        cancel.addClassName("inline-action");
                        rowButtons.get(user).add(cancel);
                        cancel.addClickListener(e -> cancelEdit());

                        actions.add(save, cancel);

                    } else {

                        Button edit = new Button(new Icon(VaadinIcon.EDIT));
                        edit.addClassName("inline-action");
                        rowButtons.get(user).add(edit);
                        edit.addClickListener(e -> startEdit(user));

                        Button delete = new Button(new Icon(VaadinIcon.TRASH));
                        delete.addClassName("inline-action");
                        rowButtons.get(user).add(delete);
                        delete.addClickListener(e -> delete(user));

                        actions.add(edit, delete);
                    }

                    return actions;

                })
                .setHeader("")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setClassNameGenerator(u -> "no-hover");
    }


    @Override
    protected PageResponse<UserResponse> fetchPage(int apiPage, int pageSize) {
        try {
            return usersService.findPaged(apiPage, pageSize);
        } catch (ApiClientException ex) {
            showErrorNotification(ex.getMessage());
            return new PageResponse<>(List.of(), 0, 0, pageSize, apiPage);
        }
    }


    @Override
    protected UserResponse createEmpty() {
        return new UserResponse(null, "", "USER");
    }

    @Override
    protected void save(UserResponse user) {

        String username = user.getUsername();
        String role = user.getRole();

        boolean isCreating = (creatingItem == user);

        if (isNullOrBlank(username) || isNullOrBlank(role)) {
            showErrorNotification("No puedes crear un usuario vacío");
            cancelEdit();
            return;
        }

        try {
            if (isCreating) {

                openPasswordDialog(password -> {
                    CreateUserRequest dto = new CreateUserRequest(
                            username,
                            password,
                            role
                    );
                    usersService.create(dto);

                    cancelEdit();
                    goToPageOfNewElement();
                });

            } else {

                UpdateUserRequest dto = new UpdateUserRequest(
                        username,
                        role
                );

                usersService.update(user.getId(), dto);
                refresh();
                cancelEdit();

            }

        } catch (ApiClientException ex) {
            showErrorNotification(ex.getMessage());
            refresh();
            cancelEdit();
        }
    }

    @Override
    protected void delete(UserResponse user) {
        try {
            usersService.delete(user.getId());
            adjustPageAfterDelete();
        } catch (ApiClientException ex) {
            showErrorNotification(ex.getMessage());
            refresh();
            cancelEdit();
        }
    }

    private void openPasswordDialog(Consumer<String> onPasswordEntered) {

        Dialog dialog = new Dialog();
        dialog.setCloseOnEsc(true);
        dialog.setCloseOnOutsideClick(false);
        dialog.addThemeName("password-dialog");

        PasswordField pwd = new PasswordField("Password");
        pwd.setWidthFull();
        pwd.getStyle().set("color", "white");

        Button ok = new Button("Aceptar", e -> {
            if (pwd.getValue() == null || pwd.getValue().isBlank()) {
                pwd.setInvalid(true);
                pwd.setErrorMessage("La contraseña no puede estar vacía");
                return;
            }
            dialog.close();
            onPasswordEntered.accept(pwd.getValue());
        });

        Button cancel = new Button("Cancelar", e -> dialog.close());

        HorizontalLayout buttons = new HorizontalLayout(ok, cancel);
        buttons.setJustifyContentMode(JustifyContentMode.END);

        VerticalLayout layout = new VerticalLayout(pwd, buttons);
        layout.setPadding(false);
        layout.setSpacing(true);

        dialog.add(layout);
        dialog.open();
    }
}
