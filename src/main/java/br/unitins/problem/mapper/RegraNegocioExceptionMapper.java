package br.unitins.problem.mapper;

import java.net.URI;

import br.unitins.problem.ProblemDetails;
import br.unitins.problem.RegraNegocioException;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Mapeador global para RegraNegocioException.
 * Produz resposta HTTP no formato Problem Details RFC 9457 com media type application/problem+json.
 */
@Provider
@Priority(Priorities.USER - 100)
public class RegraNegocioExceptionMapper extends BaseExceptionMapper
        implements ExceptionMapper<RegraNegocioException> {

    public static final String TYPE_URI = "urn:problem-type:regra-negocio";
    public static final String TITLE = "Violação de Regra de Negócio";

    @Override
    public Response toResponse(RegraNegocioException exception) {
        String detail = (exception.getMessage() != null && !exception.getMessage().isBlank())
                ? exception.getMessage()
                : "A operação solicitada viola regras de negócio do sistema.";

        ProblemDetails problem = ProblemDetails.builder()
                .type(URI.create(TYPE_URI))
                .title(TITLE)
                .status(exception.getStatus().getStatusCode())
                .detail(detail)
                .instance(getRequestInstance())
                .build();

        return buildResponse(exception.getStatus(), problem);
    }
}
