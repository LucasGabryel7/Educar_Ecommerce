package br.unitins.problem;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Representa um erro de validação em um campo específico conforme extensão do RFC 9457.
 */
@JsonPropertyOrder({"field", "message"})
public record Violation(
    String field,
    String message
) {}
