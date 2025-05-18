package io.aegisq.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "certificate")
@Getter @Setter @NoArgsConstructor
public class Certificate {

    @Id
    private Long serial;                       // PK matches DB

    @Column(name = "subject_dn", nullable = false, length = 255)
    private String subjectDn;

    @Column(nullable = false)
    private OffsetDateTime notBefore;

    @Column(nullable = false)
    private OffsetDateTime notAfter;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CertificateStatus status = CertificateStatus.ISSUED;

    @Lob
    @Column(nullable = false)
    private String pem;                        // PEM blob

    @Column(nullable = false, length = 64)
    private String kid;

    @Column(nullable = false)
    private OffsetDateTime issuedAt = OffsetDateTime.now();

    /* convenience helper */
    public boolean isActive() {
        return status == CertificateStatus.ISSUED;
    }
}
