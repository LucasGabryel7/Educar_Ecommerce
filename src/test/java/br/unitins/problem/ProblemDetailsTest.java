package br.unitins.problem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ProblemDetailsTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testProblemDetailsSerialization() throws Exception {
        ProblemDetails problem = ProblemDetails.builder()
                .type("urn:problem-type:not-found")
                .title("Recurso Não Encontrado")
                .status(404)
                .detail("Aula prática com ID 99 não encontrada.")
                .instance("/aulas-praticas/99")
                .build();

        String json = objectMapper.writeValueAsString(problem);

        assertNotNull(json);
        assertTrue(json.contains("\"type\":\"urn:problem-type:not-found\""));
        assertTrue(json.contains("\"title\":\"Recurso Não Encontrado\""));
        assertTrue(json.contains("\"status\":404"));
        assertTrue(json.contains("\"detail\":\"Aula prática com ID 99 não encontrada.\""));
        assertTrue(json.contains("\"instance\":\"/aulas-praticas/99\""));
        assertFalse(json.contains("violations")); // Violations must not be serialized when empty/null
    }

    @Test
    public void testProblemDetailsWithViolationsSerialization() throws Exception {
        ProblemDetails problem = ProblemDetails.builder()
                .type(URI.create("urn:problem-type:validation-error"))
                .title("Erro de Validação")
                .status(400)
                .detail("Um ou mais campos contêm valores inválidos.")
                .instance(URI.create("/aulas-praticas"))
                .addViolation("nome", "O nome é obrigatório.")
                .addViolation("preco", "O preço deve ser maior que zero.")
                .build();

        String json = objectMapper.writeValueAsString(problem);

        assertNotNull(json);
        assertTrue(json.contains("\"type\":\"urn:problem-type:validation-error\""));
        assertTrue(json.contains("\"violations\":["));
        assertTrue(json.contains("\"field\":\"nome\""));
        assertTrue(json.contains("\"message\":\"O nome é obrigatório.\""));
        assertTrue(json.contains("\"field\":\"preco\""));
        assertNotNull(problem.getViolations());
        assertEquals(2, problem.getViolations().size());
    }

    @Test
    public void testDefaultTypeIsAboutBlank() {
        ProblemDetails problem = ProblemDetails.builder()
                .title("Erro")
                .status(500)
                .build();

        assertEquals(URI.create("about:blank"), problem.getType());
        assertEquals("Erro", problem.getTitle());
        assertEquals(500, problem.getStatus());
    }
}
