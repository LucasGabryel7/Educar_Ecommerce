package br.unitins.problem.mapper;

import org.jboss.logging.Logger;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Mapeador global de fallback para qualquer exceção não tratada (Throwable).
 * Evita vazamento de stacktrace para o cliente e padroniza o erro 500 em application/problem+json.
 */
@Provider
@Priority(Priorities.USER + 100)
public class GeneralExceptionMapper extends BaseExceptionMapper
        implements ExceptionMapper<Throwable> {

    private static final Logger LOG = Logger.getLogger(GeneralExceptionMapper.class);

    public static final String TYPE_URI = "urn:problem-type:internal-server-error";
    public static final String TITLE = "Erro Interno do Servidor";

    @Override
    public Response toResponse(Throwable exception) {
        LOG.error("Erro interno não tratado no servidor", exception);

        String detail = "Ocorreu um erro interno inesperado no servidor. Por favor, tente novamente mais tarde.";

        return buildResponse(Response.Status.INTERNAL_SERVER_ERROR, TYPE_URI, TITLE, detail);
    }
}
