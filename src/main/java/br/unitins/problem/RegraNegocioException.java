package br.unitins.problem;

import jakarta.ws.rs.core.Response.Status;

/**
 * Exceção lançada quando uma regra de negócio ou validação semântica é violada.
 */
public class RegraNegocioException extends RuntimeException {

    private final Status status;

    public RegraNegocioException(String message) {
        super(message);
        this.status = Status.BAD_REQUEST;
    }

    public RegraNegocioException(String message, Status status) {
        super(message);
        this.status = status != null ? status : Status.BAD_REQUEST;
    }

    public Status getStatus() {
        return status;
    }
}
