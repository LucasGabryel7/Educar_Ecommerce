package br.unitins.problem.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.net.URI;
import java.util.Set;

import org.junit.jupiter.api.Test;

import br.unitins.problem.ProblemDetails;
import br.unitins.problem.RegraNegocioException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.NotBlank;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;

public class ExceptionMapperTest {

    private record TestBean(
            @NotBlank(message = "O nome é obrigatório.") String nome) {
    }

    @Test
    public void testNotFoundExceptionMapper() {
        NotFoundExceptionMapper mapper = new NotFoundExceptionMapper();
        NotFoundException exception = new NotFoundException("Aula teórica não encontrada.");

        Response response = mapper.toResponse(exception);

        assertEquals(404, response.getStatus());
        assertEquals("application/problem+json", response.getMediaType().toString());

        ProblemDetails entity = (ProblemDetails) response.getEntity();
        assertNotNull(entity);
        assertEquals(404, entity.getStatus());
        assertEquals(NotFoundExceptionMapper.TITLE, entity.getTitle());
        assertEquals("Aula teórica não encontrada.", entity.getDetail());
        assertEquals(URI.create(NotFoundExceptionMapper.TYPE_URI), entity.getType());
        assertNull(entity.getViolations());
    }

    @Test
    public void testBadRequestExceptionMapper() {
        BadRequestExceptionMapper mapper = new BadRequestExceptionMapper();
        BadRequestException exception = new BadRequestException("Parâmetro de consulta inválido.");

        Response response = mapper.toResponse(exception);

        assertEquals(400, response.getStatus());
        assertEquals("application/problem+json", response.getMediaType().toString());

        ProblemDetails entity = (ProblemDetails) response.getEntity();
        assertNotNull(entity);
        assertEquals(400, entity.getStatus());
        assertEquals(BadRequestExceptionMapper.TITLE, entity.getTitle());
        assertEquals("Parâmetro de consulta inválido.", entity.getDetail());
        assertEquals(URI.create(BadRequestExceptionMapper.TYPE_URI), entity.getType());
    }

    @Test
    public void testRegraNegocioExceptionMapper() {
        RegraNegocioExceptionMapper mapper = new RegraNegocioExceptionMapper();
        RegraNegocioException exception = new RegraNegocioException("Conflito de horário do instrutor.");

        Response response = mapper.toResponse(exception);

        assertEquals(400, response.getStatus());
        assertEquals("application/problem+json", response.getMediaType().toString());

        ProblemDetails entity = (ProblemDetails) response.getEntity();
        assertNotNull(entity);
        assertEquals(400, entity.getStatus());
        assertEquals(RegraNegocioExceptionMapper.TITLE, entity.getTitle());
        assertEquals("Conflito de horário do instrutor.", entity.getDetail());
        assertEquals(URI.create(RegraNegocioExceptionMapper.TYPE_URI), entity.getType());
    }

    @Test
    public void testConstraintViolationExceptionMapper() {
        ConstraintViolationExceptionMapper mapper = new ConstraintViolationExceptionMapper();

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<TestBean>> violations = validator.validate(new TestBean(null));

        ConstraintViolationException exception = new ConstraintViolationException("Validation failed", violations);

        Response response = mapper.toResponse(exception);

        assertEquals(400, response.getStatus());
        assertEquals("application/problem+json", response.getMediaType().toString());

        ProblemDetails entity = (ProblemDetails) response.getEntity();
        assertNotNull(entity);
        assertEquals(400, entity.getStatus());
        assertEquals(ConstraintViolationExceptionMapper.TITLE, entity.getTitle());
        assertNotNull(entity.getViolations());
        assertEquals(1, entity.getViolations().size());
        assertEquals("nome", entity.getViolations().get(0).field());
        assertEquals("O nome é obrigatório.", entity.getViolations().get(0).message());
    }

    @Test
    public void testWebApplicationExceptionMapper() {
        WebApplicationExceptionMapper mapper = new WebApplicationExceptionMapper();
        Response originalResponse = Response.status(405).build();
        jakarta.ws.rs.WebApplicationException exception = new jakarta.ws.rs.WebApplicationException(
                "Método não permitido", originalResponse);

        Response response = mapper.toResponse(exception);

        assertEquals(405, response.getStatus());
        assertEquals("application/problem+json", response.getMediaType().toString());

        ProblemDetails entity = (ProblemDetails) response.getEntity();
        assertNotNull(entity);
        assertEquals(405, entity.getStatus());
        assertEquals(URI.create("urn:problem-type:http-405"), entity.getType());
    }

    @Test
    public void testGeneralExceptionMapper() {
        GeneralExceptionMapper mapper = new GeneralExceptionMapper();
        RuntimeException exception = new RuntimeException("NullPointerException simulado");

        Response response = mapper.toResponse(exception);

        assertEquals(500, response.getStatus());
        assertEquals("application/problem+json", response.getMediaType().toString());

        ProblemDetails entity = (ProblemDetails) response.getEntity();
        assertNotNull(entity);
        assertEquals(500, entity.getStatus());
        assertEquals(GeneralExceptionMapper.TITLE, entity.getTitle());
        assertEquals(URI.create(GeneralExceptionMapper.TYPE_URI), entity.getType());
    }
}
