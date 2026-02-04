package org.example.flagsentinelpanel.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.example.flagsentinelpanel.service.SecurityService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

@PermitAll
@Route("login")
public class LoginView extends VerticalLayout {

    private final SecurityService securityService;

    public LoginView(SecurityService securityService) {
        this.securityService = securityService;

        // Pantalla completa, centrada
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        // Fondo degradado azul–negro
        String gradient = "linear-gradient(135deg, #050816 0%, #07111F 35%, #001F3F 100%)";
        getStyle().set("background", gradient);

        // Card central tipo “glassmorphism”
        VerticalLayout card = new VerticalLayout();
        card.setWidth("420px");
        card.setPadding(true);
        card.setSpacing(true);
        card.setAlignItems(Alignment.CENTER);
        card.getStyle()
                .set("background", "rgba(10, 14, 25, 0.9)")
                .set("backdrop-filter", "blur(14px)")
                .set("border-radius", "18px")
                .set("padding", "40px")
                .set("border", "1px solid rgba(0, 122, 255, 0.5)")
                .set("box-shadow", "0 0 30px rgba(0, 122, 255, 0.35)");

        H1 title = new H1("FlagSentinel");
        title.getStyle()
                .set("color", "#E5ECF5")
                .set("font-size", "2.3rem")
                .set("margin-bottom", "4px");

        Span subtitle = new Span("Acceso al panel de administración");
        subtitle.getStyle()
                .set("color", "#8FA3BF")
                .set("font-size", "0.95rem")
                .set("margin-bottom", "22px");

        LoginForm loginForm = new LoginForm();
        loginForm.setForgotPasswordButtonVisible(false);
        loginForm.getStyle().set("width", "100%");

        // Fondo del LoginForm igual que el fondo general
        loginForm.getElement().executeJs("""
                    const host = this;
                    host.style.background = arguments[0];
                    host.style.borderRadius = "12px";
                    host.style.padding = "20px";
                """, gradient);


        // LOGIN REAL
        loginForm.addLoginListener(e -> {
            try {
                boolean ok = securityService.login(e.getUsername(), e.getPassword());

                if (ok) {
                    // Recuperamos el token guardado en VaadinSession
                    String token = securityService.getToken();

                    // Creamos la autenticación para Spring Security
                    var auth = new UsernamePasswordAuthenticationToken(
                            e.getUsername(),
                            token,
                            List.of(new SimpleGrantedAuthority("ROLE_USER"))
                    );

                    SecurityContextHolder.getContext().setAuthentication(auth);

                    // Navegamos a la vista principal
                    UI.getCurrent().navigate("main");

                } else {
                    loginForm.setError(true);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                loginForm.setError(true);
            }
        });

        card.add(title, subtitle, loginForm);
        add(card);
    }
}
