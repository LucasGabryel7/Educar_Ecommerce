package br.unitins.problem.mapper;

import jakarta.annotation.Priority;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Mapeador global para NotFoundException (HTTP 404).
 * Produz resposta no formato Problem Details RFC 9457 com media type application/problem+json.
 */
@Provider
@Priority(Priorities.USER - 100)
public class NotFoundExceptionMapper extends BaseExceptionMapper
        implements ExceptionMapper<NotFoundException> {

    public static final String TYPE_URI = "urn:problem-type:not-found";
    public static final String TITLE = "Recurso Não Encontrado";

    @Override
    public Response toResponse(NotFoundException exception) {
        String detail = (exception.getMessage() != null && !exception.getMessage().isBlank())
                ? exception.getMessage()
                : "O recurso solicitado não foi encontrado no servidor.";

        return buildResponse(Response.Status.NOT_FOUND, TYPE_URI, TITLE, detail);
    }
}
