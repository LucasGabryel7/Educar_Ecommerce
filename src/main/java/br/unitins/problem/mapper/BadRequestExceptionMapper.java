package br.unitins.problem.mapper;

import jakarta.annotation.Priority;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Mapeador global para BadRequestException (HTTP 400).
 * Produz resposta no formato Problem Details RFC 9457 com media type application/problem+json.
 */
@Provider
@Priority(Priorities.USER - 100)
public class BadRequestExceptionMapper extends BaseExceptionMapper
        implements ExceptionMapper<BadRequestException> {

    public static final String TYPE_URI = "urn:problem-type:bad-request";
    public static final String TITLE = "Requisição Inválida";

    @Override
    public Response toResponse(BadRequestException exception) {
        String detail = (exception.getMessage() != null && !exception.getMessage().isBlank())
                ? exception.getMessage()
                : "A requisição enviada é inválida ou contém sintaxe incorreta.";

        return buildResponse(Response.Status.BAD_REQUEST, TYPE_URI, TITLE, detail);
    }
}
