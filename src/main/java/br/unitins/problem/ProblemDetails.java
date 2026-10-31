package br.unitins.problem;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Representação padronizada do formato Problem Details para APIs HTTP (RFC 9457).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"type", "title", "status", "detail", "instance", "violations"})
public class ProblemDetails {

    public static final String MEDIA_TYPE = "application/problem+json";

    private URI type;
    private String title;
    private Integer status;
    private String detail;
    private URI instance;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<Violation> violations;

    public ProblemDetails() {
    }

    public ProblemDetails(URI type, String title, Integer status, String detail, URI instance, List<Violation> violations) {
        this.type = type != null ? type : URI.create("about:blank");
        this.title = title;
        this.status = status;
        this.detail = detail;
        this.instance = instance;
        this.violations = violations != null ? new ArrayList<>(violations) : null;
    }

    public URI getType() {
        return type;
    }

    public void setType(URI type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public URI getInstance() {
        return instance;
    }

    public void setInstance(URI instance) {
        this.instance = instance;
    }

    public List<Violation> getViolations() {
        return violations;
    }

    public void setViolations(List<Violation> violations) {
        this.violations = violations != null ? new ArrayList<>(violations) : null;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private URI type = URI.create("about:blank");
        private String title;
        private Integer status;
        private String detail;
        private URI instance;
        private List<Violation> violations;

        public Builder type(URI type) {
            this.type = type;
            return this;
        }

        public Builder type(String type) {
            this.type = type != null ? URI.create(type) : null;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder status(Integer status) {
            this.status = status;
            return this;
        }

        public Builder detail(String detail) {
            this.detail = detail;
            return this;
        }

        public Builder instance(URI instance) {
            this.instance = instance;
            return this;
        }

        public Builder instance(String instance) {
            this.instance = instance != null ? URI.create(instance) : null;
            return this;
        }

        public Builder violations(List<Violation> violations) {
            this.violations = violations != null ? new ArrayList<>(violations) : null;
            return this;
        }

        public Builder addViolation(String field, String message) {
            if (this.violations == null) {
                this.violations = new ArrayList<>();
            }
            this.violations.add(new Violation(field, message));
            return this;
        }

        public ProblemDetails build() {
            return new ProblemDetails(type, title, status, detail, instance, violations);
        }
    }
}
