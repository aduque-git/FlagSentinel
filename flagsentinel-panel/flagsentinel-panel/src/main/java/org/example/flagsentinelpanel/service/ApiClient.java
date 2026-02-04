package org.example.flagsentinelpanel.service;

import com.vaadin.flow.server.VaadinSession;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ApiClient {

    private final RestTemplate rest = new RestTemplate();

    private String getToken() {
        return (String) VaadinSession.getCurrent().getAttribute("token");
    }

    public ResponseEntity<String> getProtectedData() {
        String token = getToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        return rest.exchange(
                "http://localhost:8081/protected/test", // tu endpoint protegido
                HttpMethod.GET,
                request,
                String.class
        );
    }
}
