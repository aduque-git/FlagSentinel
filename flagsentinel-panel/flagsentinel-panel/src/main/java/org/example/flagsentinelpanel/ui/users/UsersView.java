package org.example.flagsentinelpanel.ui.users;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.example.flagsentinelpanel.ui.components.PaginationProperties;
import org.example.flagsentinelpanel.ui.main.MainLayout;
import org.example.flagsentinelpanel.ui.users.components.UserListComponent;
import org.example.flagsentinelpanel.ui.users.service.UsersService;

@PermitAll
@Route(value = "users", layout = MainLayout.class)
@PageTitle("Users | FlagSentinel")
public class UsersView extends HorizontalLayout {

    private final UserListComponent list;

    public UsersView(UsersService usersService, PaginationProperties pagination) {

        setSizeFull();
        setPadding(true);
        setSpacing(true);
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        list = new UserListComponent(usersService, pagination);

        add(list);
    }
}
