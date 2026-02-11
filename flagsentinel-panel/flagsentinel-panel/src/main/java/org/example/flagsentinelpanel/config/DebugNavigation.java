package org.example.flagsentinelpanel.config;

import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterListener;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class DebugNavigation implements BeforeEnterListener {

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        System.out.println("NAVEGANDO A: " + event.getNavigationTarget().getSimpleName());
        System.out.println("TOKEN EN SESION: " + VaadinSession.getCurrent().getAttribute("token"));
        System.out.println("AUTENTICACION: " + SecurityContextHolder.getContext().getAuthentication());
    }
}
