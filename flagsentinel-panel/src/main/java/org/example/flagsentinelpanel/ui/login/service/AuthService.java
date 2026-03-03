package org.example.flagsentinelpanel.ui.login.service;


import com.vaadin.flow.server.VaadinSession;
import org.example.flagsentinelpanel.dto.LoginRequest;
import org.example.flagsentinelpanel.dto.LoginResponse;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AuthService {

    private final RestTemplate rest = new RestTemplate();
    private final String LOGIN_URL = "http://localhost:8080/api/auth/login"; // tu API real

    public boolean login(String username, String password) {
        try {
            // Creamos el DTO EXACTO que la API espera
            LoginRequest body = new LoginRequest(username, password);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<LoginRequest> request = new HttpEntity<>(body, headers);

            ResponseEntity<LoginResponse> response = rest.exchange(
                    LOGIN_URL,
                    HttpMethod.POST,
                    request,
                    LoginResponse.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {

                String token = response.getBody().getToken();

                // Guardamos el token en la sesión Vaadin
                VaadinSession.getCurrent().setAttribute("token", token);
                System.out.println("TOKEN EN MAINVIEW = " + token);

                return true;
            }

        } catch (Exception e) {
            System.out.println("Error en login: " + e.getMessage());
            return false;
        }

        return false;
    }

}
