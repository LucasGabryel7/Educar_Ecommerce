package br.unitins.problem.mapper;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Mapeador global para outras exceções Jakarta REST (WebApplicationException).
 * Garante que status como 405 (Method Not Allowed), 415 (Unsupported Media Type), etc.
 * também sigam o padrão RFC 9457 com application/problem+json.
 */
@Provider
@Priority(Priorities.USER)
public class WebApplicationExceptionMapper extends BaseExceptionMapper
        implements ExceptionMapper<WebApplicationException> {

    @Override
    public Response toResponse(WebApplicationException exception) {
        int statusCode = exception.getResponse() != null
                ? exception.getResponse().getStatus()
                : Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();

        Response.Status statusEnum = Response.Status.fromStatusCode(statusCode);
        String title = statusEnum != null ? statusEnum.getReasonPhrase() : "Erro HTTP";

        String detail = (exception.getMessage() != null && !exception.getMessage().isBlank())
                ? exception.getMessage()
                : "Ocorreu um erro ao processar a requisição HTTP.";

        String typeUrn = "urn:problem-type:http-" + statusCode;

        return buildResponse(statusEnum != null ? statusEnum : Response.Status.INTERNAL_SERVER_ERROR,
                typeUrn, title, detail);
    }
}
