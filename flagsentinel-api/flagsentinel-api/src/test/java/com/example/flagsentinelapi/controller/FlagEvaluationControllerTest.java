package com.example.flagsentinelapi.controller;

import com.example.flagsentinelapi.dto.FlagEvaluationRequest;
import com.example.flagsentinelapi.dto.FlagEvaluationResponse;
import com.example.flagsentinelapi.service.FlagEvaluationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class FlagEvaluationControllerTest {

    @Mock
    private FlagEvaluationService service;

    @InjectMocks
    private FlagEvaluationController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    // ---------------------------------------------------------
    // EVALUATE FLAG
    // ---------------------------------------------------------
    @Test
    void evaluate_shouldReturnEvaluationResponse() throws Exception {
        // Creamos un objeto de respuesta simulado que el servicio debería devolver.
        // Este es el resultado esperado de la evaluación del flag.
        FlagEvaluationResponse response =
                new FlagEvaluationResponse("flag1", true, "Rules passed");

        // Configuramos el mock del servicio para que, cuando se llame a evaluate(...)
        // con cualquier argumento, devuelva el objeto 'response' definido arriba.
        when(service.evaluate(any())).thenReturn(response);
        // Ejecutamos una petición HTTP POST simulada contra el endpoint /api/flags/evaluate
        mockMvc.perform(post("/api/flags/evaluate")
                        // Indicamos que el cuerpo de la petición será JSON
                        .contentType(MediaType.APPLICATION_JSON)
                        // Este es el JSON que enviamos como body de la petición.
                        // Representa un FlagEvaluationRequest real.
                        .content("""
                                {
                                  "key": "flag1",
                                  "attributes": {
                                    "country": "ES",
                                    "age": "25"
                                  }
                                }
                                """))
                // Esperamos que la respuesta HTTP tenga código 200 OK
                .andExpect(status().isOk())
                // Validamos que el JSON devuelto contiene "key": "flag1"
                .andExpect(jsonPath("$.key").value("flag1"))
                // Validamos que "enabled": true
                .andExpect(jsonPath("$.enabled").value(true))
                // Validamos que "reason": "Rules passed"
                .andExpect(jsonPath("$.reason").value("Rules passed"));
        // Verificamos que el servicio fue llamado exactamente una vez
        // con cualquier FlagEvaluationRequest.
        verify(service).evaluate(any());
    }
}
