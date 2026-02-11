package org.example.flagsentinelpanel.security;

import com.vaadin.flow.spring.security.VaadinWebSecurity;
import org.example.flagsentinelpanel.ui.login.LoginView;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends VaadinWebSecurity {

    @Override
    public void configure(HttpSecurity http) throws Exception {
        System.out.println(">>> SECURITY CONFIG ACTIVE <<<");

        http.csrf(AbstractHttpConfigurer::disable);
        super.configure(http);
        setLoginView(http, LoginView.class);
    }
}
