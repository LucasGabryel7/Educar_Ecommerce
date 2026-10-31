package br.unitins.problem.mapper;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import br.unitins.problem.ProblemDetails;
import br.unitins.problem.Violation;
import jakarta.annotation.Priority;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Mapeador global para ConstraintViolationException (erros de validação do Hibernate Validator).
 * Produz resposta HTTP 400 Bad Request no formato Problem Details RFC 9457 com a extensão 'violations'.
 */
@Provider
@Priority(Priorities.USER - 100)
public class ConstraintViolationExceptionMapper extends BaseExceptionMapper
        implements ExceptionMapper<ConstraintViolationException> {

    public static final String TYPE_URI = "urn:problem-type:validation-error";
    public static final String TITLE = "Erro de Validação";

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        List<Violation> violations = new ArrayList<>();
        if (exception != null && exception.getConstraintViolations() != null) {
            for (ConstraintViolation<?> cv : exception.getConstraintViolations()) {
                if (cv != null) {
                    String fieldName = extractFieldName(cv.getPropertyPath());
                    String message = cv.getMessage() != null ? cv.getMessage() : "";
                    violations.add(new Violation(fieldName, message));
                }
            }
        }
        violations.sort((v1, v2) -> {
            String f1 = (v1 != null && v1.field() != null) ? v1.field() : "";
            String f2 = (v2 != null && v2.field() != null) ? v2.field() : "";
            return f1.compareToIgnoreCase(f2);
        });

        ProblemDetails problem = ProblemDetails.builder()
                .type(URI.create(TYPE_URI))
                .title(TITLE)
                .status(Response.Status.BAD_REQUEST.getStatusCode())
                .detail("Um ou mais campos contêm valores inválidos ou não atendem às restrições.")
                .instance(getRequestInstance())
                .violations(violations)
                .build();

        return buildResponse(Response.Status.BAD_REQUEST, problem);
    }

    private String extractFieldName(Path propertyPath) {
        if (propertyPath == null) {
            return "desconhecido";
        }
        String field = null;
        for (Path.Node node : propertyPath) {
            if (node != null) {
                field = node.getName();
            }
        }
        return (field != null && !field.isBlank()) ? field : propertyPath.toString();
    }
}
