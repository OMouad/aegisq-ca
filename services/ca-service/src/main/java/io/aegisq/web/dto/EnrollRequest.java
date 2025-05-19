package io.aegisq.web.dto;

import java.util.Objects;

public record EnrollRequest(String csrPEM) {
    public EnrollRequest {
        Objects.requireNonNull(csrPEM, "CSR PEM content cannot be null");
    }
}