package org.example.flagsentinelpanel.security;

import com.vaadin.flow.spring.security.VaadinWebSecurity;
import org.example.flagsentinelpanel.views.LoginView;
import org.springframework.context.annotation.Bean;
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


    private void configureVaadin(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/VAADIN/**", "/HEARTBEAT/**", "/UIDL/**", "/resources/**",
                        "/webjars/**", "/frontend/**", "/frontend-es5/**", "/frontend-es6/**",
                        "/vaadinServlet/**", "/connect/**", "/manifest.webmanifest", "/sw.js",
                        "/offline.html", "/icons/**", "/images/**", "/styles/**")
                .permitAll()
        );
    }
}
