package br.unitins.problem.mapper;

import java.net.URI;

import br.unitins.problem.ProblemDetails;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.StatusType;
import jakarta.ws.rs.core.UriInfo;

/**
 * Classe base abstrata para ExceptionMappers, centralizando a construção de respostas RFC 9457.
 */
public abstract class BaseExceptionMapper {

    @Context
    protected UriInfo uriInfo;

    /**
     * Obtém o caminho relativo do recurso que originou a requisição.
     */
    protected URI getRequestInstance() {
        if (uriInfo != null && uriInfo.getRequestUri() != null) {
            String path = uriInfo.getRequestUri().getRawPath();
            if (path != null && !path.isBlank()) {
                return URI.create(path);
            }
        }
        return null;
    }

    /**
     * Constrói uma resposta padronizada com Content-Type application/problem+json.
     */
    protected Response buildResponse(StatusType status, String typeUrn, String title, String detail) {
        ProblemDetails problem = ProblemDetails.builder()
                .type(typeUrn != null ? URI.create(typeUrn) : URI.create("about:blank"))
                .title(title)
                .status(status.getStatusCode())
                .detail(detail)
                .instance(getRequestInstance())
                .build();

        return buildResponse(status, problem);
    }

    /**
     * Constrói uma resposta com a entidade ProblemDetails especificada e media type application/problem+json.
     */
    protected Response buildResponse(StatusType status, ProblemDetails problem) {
        return Response.status(status)
                .type(ProblemDetails.MEDIA_TYPE)
                .entity(problem)
                .build();
    }
}
