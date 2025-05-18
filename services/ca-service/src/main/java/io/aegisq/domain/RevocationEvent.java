package io.aegisq.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "revocation")
@Getter @Setter @NoArgsConstructor
public class RevocationEvent {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cert_serial")          // FK column
    private Certificate certificate;

    @Column(nullable = false, length = 50)
    private String reason;

    @Column(nullable = false)
    private OffsetDateTime revokedAt = OffsetDateTime.now();
}
