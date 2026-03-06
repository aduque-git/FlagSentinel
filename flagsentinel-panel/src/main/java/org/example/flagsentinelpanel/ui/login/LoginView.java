package org.example.flagsentinelpanel.ui.login;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import jakarta.annotation.security.PermitAll;
import org.example.flagsentinelpanel.service.SecurityService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import java.util.List;

@PermitAll
@Route("login")
public class LoginView extends VerticalLayout {

    private final SecurityService securityService;

    public LoginView(SecurityService securityService) {
        this.securityService = securityService;

        // ============================
        // Layout principal
        // ============================
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        // Fondo global igual al de la app
        String gradient = "linear-gradient(135deg, #0A0F1E 0%, #161B33 50%, #0C0F20 100%)";
        getStyle().set("background", gradient);

        // ============================
        // Card central (glassmorphism)
        // ============================
        VerticalLayout card = new VerticalLayout();
        card.setWidth("420px");
        card.setPadding(true);
        card.setSpacing(true);
        card.setAlignItems(Alignment.CENTER);
        card.getStyle()
                .set("background", "rgba(28, 30, 50, 0.95)")  // mismo que inputs
                .set("backdrop-filter", "blur(14px)")
                .set("border-radius", "16px")
                .set("padding", "40px")
                .set("border", "1px solid rgba(128, 0, 255, 0.8)")
                .set("box-shadow", "0 0 20px rgba(128, 0, 255, 0.25)");

        // ============================
        // Título y subtítulo
        // ============================
        H1 title = new H1("FlagSentinel");
        title.getStyle()
                .set("color", "#FFFFFF")
                .set("font-size", "2rem")
                .set("margin-bottom", "4px");

        Span subtitle = new Span("Acceso al panel de administración");
        subtitle.getStyle()
                .set("color", "#A3B1D1")  // mismo que labels de inputs
                .set("font-size", "0.95rem")
                .set("margin-bottom", "22px");

        // ============================
        // LoginForm
        // ============================
        LoginForm loginForm = new LoginForm();
        loginForm.setForgotPasswordButtonVisible(false);
        loginForm.getStyle().set("width", "100%");

        // Ajustamos el LoginForm para que tenga fondo del card
        loginForm.getElement().executeJs("""
            const host = this;
            host.style.background = "rgba(28, 30, 50, 0.95)";
            host.style.borderRadius = "12px";
            host.style.padding = "20px";
            host.style.color = "#FFFFFF";
        """);

        // ============================
        // LOGIN REAL
        // ============================
        loginForm.addLoginListener(e -> {
            UI ui = UI.getCurrent();
            ui.access(() -> {
                boolean ok = securityService.login(e.getUsername(), e.getPassword());
                if (ok) {
                    String token = VaadinSession.getCurrent().getAttribute("token").toString();
                    Authentication auth = new UsernamePasswordAuthenticationToken(
                            e.getUsername(),
                            token,
                            List.of(new SimpleGrantedAuthority("ROLE_USER"))
                    );

                    SecurityContextHolder.getContext().setAuthentication(auth);
                    VaadinSession.getCurrent().getSession().setAttribute(
                            HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                            SecurityContextHolder.getContext()
                    );

                    ui.navigate("main");
                } else {
                    loginForm.setError(true);
                }
            });
        });

        // ============================
        // Añadimos al layout
        // ============================
        card.add(title, subtitle, loginForm);
        add(card);
    }
}
