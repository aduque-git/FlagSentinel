package org.example.flagsentinelpanel.service;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {

    private final AuthService authService;

    public SecurityService(AuthService authService) {
        this.authService = authService;
    }

    public boolean login(String username, String password) {
        return authService.login(username, password);
    }

    public String getToken() {
        return (String) VaadinSession.getCurrent().getAttribute("token");
    }

    public void handleSuccessfulLogin() {
        UI.getCurrent().navigate("main");
    }
}
